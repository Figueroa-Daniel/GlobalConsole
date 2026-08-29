package org.example.globalconsole.duckstation.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.duckstation.data.database.GameDuckStationAdapter
import org.example.globalconsole.duckstation.data.database.GameDuckStationFileSystemAdapter
import org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository
import org.example.globalconsole.duckstation.domain.entitys.GameDuckStation

/**
 * Implementación del repositorio de juegos de DuckStation.
 * Orquesta el adaptador de sistema de archivos para el listado y
 * el adaptador nativo para la ejecución y cierre de juegos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class GameDuckStationRepositoryImpl(
    private val dataSourceFile: GameDuckStationFileSystemAdapter,
    private val dataSourceDuckStation: GameDuckStationAdapter
) : GameDuckStationRepository {

    override suspend fun deleteGameDuckStation(id: String): Boolean = withContext(Dispatchers.IO) {
        // En esta iteración no borramos archivos físicos.
        true
    }

    override suspend fun executeGameDuckStation(id: String): Boolean = withContext(Dispatchers.IO) {
        val game = getGameDuckStationById(id)
        if (game != null) {
            dataSourceDuckStation.executeGame(game.urlGameExecute)
        } else {
            false
        }
    }

    override suspend fun getGamesByName(name: String): List<GameDuckStation> = withContext(Dispatchers.IO) {
        dataSourceFile.getGamesFromDirectory().filter {
            it.name.contains(name, ignoreCase = true)
        }
    }

    override suspend fun getAllGamesDuckStation(): List<GameDuckStation> = withContext(Dispatchers.IO) {
        dataSourceFile.getGamesFromDirectory()
    }

    override suspend fun getGameDuckStationById(id: String): GameDuckStation? = withContext(Dispatchers.IO) {
        dataSourceFile.getGamesFromDirectory().find { it.id == id }
    }

    override suspend fun closeGame(): Boolean = withContext(Dispatchers.IO) {
        dataSourceDuckStation.closeProcess()
    }
}
