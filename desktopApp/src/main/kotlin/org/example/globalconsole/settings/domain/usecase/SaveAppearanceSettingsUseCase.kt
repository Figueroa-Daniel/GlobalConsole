package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.AppearanceSettings
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Valida y persiste las preferencias visuales sin depender de Compose.
 * @property repository Almacenamiento de preferencias.
 * @author Daniel Figueroa Vidal
 */
class SaveAppearanceSettingsUseCase(private val repository: SettingsRepository) {
    /**
     * Guarda una selección válida de apariencia.
     * @param settings Preferencias elegidas.
     * @return Unit.
     * @throws IllegalArgumentException Si el color o brillo es inválido.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-04
     */
    suspend operator fun invoke(settings: AppearanceSettings): Unit {
        require(settings.accentColor in AppearanceSettings.COLORS) { "Color de acento desconocido" }
        require(settings.glowIntensity in 0..100) { "El brillo debe estar entre 0 y 100" }
        repository.saveAppearanceSettings(settings)
    }
}
