package org.example.globalconsole.settings.domain

/**
 * Preferencias de acento y halo; las superficies permanecen siempre oscuras.
 * @property accentColor Identificador estable del color.
 * @property glowIntensity Intensidad del halo entre 0 y 100.
 * @author Daniel Figueroa Vidal
 */
data class AppearanceSettings(
    val accentColor: String = "CYAN",
    val glowIntensity: Int = 45
) {
    companion object {
        val COLORS = listOf("CYAN", "BLUE", "VIOLET", "PINK", "AMBER", "GREEN")
    }
}
