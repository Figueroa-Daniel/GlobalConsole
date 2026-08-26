package org.example.globalconsole.azahar.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.azahar.data.dto.AzaharLauncherDto
import org.example.globalconsole.azahar.data.repository.AzaharRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Repositorio fake para tests de visibilidad del launcher de Azahar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class FakeAzaharRepository : AzaharRepository {
    var isEnabled = false
    override suspend fun executeLauncher(): Boolean = true
    override suspend fun closeLauncher(): Boolean = true
    override suspend fun executeGame(executeUrl: String?): Boolean = true
    override suspend fun isAzaharEnabled(): Boolean = isEnabled
    override suspend fun saveAzaharEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
    override suspend fun showAzaharLauncher(): AzaharLauncherDto {
        return AzaharLauncherDto("azahar-launcher", "Azahar Launcher", "azahar")
    }
}

/**
 * Tests unitarios para el caso de uso FindAzaharLauncherUseCase.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class FindAzaharLauncherUseCaseTest {
    @Test
    fun testFindAzaharLauncher() = runBlocking {
        val repo = FakeAzaharRepository()
        val useCase = FindAzaharLauncherUseCase(repo)
        assertFalse(useCase())
        repo.isEnabled = true
        assertTrue(useCase())
    }
}

/**
 * Tests unitarios para el caso de uso EnableAzaharLauncherUseCase.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class EnableAzaharLauncherUseCaseTest {
    @Test
    fun testEnableAzaharLauncher() = runBlocking {
        val repo = FakeAzaharRepository()
        val useCase = EnableAzaharLauncherUseCase(repo)
        useCase()
        assertTrue(repo.isEnabled)
    }
}

/**
 * Tests unitarios para el caso de uso HideAzaharLauncherUseCase.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class HideAzaharLauncherUseCaseTest {
    @Test
    fun testHideAzaharLauncher() = runBlocking {
        val repo = FakeAzaharRepository()
        repo.isEnabled = true
        val useCase = HideAzaharLauncherUseCase(repo)
        useCase()
        assertFalse(repo.isEnabled)
    }
}

/**
 * Tests unitarios para el caso de uso ShowAzaharLauncherUseCase.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class ShowAzaharLauncherUseCaseTest {
    @Test
    fun testShowAzaharLauncher() = runBlocking {
        val repo = FakeAzaharRepository()
        val useCase = ShowAzaharLauncherUseCase(repo)
        val launcher = useCase()
        assertEquals("azahar-launcher", launcher.id)
        assertEquals("Azahar Launcher", launcher.name)
        assertEquals("azahar", launcher.urlGameExecute)
    }
}
