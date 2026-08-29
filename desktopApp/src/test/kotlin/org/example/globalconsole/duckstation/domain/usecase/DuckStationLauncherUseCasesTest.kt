package org.example.globalconsole.duckstation.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.duckstation.data.dto.DuckStationLauncherDto
import org.example.globalconsole.duckstation.data.repository.DuckStationRepository
import org.example.globalconsole.generalDomain.entititys.Platforms
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FakeDuckStationRepository : DuckStationRepository {
    var isEnabled = false
    var launcherExecuted = false
    var launcherClosed = false

    override suspend fun executeLauncher(): Boolean {
        launcherExecuted = true
        return true
    }

    override suspend fun closeLauncher(): Boolean {
        launcherClosed = true
        return true
    }

    override suspend fun executeGame(executeUrl: String?): Boolean = true

    override suspend fun isDuckStationEnabled(): Boolean = isEnabled

    override suspend fun saveDuckStationEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    override suspend fun showDuckStationLauncher(): DuckStationLauncherDto {
        return DuckStationLauncherDto("duckstation-launcher", "DuckStation Launcher", "duckstation", null, Platforms.DUCKSTATION)
    }
}

class FindDuckStationLauncherUseCaseTest {
    @Test
    fun testFindDuckStationLauncher() = runBlocking {
        val repo = FakeDuckStationRepository()
        val useCase = FindDuckStationLauncherUseCase(repo)
        assertFalse(useCase())
        repo.isEnabled = true
        assertTrue(useCase())
    }
}

class EnableDuckStationLauncherUseCaseTest {
    @Test
    fun testEnableDuckStationLauncher() = runBlocking {
        val repo = FakeDuckStationRepository()
        val useCase = EnableDuckStationLauncherUseCase(repo)
        useCase()
        assertTrue(repo.isEnabled)
    }
}

class HideDuckStationLauncherUseCaseTest {
    @Test
    fun testHideDuckStationLauncher() = runBlocking {
        val repo = FakeDuckStationRepository()
        repo.isEnabled = true
        val useCase = HideDuckStationLauncherUseCase(repo)
        useCase()
        assertFalse(repo.isEnabled)
    }
}

class ShowDuckStationLauncherUseCaseTest {
    @Test
    fun testShowDuckStationLauncher() = runBlocking {
        val repo = FakeDuckStationRepository()
        val useCase = ShowDuckStationLauncherUseCase(repo)
        val launcher = useCase()
        assertEquals("duckstation-launcher", launcher.id)
        assertEquals("DuckStation Launcher", launcher.name)
        assertEquals("duckstation", launcher.urlGameExecute)
    }
}

class ExecuteLauncherDuckStationUseCaseTest {
    @Test
    fun testExecuteLauncher() = runBlocking {
        val repo = FakeDuckStationRepository()
        val useCase = ExecuteLauncherDuckStationUseCase(repo)
        val result = useCase()
        assertTrue(result)
        assertTrue(repo.launcherExecuted)
    }
}

class CloseLauncherDuckStationUseCaseTest {
    @Test
    fun testCloseLauncher() = runBlocking {
        val repo = FakeDuckStationRepository()
        val useCase = CloseLauncherDuckStationUseCase(repo)
        val result = useCase()
        assertTrue(result)
        assertTrue(repo.launcherClosed)
    }
}
