package org.example.globalconsole.input.domain.usecase

/**
 * Traduce L1 y R1 a movimientos del cursor del campo externo mientras el OSK está activo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OskExternalCursorUseCase {
    private var previousLeft = false
    private var previousRight = false

    /**
     * Emite una sola acción por flanco y descarta entradas fuera del teclado externo.
     * @param left Indica si L1 está pulsado.
     * @param right Indica si R1 está pulsado.
     * @param active Indica que el teclado flotante de una aplicación externa recibe el mando.
     * @return Dirección a inyectar en el campo enfocado o NONE.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    operator fun invoke(left: Boolean, right: Boolean, active: Boolean): OskExternalCursorAction {
        if (!active) {
            reset()
            return OskExternalCursorAction.NONE
        }
        val action = when {
            right && !previousRight -> OskExternalCursorAction.RIGHT
            left && !previousLeft -> OskExternalCursorAction.LEFT
            else -> OskExternalCursorAction.NONE
        }
        previousLeft = left
        previousRight = right
        return action
    }

    /**
     * Olvida botones mantenidos al cerrar el teclado flotante.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    fun reset() {
        previousLeft = false
        previousRight = false
    }
}

/**
 * Movimiento solicitado para el cursor del campo de texto externo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
enum class OskExternalCursorAction { NONE, LEFT, RIGHT }
