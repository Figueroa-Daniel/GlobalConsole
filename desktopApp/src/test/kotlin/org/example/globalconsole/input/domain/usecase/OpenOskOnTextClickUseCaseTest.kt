package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Valida apertura por clic en texto sin introducir atajos alternativos.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OpenOskOnTextClickUseCaseTest {
    /**
     * Abre una vez al soltar y conserva la detección realizada al pulsar.
     * @return Unit.
     * @throws AssertionError Si abre antes de enfocar el campo o repite.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun opensOnceAfterReleasingTextClick() {
        val useCase = OpenOskOnTextClickUseCase()
        assertFalse(useCase(true, true, true))
        assertFalse(useCase(true, false, true))
        assertTrue(useCase(false, false, true))
        assertFalse(useCase(false, true, true))
    }

    /**
     * Clics fuera de texto o fuera del contexto externo no abren el teclado.
     * @return Unit.
     * @throws AssertionError Si un clic no válido abre el teclado.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun ignoresNonTextAndCancelledContext() {
        val useCase = OpenOskOnTextClickUseCase()
        repeat(2) {
            assertFalse(useCase(true, false, true))
            assertFalse(useCase(false, false, true))
        }
        assertFalse(useCase(true, true, true))
        assertFalse(useCase(true, true, false))
        assertFalse(useCase(false, true, true))
        assertFalse(useCase(true, true, false))
        assertFalse(useCase(false, true, true))
    }
}
