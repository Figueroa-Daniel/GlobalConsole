package org.example.globalconsole.dolphin.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.dolphin.data.dto.DolphinLauncherDto
import org.example.globalconsole.dolphin.data.repository.DolphinRepository
import org.example.globalconsole.generalDomain.entititys.Platforms
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FakeDolphinRepository : DolphinRepository {
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
    
    override suspend fun isDolphinEnabled(): Boolean = isEnabled
    
    override suspend fun saveDolphinEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
    
    override suspend fun showDolphinLauncher(): DolphinLauncherDto {
        return DolphinLauncherDto("dolphin-launcher", "Dolphin Launcher", "dolphin", null, Platforms.DOLPHIN)
    }
}

class FindDolphinLauncherUseCaseTest {
    @Test
    fun testFindDolphinLauncher() = runBlocking {
        val repo = FakeDolphinRepository()
        val useCase = FindDolphinLauncherUseCase(repo)
        assertFalse(useCase())
        repo.isEnabled = true
        assertTrue(useCase())
    }
}

class EnableDolphinLauncherUseCaseTest {
    @Test
    fun testEnableDolphinLauncher() = runBlocking {
        val repo = FakeDolphinRepository()
        val useCase = EnableDolphinLauncherUseCase(repo)
        useCase()
        assertTrue(repo.isEnabled)
    }
}

class HideDolphinLauncherUseCaseTest {
    @Test
    fun testHideDolphinLauncher() = runBlocking {
        val repo = FakeDolphinRepository()
        repo.isEnabled = true
        val useCase = HideDolphinLauncherUseCase(repo)
        useCase()
        assertFalse(repo.isEnabled)
    }
}

class ShowDolphinLauncherUseCaseTest {
    @Test
    fun testShowDolphinLauncher() = runBlocking {
        val repo = FakeDolphinRepository()
        val useCase = ShowDolphinLauncherUseCase(repo)
        val launcher = useCase()
        assertEquals("dolphin-launcher", launcher.id)
        assertEquals("Dolphin Launcher", launcher.name)
        assertEquals("dolphin", launcher.urlGameExecute)
    }
}

class ExecuteLauncherDolphinUseCaseTest {
    @Test
    fun testExecuteLauncher() = runBlocking {
        val repo = FakeDolphinRepository()
        val useCase = ExecuteLauncherDolphinUseCase(repo)
        val result = useCase()
        assertTrue(result)
        assertTrue(repo.launcherExecuted)
    }
}

class CloseLauncherDolphinUseCaseTest {
    @Test
    fun testCloseLauncher() = runBlocking {
        val repo = FakeDolphinRepository()
        val useCase = CloseLauncherDolphinUseCase(repo)
        val result = useCase()
        assertTrue(result)
        assertTrue(repo.launcherClosed)
    }
}
