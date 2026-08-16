package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.GameDolphinRepository
import org.example.globalconsole.dolphin.domain.entitys.GameDolphin

/**
 * UseCase para obtener la lista de juegos de Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class GetGamesDolphinUseCase(private val repository: GameDolphinRepository) {
    suspend operator fun invoke(): List<GameDolphin> {
        return repository.getAllGamesDolphin()
    }
}
