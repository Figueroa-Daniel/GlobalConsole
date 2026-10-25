package org.example.globalconsole.input.domain.usecase

/**
 * Convierte la lectura analógica de un gatillo en pulsado/soltado con histéresis,
 * para que reposos en -1 o 0 y ruido cerca del umbral no generen falsas pulsaciones.
 * @author Daniel Figueroa Vidal
 */
class TriggerPressUseCase {
    private var pressed = false

    /**
     * Actualiza y devuelve el estado del gatillo.
     * @param axis Valor del eje entre -1 y 1.
     * @return `true` si el gatillo se considera pulsado.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    operator fun invoke(axis: Float): Boolean {
        pressed = if (pressed) axis > RELEASE_THRESHOLD else axis > PRESS_THRESHOLD
        return pressed
    }

    /**
     * Reinicia el estado al perder el control de la entrada.
     * @return Unit.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    fun reset() {
        pressed = false
    }

    private companion object {
        const val PRESS_THRESHOLD = 0.5f
        const val RELEASE_THRESHOLD = 0.2f
    }
}
