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
        MENU,
        /** Botón de borrado / eliminar (X en Xbox, Cuadrado en PlayStation). Usado en el OSK para borrar la última letra. */
        DELETE,
        /** Botón de sistema / central (Xbox Guide, PS Button). Usado para cerrar juegos o salir. */
        HOME,
        /** Botón de opciones / configuración (Y en Xbox, Triángulo en PlayStation). Usado para abrir el recortador de carátula. */
        OPTIONS,
        /** Bumper izquierdo (L1 / LB). Usado para avanzar página / scroll rápido arriba. */
        PAGE_UP,
        /** Bumper derecho (R1 / RB). Usado para retroceder página / scroll rápido abajo. */
        PAGE_DOWN
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
