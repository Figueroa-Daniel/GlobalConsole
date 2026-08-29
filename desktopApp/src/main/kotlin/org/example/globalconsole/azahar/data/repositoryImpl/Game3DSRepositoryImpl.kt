package org.example.globalconsole.azahar.data.repositoryImpl

import org.example.globalconsole.azahar.data.database.Game3DSFileSystemAdapter
import org.example.globalconsole.azahar.data.database.Game3DSAzaharAdapter
import org.example.globalconsole.azahar.data.dto.Game3DSDto
import org.example.globalconsole.azahar.data.mappers.toDomain
import org.example.globalconsole.azahar.data.repository.Game3DSRepository
import org.example.globalconsole.azahar.domain.entitys.Game3DS

/**
 * Implementación del repositorio de juegos de Azahar (Nintendo 3DS).
 * Gestiona el acceso al sistema de archivos para las ROMs y delega la ejecución al adaptador.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class Game3DSRepositoryImpl(
    private val dataSourceFile: Game3DSFileSystemAdapter,
    private val dataSourceAzahar: Game3DSAzaharAdapter
) : Game3DSRepository {

    private var gamesInMemoryCache = mutableListOf<Game3DSDto>()

    override suspend fun deleteGame3DS(id: String): Boolean {
        return dataSourceFile.deleteGameInFile(id)
    }

    override suspend fun executeGame3DS(id: String): Boolean {
        val gameSelected = gamesInMemoryCache.find { it.id == id }
        val executeUrl = gameSelected?.urlGameExecute
        return dataSourceAzahar.executeGame(executeUrl)
    }

    /**
     * Cierra el juego en ejecución llamando al adaptador nativo.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    override suspend fun closeGame(): Boolean {
        return dataSourceAzahar.closeProcess()
    }

    override suspend fun getGamesByName(name: String): List<Game3DS> {
        return gamesInMemoryCache.filter { game ->
            game.name.contains(name, ignoreCase = true)
        }.toDomain()
    }

    override suspend fun getAllGames3DS(): List<Game3DS> {
        val gamesDto = dataSourceFile.getGamesInSystemFile()
        gamesInMemoryCache = gamesDto.toMutableList()
        return gamesDto.toDomain()
    }

    override suspend fun getGame3DSById(id: String): Game3DS? {
        return gamesInMemoryCache.find { it.id == id }?.toDomain()
    }
}
