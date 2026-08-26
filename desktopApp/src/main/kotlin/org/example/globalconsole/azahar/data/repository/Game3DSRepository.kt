package org.example.globalconsole.azahar.data.repository

import org.example.globalconsole.azahar.domain.entitys.Game3DS

/**
 * Repositorio para la gestión de juegos de Nintendo 3DS (Azahar).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
interface Game3DSRepository {
    suspend fun deleteGame3DS(id: String): Boolean
    suspend fun executeGame3DS(id: String): Boolean
    suspend fun getGamesByName(name: String): List<Game3DS>
    suspend fun getAllGames3DS(): List<Game3DS>
    suspend fun getGame3DSById(id: String): Game3DS?

    /**
     * Cierra el juego de Azahar actualmente en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend fun closeGame(): Boolean
}
