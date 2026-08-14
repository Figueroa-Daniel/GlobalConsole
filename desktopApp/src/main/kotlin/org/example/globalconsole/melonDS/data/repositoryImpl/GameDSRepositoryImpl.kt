package org.example.globalconsole.melonDS.data.repositoryImpl

import org.example.globalconsole.melonDS.data.repository.GameDSRepository
import org.example.globalconsole.melonDS.domain.entitys.GameDS

class GameDSRepositoryImpl(): GameDSRepository {
    override suspend fun deleteGameDS(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun executeGameDS(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getGamesByName(name: String): List<GameDS> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllGamesDS(): List<GameDS> {
        TODO("Not yet implemented")
    }

    override suspend fun getGameDSById(id: String): GameDS? {
        TODO("Not yet implemented")
    }
}