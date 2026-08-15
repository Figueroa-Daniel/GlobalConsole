package org.example.globalconsole.melonDS.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.melonDS.data.repository.MelonDSRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para el caso de uso de ejecución del launcher de Melon DS.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class ExecuteLauncherMelonDSUseCaseTest {

    @Test
    fun `invoke should return true when repository executes launcher successfully`() = runBlocking {
        // Arrange
        val repository = object : MelonDSRepository {
            override suspend fun executeLauncher(): Boolean = true
            override suspend fun closeLauncher(): Boolean = false
            override suspend fun executeGame(executeUrl: String?): Boolean = false
        }
        val useCase = ExecuteLauncherMelonDSUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke should return false when repository fails to execute launcher`() = runBlocking {
        // Arrange
        val repository = object : MelonDSRepository {
            override suspend fun executeLauncher(): Boolean = false
            override suspend fun closeLauncher(): Boolean = false
            override suspend fun executeGame(executeUrl: String?): Boolean = false
        }
        val useCase = ExecuteLauncherMelonDSUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertFalse(result)
    }
}
