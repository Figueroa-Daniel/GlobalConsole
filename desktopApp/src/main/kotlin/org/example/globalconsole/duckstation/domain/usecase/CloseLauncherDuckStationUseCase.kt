package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.DuckStationRepository

/**
 * UseCase para cerrar el launcher de DuckStation.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class CloseLauncherDuckStationUseCase(private val repository: DuckStationRepository) {
    /**
     * Fuerza el cierre del proceso del lanzador de DuckStation.
     *
     * @return True si se cerró correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeLauncher()
    }
}
