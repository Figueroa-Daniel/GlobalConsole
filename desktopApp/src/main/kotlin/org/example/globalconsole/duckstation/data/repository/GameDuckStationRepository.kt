package org.example.globalconsole.duckstation.data.repository

import org.example.globalconsole.duckstation.domain.entitys.GameDuckStation

/**
 * Repositorio para la gestión de juegos de DuckStation (PlayStation 1).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
interface GameDuckStationRepository {
    suspend fun deleteGameDuckStation(id: String): Boolean
    suspend fun executeGameDuckStation(id: String): Boolean
    suspend fun getGamesByName(name: String): List<GameDuckStation>
    suspend fun getAllGamesDuckStation(): List<GameDuckStation>
    suspend fun getGameDuckStationById(id: String): GameDuckStation?

    /**
     * Cierra el juego de DuckStation actualmente en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend fun closeGame(): Boolean
}
