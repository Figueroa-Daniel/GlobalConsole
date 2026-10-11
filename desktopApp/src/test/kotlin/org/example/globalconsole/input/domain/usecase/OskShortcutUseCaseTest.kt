package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regresiones de los atajos del teclado externo, independientes del dispositivo.
 * @author Daniel Figueroa Vidal
 */
class OskShortcutUseCaseTest {
    /**
     * Comprueba ambos órdenes del combo, sin repetir al mantenerlo pulsado.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun combo_worksInEitherOrderAndDoesNotRepeat() {
        for (first in listOf(OskShortcutState(start = true), OskShortcutState(rightTrigger = true))) {
            val useCase = OskShortcutUseCase()
            useCase(first, false)
            val combo = OskShortcutState(start = true, rightTrigger = true)
            assertEquals(OskShortcutAction.TOGGLE, useCase(combo, false))
            assertEquals(OskShortcutAction.NONE, useCase(combo, true))
            assertEquals(OskShortcutAction.NONE, useCase(OskShortcutState(), true))
            assertEquals(OskShortcutAction.CONFIRM, useCase(OskShortcutState(start = true), true))
        }
    }

    /**
     * R3 y R1 alternan; Start solo confirma con el teclado abierto.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun alternatives_toggleWithoutConfirmingAndResetOnReturn() {
        val useCase = OskShortcutUseCase()
        assertEquals(OskShortcutAction.NONE, useCase(OskShortcutState(start = true), false))
        assertEquals(OskShortcutAction.TOGGLE, useCase(OskShortcutState(start = true, rightBumper = true), false))
        assertEquals(OskShortcutAction.NONE, useCase(OskShortcutState(), true))
        assertEquals(OskShortcutAction.TOGGLE, useCase(OskShortcutState(rightStick = true), true))
        assertEquals(OskShortcutAction.NONE, useCase(OskShortcutState(rightStick = true), false))
        useCase.reset()
        assertEquals(OskShortcutAction.TOGGLE, useCase(OskShortcutState(rightStick = true), false))
    }
}
