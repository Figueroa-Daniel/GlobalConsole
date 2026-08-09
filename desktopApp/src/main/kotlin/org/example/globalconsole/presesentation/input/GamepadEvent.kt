package org.example.globalconsole.presesentation.input

/**
 * Representa los distintos eventos de entrada generados por un gamepad físico.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
sealed class GamepadEvent {

    /**
     * Eventos de botones de acción del Gamepad.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    enum class Button {
        /** Botón de confirmación / acción (A en Xbox, Cruz en PlayStation). */
        CONFIRM,
        /** Botón de cancelación / volver atrás (B en Xbox, Círculo en PlayStation). */
        BACK,
        /** Botón de menú o pausa (Start / Options). */
        MENU
    }

    /**
     * Eventos de dirección generados por la cruceta (D-Pad) o el Stick analógico principal.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /**
     * Evento lanzado al pulsar un botón de acción.
     *
     * @property button Botón que ha sido presionado.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    data class ButtonPressed(val button: Button) : GamepadEvent()

    /**
     * Evento lanzado al realizar un movimiento direccional en el D-Pad o Stick.
     *
     * @property direction Dirección del movimiento.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    data class DirectionPressed(val direction: Direction) : GamepadEvent()
}
