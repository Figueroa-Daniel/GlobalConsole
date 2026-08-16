package org.example.globalconsole.dolphin.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.dolphin.data.database.GameDolphinAdapter
import org.example.globalconsole.dolphin.data.database.GameDolphinFileSystemAdapter
import org.example.globalconsole.dolphin.data.repository.GameDolphinRepository
import org.example.globalconsole.dolphin.domain.entitys.GameDolphin

/**
 * Implementación del repositorio de juegos de Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class GameDolphinRepositoryImpl(
    private val dataSourceFile: GameDolphinFileSystemAdapter,
    private val dataSourceDolphin: GameDolphinAdapter
) : GameDolphinRepository {

    override suspend fun deleteGameDolphin(id: String): Boolean = withContext(Dispatchers.IO) {
        // En esta iteración no borramos archivos físicos.
        true
    }

    override suspend fun executeGameDolphin(id: String): Boolean = withContext(Dispatchers.IO) {
        val game = getGameDolphinById(id)
        if (game != null) {
            dataSourceDolphin.executeGame(game.urlGameExecute)
        } else {
            false
        }
    }

    override suspend fun getGamesByName(name: String): List<GameDolphin> = withContext(Dispatchers.IO) {
        dataSourceFile.getGamesFromDirectory().filter {
            it.name.contains(name, ignoreCase = true)
        }
    }

    override suspend fun getAllGamesDolphin(): List<GameDolphin> = withContext(Dispatchers.IO) {
        dataSourceFile.getGamesFromDirectory()
    }

    override suspend fun getGameDolphinById(id: String): GameDolphin? = withContext(Dispatchers.IO) {
        dataSourceFile.getGamesFromDirectory().find { it.id == id }
    }

    override suspend fun closeGame(): Boolean = withContext(Dispatchers.IO) {
        dataSourceDolphin.closeProcess()
    }
}
