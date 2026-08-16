package org.example.globalconsole.dolphin.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.dolphin.data.repository.GameDolphinRepository
import org.example.globalconsole.dolphin.domain.entitys.GameDolphin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeGameDolphinRepository : GameDolphinRepository {
    var gameExecuted = false
    var gameClosed = false
    var gameDeleted = false

    override suspend fun deleteGameDolphin(id: String): Boolean {
        gameDeleted = true
        return true
    }

    override suspend fun executeGameDolphin(id: String): Boolean {
        gameExecuted = true
        return true
    }

    override suspend fun getGamesByName(name: String): List<GameDolphin> {
        return listOf(GameDolphin("1", name, "url", null))
    }

    override suspend fun getAllGamesDolphin(): List<GameDolphin> {
        return listOf(GameDolphin("1", "Game 1", "url1", null))
    }

    override suspend fun getGameDolphinById(id: String): GameDolphin? {
        return if (id == "1") GameDolphin("1", "Game 1", "url", null) else null
    }

    override suspend fun closeGame(): Boolean {
        gameClosed = true
        return true
    }
}

class GetGamesDolphinUseCaseTest {
    @Test
    fun testGetGamesDolphin() = runBlocking {
        val repo = FakeGameDolphinRepository()
        val useCase = GetGamesDolphinUseCase(repo)
        val games = useCase()
        assertEquals(1, games.size)
        assertEquals("Game 1", games.first().name)
    }
}

class ExecuteGameDolphinUseCaseTest {
    @Test
    fun testExecuteGameDolphin() = runBlocking {
        val repo = FakeGameDolphinRepository()
        val useCase = ExecuteGameDolphinUseCase(repo)
        val result = useCase("1")
        assertTrue(result)
        assertTrue(repo.gameExecuted)
    }
}

class CloseGameDolphinUseCaseTest {
    @Test
    fun testCloseGameDolphin() = runBlocking {
        val repo = FakeGameDolphinRepository()
        val useCase = CloseGameDolphinUseCase(repo)
        val result = useCase()
        assertTrue(result)
        assertTrue(repo.gameClosed)
    }
}

class DeleteGameDolphinUseCaseTest {
    @Test
    fun testDeleteGameDolphin() = runBlocking {
        val repo = FakeGameDolphinRepository()
        val useCase = DeleteGameDolphinUseCase(repo)
        val result = useCase("1")
        assertTrue(result)
        assertTrue(repo.gameDeleted)
    }
}
