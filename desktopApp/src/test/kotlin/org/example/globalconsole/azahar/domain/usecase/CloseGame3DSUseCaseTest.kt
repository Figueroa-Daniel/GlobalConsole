package org.example.globalconsole.azahar.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.azahar.data.repository.Game3DSRepository
import org.example.globalconsole.azahar.domain.entitys.Game3DS
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para el caso de uso de cierre de un juego de Azahar (3DS).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class CloseGame3DSUseCaseTest {

    @Test
    fun `invoke should return true when repository closes game successfully`() = runBlocking {
        // Arrange
        val repository = object : Game3DSRepository {
            override suspend fun deleteGame3DS(id: String): Boolean = false
            override suspend fun executeGame3DS(id: String): Boolean = false
            override suspend fun getGamesByName(name: String): List<Game3DS> = emptyList()
            override suspend fun getAllGames3DS(): List<Game3DS> = emptyList()
            override suspend fun getGame3DSById(id: String): Game3DS? = null
            override suspend fun closeGame(): Boolean = true
        }
        val useCase = CloseGame3DSUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke should return false when repository fails to close game`() = runBlocking {
        // Arrange
        val repository = object : Game3DSRepository {
            override suspend fun deleteGame3DS(id: String): Boolean = false
            override suspend fun executeGame3DS(id: String): Boolean = false
            override suspend fun getGamesByName(name: String): List<Game3DS> = emptyList()
            override suspend fun getAllGames3DS(): List<Game3DS> = emptyList()
            override suspend fun getGame3DSById(id: String): Game3DS? = null
            override suspend fun closeGame(): Boolean = false
        }
        val useCase = CloseGame3DSUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertFalse(result)
    }
}
