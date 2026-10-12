package org.example.globalconsole.input.domain.usecase

/**
 * Abre el teclado al completar un clic iniciado sobre texto en una aplicación externa.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class OpenOskOnTextClickUseCase {
    private var wasPressed = false
    private var pending = false

    /**
     * Conserva el objetivo del clic hasta soltarlo y cancela contextos deshabilitados.
     * @param pressed Indica si el botón primario está pulsado.
     * @param textCursor Indica si el cursor señala texto en el instante de pulsación.
     * @param enabled Aplicación externa activa, ratón permitido y teclado oculto.
     * @return True una sola vez al liberar un clic válido.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    operator fun invoke(pressed: Boolean, textCursor: Boolean, enabled: Boolean): Boolean {
        if (!enabled) pending = false
        else if (pressed && !wasPressed) pending = textCursor
        val open = enabled && !pressed && wasPressed && pending
        wasPressed = pressed
        if (!pressed) pending = false
        return open
    }
}
