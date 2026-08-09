package org.example.globalconsole.presesentation.input

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Fake de [FocusManager] para interceptar y registrar los movimientos de foco
 * generados por las pruebas.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
class FakeFocusManager : FocusManager {
    var lastMovedDirection: FocusDirection? = null
    var clearFocusCalled = false

    override fun clearFocus(force: Boolean) {
        clearFocusCalled = true
    }

    override fun moveFocus(direction: FocusDirection): Boolean {
        lastMovedDirection = direction
        return true
    }
}

/**
 * Pruebas unitarias para el traductor de eventos de gamepad [GamepadFocusNavigator].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
class GamepadFocusNavigatorTest {

    private val fakeFocusManager = FakeFocusManager()

    /**
     * Verifica que los eventos direccionales del mando se correspondan
     * con los movimientos de foco espacial nativos de Compose.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    @Test
    fun onGamepadEvent_directions_moveFocusCorrectly() {
        val navigator = GamepadFocusNavigator(fakeFocusManager)

        navigator.onGamepadEvent(GamepadEvent.DirectionPressed(GamepadEvent.Direction.UP))
        assertEquals(FocusDirection.Up, fakeFocusManager.lastMovedDirection)

        navigator.onGamepadEvent(GamepadEvent.DirectionPressed(GamepadEvent.Direction.DOWN))
        assertEquals(FocusDirection.Down, fakeFocusManager.lastMovedDirection)

        navigator.onGamepadEvent(GamepadEvent.DirectionPressed(GamepadEvent.Direction.LEFT))
        assertEquals(FocusDirection.Left, fakeFocusManager.lastMovedDirection)

        navigator.onGamepadEvent(GamepadEvent.DirectionPressed(GamepadEvent.Direction.RIGHT))
        assertEquals(FocusDirection.Right, fakeFocusManager.lastMovedDirection)
    }

    /**
     * Verifica que al pulsar el botón de confirmación se invoque el callback adecuado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    @Test
    fun onGamepadEvent_confirmButton_triggersCallback() {
        var confirmCalled = false
        val navigator = GamepadFocusNavigator(
            focusManager = fakeFocusManager,
            onConfirmPressed = { confirmCalled = true }
        )

        navigator.onGamepadEvent(GamepadEvent.ButtonPressed(GamepadEvent.Button.CONFIRM))
        assertTrue(confirmCalled, "El callback de confirmación no fue invocado al presionar CONFIRM")
    }

    /**
     * Verifica que al pulsar el botón de cancelar / atrás se invoque el callback de retroceso.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    @Test
    fun onGamepadEvent_backButton_triggersCallback() {
        var backCalled = false
        val navigator = GamepadFocusNavigator(
            focusManager = fakeFocusManager,
            onBackPressed = { backCalled = true }
        )

        navigator.onGamepadEvent(GamepadEvent.ButtonPressed(GamepadEvent.Button.BACK))
        assertTrue(backCalled, "El callback de volver atrás no fue invocado al presionar BACK")
    }
}
