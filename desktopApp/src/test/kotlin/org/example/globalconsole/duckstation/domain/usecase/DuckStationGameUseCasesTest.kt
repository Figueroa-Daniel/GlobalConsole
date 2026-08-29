package org.example.globalconsole.duckstation.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.duckstation.data.repository.GameDuckStationRepository
import org.example.globalconsole.duckstation.domain.entitys.GameDuckStation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeGameDuckStationRepository : GameDuckStationRepository {
    var gameExecuted = false
    var gameClosed = false
    var gameDeleted = false

    override suspend fun deleteGameDuckStation(id: String): Boolean {
        gameDeleted = true
        return true
    }

    override suspend fun executeGameDuckStation(id: String): Boolean {
        gameExecuted = true
        return true
    }

    override suspend fun getGamesByName(name: String): List<GameDuckStation> {
        return listOf(GameDuckStation("1", name, "url", null))
    }

    override suspend fun getAllGamesDuckStation(): List<GameDuckStation> {
        return listOf(GameDuckStation("1", "Game 1", "url1", null))
    }

    override suspend fun getGameDuckStationById(id: String): GameDuckStation? {
        return if (id == "1") GameDuckStation("1", "Game 1", "url", null) else null
    }

    override suspend fun closeGame(): Boolean {
        gameClosed = true
        return true
    }
}

class GetGamesDuckStationUseCaseTest {
    @Test
    fun testGetGamesDuckStation() = runBlocking {
        val repo = FakeGameDuckStationRepository()
        val useCase = GetGamesDuckStationUseCase(repo)
        val games = useCase()
        assertEquals(1, games.size)
        assertEquals("Game 1", games.first().name)
    }
}

class ExecuteGameDuckStationUseCaseTest {
    @Test
    fun testExecuteGameDuckStation() = runBlocking {
        val repo = FakeGameDuckStationRepository()
        val useCase = ExecuteGameDuckStationUseCase(repo)
        val result = useCase("1")
        assertTrue(result)
        assertTrue(repo.gameExecuted)
    }
}

class CloseGameDuckStationUseCaseTest {
    @Test
    fun testCloseGameDuckStation() = runBlocking {
        val repo = FakeGameDuckStationRepository()
        val useCase = CloseGameDuckStationUseCase(repo)
        val result = useCase()
        assertTrue(result)
        assertTrue(repo.gameClosed)
    }
}

class DeleteGameDuckStationUseCaseTest {
    @Test
    fun testDeleteGameDuckStation() = runBlocking {
        val repo = FakeGameDuckStationRepository()
        val useCase = DeleteGameDuckStationUseCase(repo)
        val result = useCase("1")
        assertTrue(result)
        assertTrue(repo.gameDeleted)
    }
}
