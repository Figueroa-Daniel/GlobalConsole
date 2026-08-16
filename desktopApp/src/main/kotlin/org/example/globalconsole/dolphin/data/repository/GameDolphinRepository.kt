package org.example.globalconsole.dolphin.data.repository

import org.example.globalconsole.dolphin.domain.entitys.GameDolphin

/**
 * Repositorio para la gestión de juegos de Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
interface GameDolphinRepository {
    suspend fun deleteGameDolphin(id: String): Boolean
    suspend fun executeGameDolphin(id: String): Boolean
    suspend fun getGamesByName(name: String): List<GameDolphin>
    suspend fun getAllGamesDolphin(): List<GameDolphin>
    suspend fun getGameDolphinById(id: String): GameDolphin?

    /**
     * Cierra el juego de Dolphin actualmente en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-16
     */
    suspend fun closeGame(): Boolean
}
