package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.GameDSRepository

/**
 * Caso de uso para eliminar un juego de Melon DS del almacenamiento.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class DeleteGameDSUseCase(private val repository: GameDSRepository) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteGameDS(id)
    }
}
