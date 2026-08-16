package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.GameDolphinRepository

/**
 * UseCase para ejecutar un juego específico de Dolphin mediante su ID.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class ExecuteGameDolphinUseCase(private val repository: GameDolphinRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.executeGameDolphin(id)
    }
}
