package org.example.globalconsole.melonDS.data.repositoryImpl

import org.example.globalconsole.melonDS.data.database.GameDSFileSystemAdapter
import org.example.globalconsole.melonDS.data.database.GameMelonDSAdapter
import org.example.globalconsole.melonDS.data.dto.GameDsDto
import org.example.globalconsole.melonDS.data.mappers.toDomain
import org.example.globalconsole.melonDS.data.repository.GameDSRepository
import org.example.globalconsole.melonDS.domain.entitys.GameDS

/**
 * Implementación del repositorio de juegos de Melon DS.
 * Gestiona el acceso al sistema de archivos para las ROMs y delega la ejecución al adaptador.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class GameDSRepositoryImpl(
    private val dataSourceFile: GameDSFileSystemAdapter,
    private val dataSourceDs: GameMelonDSAdapter
) : GameDSRepository {

    private var gamesInMemoryCache = mutableListOf<GameDsDto>()

    override suspend fun deleteGameDS(id: String): Boolean {
        return dataSourceFile.deleteGameInFile(id)
    }

    override suspend fun executeGameDS(id: String): Boolean {
        val gameSelected = gamesInMemoryCache.find { it.id == id }
        val executeUrl = gameSelected?.urlGameExecute
        return dataSourceDs.executeGame(executeUrl)
    }

    /**
     * Cierra el juego en ejecución llamando al adaptador nativo.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    override suspend fun closeGame(): Boolean {
        return dataSourceDs.closeProcess()
    }

    override suspend fun getGamesByName(name: String): List<GameDS> {
        return gamesInMemoryCache.filter { game ->
            game.name.contains(name, ignoreCase = true)
        }.toDomain()
    }

    override suspend fun getAllGamesDS(): List<GameDS> {
        val gamesDto = dataSourceFile.getGamesInSystemFile()
        gamesInMemoryCache = gamesDto.toMutableList()
        return gamesDto.toDomain()
    }

    override suspend fun getGameDSById(id: String): GameDS? {
        return gamesInMemoryCache.find { it.id == id }?.toDomain()
    }
}