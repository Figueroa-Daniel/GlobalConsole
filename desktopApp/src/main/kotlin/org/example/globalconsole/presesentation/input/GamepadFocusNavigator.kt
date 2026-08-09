package org.example.globalconsole.presesentation.input

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager

/**
 * Navegador de foco encargado de enlazar los eventos procedentes de [GamepadManager]
 * con la navegación espacial (Focus Management) de Jetpack Compose.
 *
 * @property focusManager Instancia de [FocusManager] obtenida de Compose.
 * @property onConfirmPressed Callback que se invoca cuando el usuario pulsa CONFIRM (Botón A).
 * @property onBackPressed Callback que se invoca cuando el usuario pulsa BACK (Botón B).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
class GamepadFocusNavigator(
    private val focusManager: FocusManager,
    private val onConfirmPressed: (() -> Unit)? = null,
    private val onBackPressed: (() -> Unit)? = null
) {

    /**
     * Procesa un evento de entrada de gamepad [GamepadEvent] y realiza la acción
     * correspondiente de navegación por foco o ejecución de callback.
     *
     * @param event Evento detectado por el motor de entrada.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    fun onGamepadEvent(event: GamepadEvent) {
        when (event) {
            is GamepadEvent.DirectionPressed -> {
                val focusDirection = when (event.direction) {
                    GamepadEvent.Direction.UP -> FocusDirection.Up
                    GamepadEvent.Direction.DOWN -> FocusDirection.Down
                    GamepadEvent.Direction.LEFT -> FocusDirection.Left
                    GamepadEvent.Direction.RIGHT -> FocusDirection.Right
                }
                focusManager.moveFocus(focusDirection)
            }
            is GamepadEvent.ButtonPressed -> {
                when (event.button) {
                    GamepadEvent.Button.CONFIRM -> onConfirmPressed?.invoke()
                    GamepadEvent.Button.BACK -> onBackPressed?.invoke()
                    GamepadEvent.Button.MENU -> { /* Reservado para futuros menús contextuales */ }
                }
            }
        }
    }
}
