package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso para obtener la sensibilidad configurada del ratón.
 * 
 * @property repository Repositorio de configuración que provee los datos.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class GetMouseSensitivityUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Recupera la sensibilidad del ratón configurada.
     * 
     * @return Valor de sensibilidad (Float).
     */
    suspend operator fun invoke(): Float {
        return repository.getMouseSensitivity()
    }
}
