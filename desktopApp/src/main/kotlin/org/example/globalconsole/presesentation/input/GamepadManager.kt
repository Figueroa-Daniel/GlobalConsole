package org.example.globalconsole.presesentation.input

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGamepadState
import java.nio.ByteBuffer

/**
 * Gestor del ciclo de vida de GLFW y de la lectura de eventos de gamepad físicos.
 * Ejecuta un ciclo de consulta activa (polling) en una Coroutine asíncrona dedicada,
 * detectando cambios de estado en botones y direcciones.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
class GamepadManager {

    private val _events = MutableSharedFlow<GamepadEvent>(extraBufferCapacity = 64)
    
    /**
     * Flujo de eventos asíncronos generados por el gamepad.
     * Puede ser recolectado en Compose usando `collectAsState` o `LaunchedEffect`.
     */
    val events: SharedFlow<GamepadEvent> = _events.asSharedFlow()

    private var pollingJob: Job? = null
    private var isInitialized = false
    private var selectedGamepadId: Int = -1

    // Estado del ciclo anterior para detectar pulsaciones (flancos de subida)
    private var lastButtonsState = BooleanArray(GLFW_GAMEPAD_BUTTON_LAST + 1)
    
    // Control de repetición táctil (debouncing) para direcciones
    private var lastDirectionPressedTime = 0L
    private val directionRepeatDelayMs = 200L
    private var lastPressedDirection: GamepadEvent.Direction? = null

    /**
     * Inicializa GLFW en segundo plano y comienza a escuchar eventos de gamepad.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     * @throws IllegalStateException si no se pudo inicializar GLFW.
     */
    fun start(scope: CoroutineScope) {
        if (isInitialized) return

        // Inicializamos GLFW sin crear ventanas
        // GLFW requiere ejecutarse en el hilo principal o hilos específicos según plataforma,
        // pero el polling de estado de gamepad (glfwGetGamepadState) es seguro desde coroutines en la JVM.
        if (!glfwInit()) {
            throw IllegalStateException("No se pudo inicializar GLFW para soporte de mandos.")
        }
        isInitialized = true
        detectActiveGamepad()

        pollingJob = scope.launch(Dispatchers.IO) {
            val state = GLFWGamepadState.calloc()
            try {
                while (isActive) {
                    glfwPollEvents() // Actualiza el estado interno de GLFW
                    
                    if (selectedGamepadId == -1 || !glfwJoystickIsGamepad(selectedGamepadId)) {
                        detectActiveGamepad()
                    }

                    if (selectedGamepadId != -1) {
                        if (glfwGetGamepadState(selectedGamepadId, state)) {
                            processGamepadState(state)
                        }
                    }
                    
                    delay(16) // Equivale a aprox. 60Hz de tasa de refresco
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
     * Procesa el estado de botones y ejes devuelto por GLFW en cada frame de consulta.
     */
    private suspend fun processGamepadState(state: GLFWGamepadState) {
        val buttons: ByteBuffer = state.buttons()
        
        // 1. Procesar Botones de Acción (Confirmar, Cancelar, Menú)
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_A, GamepadEvent.Button.CONFIRM)
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_B, GamepadEvent.Button.BACK)
        checkButtonPress(buttons, GLFW_GAMEPAD_BUTTON_START, GamepadEvent.Button.MENU)

        // 2. Procesar Direcciones (D-pad o Ejes analógicos)
        var activeDirection: GamepadEvent.Direction? = null

        // D-Pad tiene prioridad
        if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_UP).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.UP
        } else if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_DOWN).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.DOWN
        } else if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_LEFT).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.LEFT
        } else if (buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT).toInt() == GLFW_PRESS) {
            activeDirection = GamepadEvent.Direction.RIGHT
        } else {
            // Si la cruceta no está activa, consultamos el Stick Analógico Izquierdo
            val axis = state.axes()
            val leftX = axis.get(GLFW_GAMEPAD_AXIS_LEFT_X)
            val leftY = axis.get(GLFW_GAMEPAD_AXIS_LEFT_Y)
            val threshold = 0.5f // Zona muerta para evitar lecturas fantasmas

            if (leftY < -threshold) {
                activeDirection = GamepadEvent.Direction.UP
            } else if (leftY > threshold) {
                activeDirection = GamepadEvent.Direction.DOWN
            } else if (leftX < -threshold) {
                activeDirection = GamepadEvent.Direction.LEFT
            } else if (leftX > threshold) {
                activeDirection = GamepadEvent.Direction.RIGHT
            }
        }

        // Lógica de repetición del movimiento direccional (debouncing)
        val currentTime = System.currentTimeMillis()
        if (activeDirection != null) {
            if (activeDirection != lastPressedDirection || (currentTime - lastDirectionPressedTime) >= directionRepeatDelayMs) {
                _events.emit(GamepadEvent.DirectionPressed(activeDirection))
                lastDirectionPressedTime = currentTime
                lastPressedDirection = activeDirection
            }
        } else {
            lastPressedDirection = null
        }
    }

    /**
     * Compara el estado del botón actual con el anterior para emitir el evento solo en el flanco de subida.
     */
    private suspend fun checkButtonPress(buttons: ByteBuffer, glfwButtonId: Int, eventButton: GamepadEvent.Button) {
        val isPressed = buttons.get(glfwButtonId).toInt() == GLFW_PRESS
        val wasPressed = lastButtonsState[glfwButtonId]
        
        if (isPressed && !wasPressed) {
            _events.emit(GamepadEvent.ButtonPressed(eventButton))
        }
        lastButtonsState[glfwButtonId] = isPressed
    }
}
