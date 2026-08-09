package org.example.globalconsole.juegosPcsx2.domain.usecase

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Caso de uso encargado de obtener la lista de todos los juegos de PS2 disponibles.
 *
 * @property repository Repositorio de juegos de PS2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
open class GetGamesP2UseCase(
    private val repository: GameP2Repository
) {
    /**
     * Ejecuta el caso de uso y retorna la lista de juegos de PS2.
     *
     * @return Lista completa de objetos [GameP2].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    open suspend operator fun invoke(): List<GameP2> {
        return repository.getAllGamesP2()
    }
}
