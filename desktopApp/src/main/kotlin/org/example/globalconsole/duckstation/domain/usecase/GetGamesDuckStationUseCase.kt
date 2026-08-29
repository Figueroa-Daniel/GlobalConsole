package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository
import org.example.globalconsole.duckstation.domain.entitys.GameDuckStation

/**
 * UseCase para obtener todos los juegos de DuckStation disponibles en el directorio configurado.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class GetGamesDuckStationUseCase(private val repository: GameDuckStationRepository) {
    /**
     * Devuelve la lista completa de juegos de PlayStation 1 encontrados.
     *
     * @return Lista de [GameDuckStation].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(): List<GameDuckStation> {
        return repository.getAllGamesDuckStation()
    }
}
