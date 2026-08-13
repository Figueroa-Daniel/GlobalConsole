package org.example.globalconsole.presesentation.input

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGamepadState
import java.awt.GraphicsEnvironment
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Gestor del ciclo de vida de GLFW y de la lectura de eventos de gamepad físicos.
 * Ejecuta un ciclo de consulta activa (polling) en una Coroutine asíncrona dedicada,
 * detectando cambios de estado en botones y direcciones del stick izquierdo / D-Pad.
 *
 * El **stick derecho** actúa como puntero de ratón al estilo PS4 Remote Play,
 * controlado directamente a través de [java.awt.Robot] sin pasar por Compose.
 * El botón Cuadrado (X en GLFW) realiza click izquierdo del ratón.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
class GamepadManager {

    private val _events = MutableSharedFlow<GamepadEvent>(extraBufferCapacity = 64)

    /**
     * Flujo de eventos asíncronos generados por el gamepad (botones y direcciones de navegación).
     * El movimiento del ratón por stick derecho NO se emite aquí; se aplica directamente al sistema.
     */
    val events: SharedFlow<GamepadEvent> = _events.asSharedFlow()

    private var pollingJob: Job? = null
    private var isInitialized = false
    private var selectedGamepadId: Int = -1

    // Estado del ciclo anterior para detectar pulsaciones (flancos de subida)
    private var lastButtonsState = BooleanArray(GLFW_GAMEPAD_BUTTON_LAST + 1)

    // Control de repetición táctil (debouncing) para direcciones de navegación
    private var lastDirectionPressedTime = 0L
    private val directionRepeatDelayMs = 180L
    private var lastPressedDirection: GamepadEvent.Direction? = null

    // Robot de AWT para control nativo del ratón (stick derecho)
    private val awtRobot: Robot? = try { Robot() } catch (e: Exception) { null }

    /**
     * Indica si la lectura del mando está suspendida (ej. durante la ejecución de un juego).
     */
    var isSuspended: Boolean = false

    /**
     * Velocidad / sensibilidad del movimiento del ratón con el stick derecho.
     * Puede ser ajustada en tiempo real.
     */
    var mouseSensitivity: Float = 14f

    /**
     * Inicializa GLFW en segundo plano y comienza a escuchar eventos de gamepad.
     *
     * @param scope [CoroutineScope] en el que se ejecutará el bucle de polling.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     * @throws IllegalStateException si GLFW no pudo inicializarse correctamente.
     */
    fun start(scope: CoroutineScope) {
        if (isInitialized) return

        if (!glfwInit()) {
            throw IllegalStateException("No se pudo inicializar GLFW para soporte de mandos.")
        }
        isInitialized = true
        detectActiveGamepad()

        pollingJob = scope.launch(Dispatchers.IO) {
            val state = GLFWGamepadState.calloc()
            try {
                while (isActive) {
                    glfwPollEvents()

                    if (selectedGamepadId == -1 || !glfwJoystickIsGamepad(selectedGamepadId)) {
                        detectActiveGamepad()
                    }

                    if (selectedGamepadId != -1 && !isSuspended) {
                        if (glfwGetGamepadState(selectedGamepadId, state)) {
                            processGamepadState(state)
                        }
                    }

                    delay(16) // ~60Hz
                }
            } finally {
                state.free()
            }
        }
    }

