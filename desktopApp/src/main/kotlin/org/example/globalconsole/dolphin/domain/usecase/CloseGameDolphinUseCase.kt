package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.GameDolphinRepository

/**
 * UseCase para cerrar el juego de Dolphin que esté actualmente en ejecución.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class CloseGameDolphinUseCase(private val repository: GameDolphinRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.closeGame()
    }
}
