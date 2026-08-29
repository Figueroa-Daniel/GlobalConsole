package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.Game3DSRepository

/**
 * Caso de uso para eliminar un juego de Nintendo 3DS del almacenamiento.
 *
 * @property repository Repositorio de juegos 3DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class DeleteGame3DSUseCase(private val repository: Game3DSRepository) {
    /**
     * Elimina el juego identificado por su id.
     *
     * @param id Identificador del juego a eliminar.
     * @return True si se eliminó correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteGame3DS(id)
    }
}
