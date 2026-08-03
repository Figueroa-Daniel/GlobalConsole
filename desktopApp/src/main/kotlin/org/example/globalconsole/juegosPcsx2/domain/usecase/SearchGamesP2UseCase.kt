package org.example.globalconsole.juegosPcsx2.domain.usecase

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Caso de uso encargado de buscar juegos de PS2 que coincidan con un criterio de búsqueda.
 *
 * @property repository Repositorio de juegos de PS2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class SearchGamesP2UseCase(
    private val repository: GameP2Repository
) {
    /**
     * Ejecuta la búsqueda filtrando los juegos cuyo nombre contenga el criterio especificado.
     *
     * @param name Criterio o subcadena de búsqueda.
     * @return Lista de entidades [GameP2] que coinciden con la búsqueda.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    suspend operator fun invoke(name: String): List<GameP2> {
        return repository.getGamesByName(name)
    }
}
