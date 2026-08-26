package org.example.globalconsole.azahar.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.azahar.data.repository.AzaharRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para el caso de uso de cierre del launcher de Azahar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class CloseLauncherAzaharUseCaseTest {

    @Test
    fun `invoke should return true when repository closes launcher successfully`() = runBlocking {
        // Arrange
        val repository = object : AzaharRepository {
            override suspend fun executeLauncher(): Boolean = false
            override suspend fun closeLauncher(): Boolean = true
            override suspend fun executeGame(executeUrl: String?): Boolean = false
            override suspend fun isAzaharEnabled(): Boolean = false
            override suspend fun saveAzaharEnabled(enabled: Boolean) {}
            override suspend fun showAzaharLauncher() = org.example.globalconsole.azahar.data.dto.AzaharLauncherDto("", "", "")
        }
        val useCase = CloseLauncherAzaharUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke should return false when repository fails to close launcher`() = runBlocking {
        // Arrange
        val repository = object : AzaharRepository {
            override suspend fun executeLauncher(): Boolean = false
            override suspend fun closeLauncher(): Boolean = false
            override suspend fun executeGame(executeUrl: String?): Boolean = false
            override suspend fun isAzaharEnabled(): Boolean = false
            override suspend fun saveAzaharEnabled(enabled: Boolean) {}
            override suspend fun showAzaharLauncher() = org.example.globalconsole.azahar.data.dto.AzaharLauncherDto("", "", "")
        }
        val useCase = CloseLauncherAzaharUseCase(repository)

        // Act
        val result = useCase()

        // Assert
        assertFalse(result)
    }
}
