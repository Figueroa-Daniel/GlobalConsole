package org.example.globalconsole.input.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Comprueba el atajo con R2 físico en mandos estándar y HJDX sin admitir L2.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OskRightTriggerUseCaseTest {
    /**
     * Recorre el mapeo analógico, la histéresis y el atajo sin depender de GLFW.
     * @return Unit.
     * @throws AssertionError Si L2 abre el teclado o R2 no lo abre.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    @Test
    fun onlyPhysicalRightTriggerOpensForBothMappings() {
        for (guid in listOf(null, "standard", "03000000632500002605000010010000")) {
            val swapped = guid == "03000000632500002605000010010000"
            val resolver = OskRightTriggerUseCase()
            val press = TriggerPressUseCase()
            val shortcut = OskShortcutUseCase()
            val leftOnly = resolver(guid, if (swapped) -1f else 1f, if (swapped) 1f else -1f)
            assertEquals(OskShortcutAction.NONE, shortcut(OskShortcutState(start = true, rightTrigger = press(leftOnly)), false))
            val rightOnly = resolver(guid, if (swapped) 1f else -1f, if (swapped) -1f else 1f)
            val state = OskShortcutState(start = true, rightTrigger = press(rightOnly))
            assertEquals(OskShortcutAction.TOGGLE, shortcut(state, false))
            assertEquals(OskShortcutAction.NONE, shortcut(state, true))
        }
    }
}
