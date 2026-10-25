package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifica el movimiento del cursor externo desde los botones superiores del mando.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OskExternalCursorUseCaseTest {
    /**
     * Emite una flecha por pulsación y ninguna por mantener o liberar el botón.
     * @return Unit.
     * @throws AssertionError Si se repite o se invierte el sentido.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun bumpersMoveExternalCaretOnlyOnPressEdge() {
        val useCase = OskExternalCursorUseCase()
        assertEquals(OskExternalCursorAction.NONE, useCase(false, false, true))
        assertEquals(OskExternalCursorAction.LEFT, useCase(true, false, true))
        assertEquals(OskExternalCursorAction.NONE, useCase(true, false, true))
        assertEquals(OskExternalCursorAction.RIGHT, useCase(true, true, true))
        assertEquals(OskExternalCursorAction.NONE, useCase(true, true, true))
        assertEquals(OskExternalCursorAction.NONE, useCase(false, false, true))
        assertEquals(OskExternalCursorAction.RIGHT, useCase(false, true, true))
        assertEquals(OskExternalCursorAction.NONE, useCase(false, false, true))
        assertEquals(OskExternalCursorAction.LEFT, useCase(true, false, true))
    }

    /**
     * Ignora botones sin teclado externo y reinicia los flancos al cerrarlo.
     * @return Unit.
     * @throws AssertionError Si afecta a la biblioteca o conserva un flanco anterior.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun inactiveOverlayDoesNotMoveCaret() {
        val useCase = OskExternalCursorUseCase()
        assertEquals(OskExternalCursorAction.NONE, useCase(true, false, false))
        assertEquals(OskExternalCursorAction.NONE, useCase(false, true, false))
        assertEquals(OskExternalCursorAction.LEFT, useCase(true, false, true))
        assertEquals(OskExternalCursorAction.NONE, useCase(false, false, false))
        assertEquals(OskExternalCursorAction.RIGHT, useCase(false, true, true))
    }
}
