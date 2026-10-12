package org.example.globalconsole.input.domain.usecase

/**
 * Traduce los gatillos físicos a movimientos del texto pendiente del teclado flotante.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OskTextTriggerUseCase {
    private val mapping = OskRightTriggerUseCase()
    private val leftPress = TriggerPressUseCase()
    private val rightPress = TriggerPressUseCase()
    private var previousLeft = false
    private var previousRight = false

    /**
     * Reconoce flancos sin repetir al mantener y cede prioridad al atajo START + R2.
     * @param guid Identidad del mando para corregir el mapeo HJDX.
     * @param leftAxis Eje izquierdo normalizado por GLFW.
     * @param rightAxis Eje derecho normalizado por GLFW.
     * @param start Indica que START participa en un atajo del OSK.
     * @param active Indica que el teclado flotante está abierto y admite edición.
     * @return Movimiento del texto pendiente o null.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    operator fun invoke(guid: String?, leftAxis: Float, rightAxis: Float, start: Boolean, active: Boolean): KeyboardAction? {
        val left = leftPress(mapping.left(guid, leftAxis, rightAxis))
        val right = rightPress(mapping(guid, leftAxis, rightAxis))
        val action = when {
            !active || start -> null
            right && !previousRight -> KeyboardAction.TEXT_RIGHT
            left && !previousLeft -> KeyboardAction.TEXT_LEFT
            else -> null
        }
        previousLeft = left
        previousRight = right
        return action
    }

    /**
     * Reinicia los flancos al abandonar la sesión de aplicación externa.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    fun reset() {
        leftPress.reset()
        rightPress.reset()
        previousLeft = false
        previousRight = false
    }
}
