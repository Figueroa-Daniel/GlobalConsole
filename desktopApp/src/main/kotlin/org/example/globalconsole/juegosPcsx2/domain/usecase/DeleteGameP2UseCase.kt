package org.example.globalconsole.juegosPcsx2.domain.usecase

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository

/**
 * Caso de uso encargado de eliminar físicamente un juego de PS2.
 *
 * @property repository Repositorio de juegos de PS2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class DeleteGameP2UseCase(
    private val repository: GameP2Repository
) {
    /**
     * Ejecuta el caso de uso para eliminar físicamente el juego especificado por su identificador.
     *
     * @param id Identificador único del juego a eliminar.
     * @return True si el juego fue eliminado con éxito, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    suspend operator fun invoke(id: String): Boolean {
        return repository.deleteGameP2(id)
    }
}
