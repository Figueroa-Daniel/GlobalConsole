package org.example.globalconsole.melonDS.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.melonDS.data.repository.MelonDSRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para el caso de uso de ejecución de un juego en Melon DS.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class ExecuteGameMelonDSUseCaseTest {

    @Test
    fun `invoke should return true when repository executes game successfully`() = runBlocking {
        // Arrange
        val repository = object : MelonDSRepository {
            override suspend fun executeLauncher(): Boolean = false
            override suspend fun closeLauncher(): Boolean = false
            override suspend fun executeGame(executeUrl: String?): Boolean = true
        }
        val useCase = ExecuteGameMelonDSUseCase(repository)

        // Act
        val result = useCase("some/url.nds")

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke should return false when repository fails to execute game`() = runBlocking {
        // Arrange
        val repository = object : MelonDSRepository {
            override suspend fun executeLauncher(): Boolean = false
            override suspend fun closeLauncher(): Boolean = false
            override suspend fun executeGame(executeUrl: String?): Boolean = false
        }
        val useCase = ExecuteGameMelonDSUseCase(repository)

        // Act
        val result = useCase("some/url.nds")

        // Assert
        assertFalse(result)
    }
}
