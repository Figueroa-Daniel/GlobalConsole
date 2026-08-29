package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository

/**
 * UseCase para eliminar un juego de la biblioteca de DuckStation.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class DeleteGameDuckStationUseCase(private val repository: GameDuckStationRepository) {
    /**
     * Elimina el juego identificado por [id] de la biblioteca.
     *
     * @param id Identificador único del juego.
     * @return True si se eliminó correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteGameDuckStation(id)
    }
}
