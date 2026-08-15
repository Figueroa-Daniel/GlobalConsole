package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.GameDSRepository
import org.example.globalconsole.melonDS.domain.entitys.GameDS

/**
 * Caso de uso para obtener todos los juegos de Melon DS.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class GetGamesDSUseCase(private val repository: GameDSRepository) {
    suspend operator fun invoke(): List<GameDS> {
        return repository.getAllGamesDS()
    }
}
