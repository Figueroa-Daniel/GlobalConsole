package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Distingue los gatillos físicos del atajo START + R2 al editar el texto pendiente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OskTextTriggerUseCaseTest {
    /**
     * Emite una edición por pulsación con mapeo estándar o HJDX.
     * @return Unit.
     * @throws AssertionError Si L2/R2 se invierten o repiten.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun physicalTriggersMovePendingCaretOnce() {
        for (guid in listOf("standard", "03000000632500002605000010010000")) {
            val swapped = guid != "standard"
            val useCase = OskTextTriggerUseCase()
            fun input(left: Boolean, right: Boolean, start: Boolean = false, active: Boolean = true) =
                useCase(guid, if (swapped) value(right) else value(left),
                    if (swapped) value(left) else value(right), start, active)
            assertEquals(null, input(false, false))
            assertEquals(KeyboardAction.TEXT_LEFT, input(true, false))
            assertEquals(null, input(true, false))
            assertEquals(KeyboardAction.TEXT_RIGHT, input(true, true))
            assertEquals(null, input(true, true))
            assertEquals(null, input(false, false))
            assertEquals(KeyboardAction.TEXT_RIGHT, input(false, true))
        }
    }

    /**
     * START + R2 no edita y al soltar START el gatillo mantenido no genera movimiento.
     * @return Unit.
     * @throws AssertionError Si el combo se filtra a la edición.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun shortcutSuppressesRightTriggerUntilReleased() {
        val useCase = OskTextTriggerUseCase()
        fun input(right: Boolean, start: Boolean, active: Boolean) =
            useCase(null, -1f, value(right), start, active)
        assertEquals(null, input(true, true, false))
        assertEquals(null, input(true, true, true))
        assertEquals(null, input(true, false, true))
        assertEquals(null, input(false, false, true))
        assertEquals(KeyboardAction.TEXT_RIGHT, input(true, false, true))
        assertEquals(null, input(true, false, false))
    }

    private fun value(pressed: Boolean): Float = if (pressed) 1f else -1f
}
