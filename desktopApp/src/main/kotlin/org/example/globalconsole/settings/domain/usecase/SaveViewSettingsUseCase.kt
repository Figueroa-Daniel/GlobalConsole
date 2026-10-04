package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso para guardar la configuración de vista (agrupación y posición de launchers).
 *
 * @property repository Repositorio de configuración que persiste los datos.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
class SaveViewSettingsUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Guarda la configuración de vista.
     *
     * @param groupingMode Nombre del modo de agrupación.
     * @param launcherPosition Nombre de la posición de launchers.
     */
    suspend operator fun invoke(groupingMode: String, launcherPosition: String) {
        repository.saveViewSettings(groupingMode, launcherPosition)
    }
}
