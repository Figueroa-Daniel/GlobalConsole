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
     * R3 y R1 no abren el teclado; solo START con R2 lo alterna.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Test
    fun alternativesDoNotOpenAndResetAllowsRightTriggerCombo() {
        val useCase = OskShortcutUseCase()
        for (state in listOf(OskShortcutState(start = true), OskShortcutState(rightStick = true),
            OskShortcutState(start = true, rightBumper = true), OskShortcutState(start = true, rightStick = true))) {
            useCase.reset()
            assertEquals(OskShortcutAction.NONE, useCase(state, false))
        }
        useCase.reset()
        val combo = OskShortcutState(start = true, rightTrigger = true)
        assertEquals(OskShortcutAction.TOGGLE, useCase(combo, false))
        assertEquals(OskShortcutAction.NONE, useCase(combo, true))
        useCase.reset()
        assertEquals(OskShortcutAction.TOGGLE, useCase(combo, true))
    }
}
