package org.example.globalconsole.melonDS.data.repository

import org.example.globalconsole.melonDS.domain.entitys.GameDS


interface GameDSRepository {
    suspend fun deleteGameDS(id: String): Boolean
    suspend fun executeGameDS(id: String): Boolean
    suspend fun getGamesByName(name: String): List<GameDS>
    suspend fun getAllGamesDS(): List<GameDS>
    suspend fun getGameDSById(id: String): GameDS?

    /**
     * Cierra el juego de Melon DS actualmente en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend fun closeGame(): Boolean
}