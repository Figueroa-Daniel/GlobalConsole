package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.DuckStationRepository

/**
 * UseCase para habilitar el launcher de DuckStation en la configuración.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class EnableDuckStationLauncherUseCase(private val repository: DuckStationRepository) {
    /**
     * Persiste el estado habilitado de DuckStation.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke() {
        repository.saveDuckStationEnabled(true)
    }
}
