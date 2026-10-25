package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.SettingsRepository
import org.example.globalconsole.settings.domain.ViewSettings

/**
 * Caso de uso para obtener la configuración de vista guardada.
 *
 * @property repository Repositorio de configuración que provee los datos.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
class GetViewSettingsUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Recupera la configuración de vista guardada (o los valores por defecto).
     *
     * @return [ViewSettings] vigente.
     */
    suspend operator fun invoke(): ViewSettings {
        return repository.getViewSettings()
    }
}