    /**
     * Apaga el bucle de lectura y libera los recursos de GLFW.
     * Debe llamarse al cerrarse la ventana de la aplicación.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    fun stop() {
        pollingJob?.cancel()
        pollingJob = null
        if (isInitialized) {
            glfwTerminate()
            isInitialized = false
        }
    }

    /**
     * Busca el primer joystick conectado que sea compatible con el estándar Gamepad (mapeo SDL).
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    private fun detectActiveGamepad() {
        for (i in GLFW_JOYSTICK_1..GLFW_JOYSTICK_LAST) {
            if (glfwJoystickPresent(i) && glfwJoystickIsGamepad(i)) {
                selectedGamepadId = i
                return
            }
        }
        selectedGamepadId = -1
    }

    /**
     * Procesa el estado completo del mando en cada frame:
     * botones de acción, direcciones de navegación y movimiento del ratón por stick derecho.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    private suspend fun processGamepadState(state: GLFWGamepadState) {
        val buttons: ByteBuffer = state.buttons()
        val axes = state.axes()

        // 1. Botones de acción
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_A, GamepadEvent.Button.CONFIRM)
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_B, GamepadEvent.Button.BACK)
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_START, GamepadEvent.Button.MENU)
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_X, GamepadEvent.Button.DELETE)

        // 2. Stick derecho → ratón (estilo PS4 Remote Play)
        moveMouseWithRightStick(axes)

        // 3. Cuadrado (BUTTON_X en GLFW) → click izquierdo del ratón
        handleMouseLeftClick(buttons)

        // 4. Direcciones de navegación: D-Pad tiene prioridad sobre stick izquierdo
        var activeDirection: GamepadEvent.Direction? = null

        if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_UP).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.UP
        } else if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_DOWN).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.DOWN
        } else if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_LEFT).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.LEFT
        } else if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.RIGHT
        } else {
            val leftX = axes.get(GLFW_GAMEPAD_AXIS_LEFT_X)
            val leftY = axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y)
            val navThreshold = 0.5f

            activeDirection = when {
                leftY < -navThreshold -> GamepadEvent.Direction.UP
                leftY > navThreshold -> GamepadEvent.Direction.DOWN
                leftX < -navThreshold -> GamepadEvent.Direction.LEFT
                leftX > navThreshold -> GamepadEvent.Direction.RIGHT
                else -> null
            }
        }

        // Debouncing de dirección: emite el primer evento inmediatamente y repite después de un retardo
        val currentTime = System.currentTimeMillis()
        if (activeDirection != null) {
            if (activeDirection != lastPressedDirection ||
                (currentTime - lastDirectionPressedTime) >= directionRepeatDelayMs
            ) {
                _events.emit(GamepadEvent.DirectionPressed(activeDirection))
                lastDirectionPressedTime = currentTime
                lastPressedDirection = activeDirection
            }
        } else {
            lastPressedDirection = null
        }
    }

    /**
     * Mueve el cursor del sistema usando el stick analógico derecho del mando.
     * Aplica escalado cuadrático para mayor precisión cerca del centro y
     * mayor velocidad al empujar el stick hasta el extremo.
     *
     * @param axes Buffer de ejes GLFW del mando.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    private fun moveMouseWithRightStick(axes: java.nio.FloatBuffer) {
        val rightX = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_X)
        val rightY = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_Y)
        val deadZone = 0.12f

        if (abs(rightX) < deadZone && abs(rightY) < deadZone) return

        // Escalado cuadrático: suave en el centro, rápido en el extremo
        val scaledX = rightX * abs(rightX) * mouseSensitivity
        val scaledY = rightY * abs(rightY) * mouseSensitivity

        val loc = MouseInfo.getPointerInfo()?.location ?: return
        
        // Calcular los límites de todos los monitores para permitir movimiento global
        var virtualBounds = java.awt.Rectangle()
        for (gs in GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices) {
            virtualBounds = virtualBounds.union(gs.defaultConfiguration.bounds)
        }

        awtRobot?.mouseMove(
            (loc.x + scaledX).toInt().coerceIn(virtualBounds.x, virtualBounds.x + virtualBounds.width - 1),
            (loc.y + scaledY).toInt().coerceIn(virtualBounds.y, virtualBounds.y + virtualBounds.height - 1)
        )
    }

    /**
     * Detecta la pulsación del botón Cuadrado (GLFW_GAMEPAD_BUTTON_X) y ejecuta
     * un click izquierdo del ratón en la posición actual del cursor.
     *
     * @param buttons Buffer de botones GLFW del mando.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    private fun handleMouseLeftClick(buttons: ByteBuffer) {
        val isPressed = buttons.get(GLFW_GAMEPAD_BUTTON_X).toInt() == GLFW_PRESS
        val wasPressed = lastButtonsState[GLFW_GAMEPAD_BUTTON_X]

        if (isPressed && !wasPressed) {
            awtRobot?.let {
                it.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                it.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
            }
        }
        lastButtonsState[GLFW_GAMEPAD_BUTTON_X] = isPressed
    }

    /**
     * Compara el estado del botón actual con el anterior para emitir el evento
     * solo en el flanco de subida (pressed, not held).
     *
     * @param buttons Buffer de botones GLFW.
     * @param glfwButtonId Identificador del botón a comprobar.
     * @param eventButton Evento de [GamepadEvent.Button] a emitir.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    private suspend fun checkButtonPress(
        buttons: ByteBuffer,
        glfwButtonId: Int,
        eventButton: GamepadEvent.Button
    ) {
        val isPressed = buttons.get(glfwButtonId).toInt() == GLFW_PRESS
        val wasPressed = lastButtonsState[glfwButtonId]

        if (isPressed && !wasPressed) {
            _events.emit(GamepadEvent.ButtonPressed(eventButton))
        }
        lastButtonsState[glfwButtonId] = isPressed
    }
}
