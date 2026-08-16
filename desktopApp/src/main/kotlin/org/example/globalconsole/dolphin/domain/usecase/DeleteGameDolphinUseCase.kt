package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.GameDolphinRepository

/**
 * UseCase para eliminar un juego de Dolphin de la base de datos o sistema.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class DeleteGameDolphinUseCase(private val repository: GameDolphinRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteGameDolphin(id)
    }
}
