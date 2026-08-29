package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository

/**
 * UseCase para ejecutar un juego específico de DuckStation mediante su ID.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class ExecuteGameDuckStationUseCase(private val repository: GameDuckStationRepository) {
    /**
     * Lanza el juego identificado por [id].
     *
     * @param id Identificador único del juego.
     * @return True si el proceso se inició correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(id: String): Boolean {
        return repository.executeGameDuckStation(id)
    }
}
