package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso para guardar la sensibilidad configurada del ratón.
 * 
 * @property repository Repositorio de configuración que persiste los datos.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class SaveMouseSensitivityUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Guarda la sensibilidad del ratón configurada.
     * 
     * @param speed Valor de sensibilidad (Float).
     */
    suspend operator fun invoke(speed: Float) {
        repository.saveMouseSensitivity(speed)
    }
}
