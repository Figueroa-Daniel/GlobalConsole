package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.mappers.toDomain
import org.example.globalconsole.duckstation.data.repository.DuckStationRepository
import org.example.globalconsole.duckstation.domain.entitys.DuckStationLauncher

/**
 * UseCase para obtener la información del launcher de DuckStation para ser mostrada en UI.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class ShowDuckStationLauncherUseCase(private val repository: DuckStationRepository) {
    /**
     * Devuelve la entidad [DuckStationLauncher] con los datos del lanzador.
     *
     * @return [DuckStationLauncher] con id, nombre e imagen.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(): DuckStationLauncher {
        return repository.showDuckStationLauncher().toDomain()
    }
}
