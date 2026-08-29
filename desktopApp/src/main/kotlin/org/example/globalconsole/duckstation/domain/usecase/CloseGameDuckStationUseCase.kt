package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository

/**
 * UseCase para cerrar el juego de DuckStation actualmente en ejecución.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class CloseGameDuckStationUseCase(private val repository: GameDuckStationRepository) {
    /**
     * Fuerza el cierre del proceso de DuckStation en ejecución.
     *
     * @return True si se cerró correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeGame()
    }
}
