package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.AppearanceSettings
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Recupera y normaliza las preferencias visuales sin depender de Compose.
 * @property repository Almacenamiento de preferencias.
 * @author Daniel Figueroa Vidal
 */
class GetAppearanceSettingsUseCase(private val repository: SettingsRepository) {
    /**
     * Lee valores compatibles con versiones anteriores.
     * @return AppearanceSettings.
     * @throws Exception Si falla la lectura.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    suspend operator fun invoke(): AppearanceSettings {
        val saved = repository.getAppearanceSettings()
        return saved.copy(
            accentColor = saved.accentColor.takeIf { it in AppearanceSettings.COLORS } ?: "CYAN",
            glowIntensity = saved.glowIntensity.coerceIn(0, 100)
        )
    }
}
