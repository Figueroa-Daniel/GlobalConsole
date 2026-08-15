package org.example.globalconsole.melonDS.domain.usecase

import kotlinx.coroutines.runBlocking
import org.example.globalconsole.melonDS.data.dto.MelonDSLauncherDto
import org.example.globalconsole.melonDS.data.repository.MelonDSRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class FakeMelonDSRepository : MelonDSRepository {
    var isEnabled = false
    override suspend fun executeLauncher(): Boolean = true
    override suspend fun closeLauncher(): Boolean = true
    override suspend fun executeGame(executeUrl: String?): Boolean = true
    override suspend fun isMelonDSEnabled(): Boolean = isEnabled
    override suspend fun saveMelonDSEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
    override suspend fun showMelonDSLauncher(): MelonDSLauncherDto {
        return MelonDSLauncherDto("melonds-launcher", "Melon DS Launcher", "melonds")
    }
}

class FindMelonDSLauncherUseCaseTest {
    @Test
    fun testFindMelonDSLauncher() = runBlocking {
        val repo = FakeMelonDSRepository()
        val useCase = FindMelonDSLauncherUseCase(repo)
        assertFalse(useCase())
        repo.isEnabled = true
        assertTrue(useCase())
    }
}

class EnableMelonDSLauncherUseCaseTest {
    @Test
    fun testEnableMelonDSLauncher() = runBlocking {
        val repo = FakeMelonDSRepository()
        val useCase = EnableMelonDSLauncherUseCase(repo)
        useCase()
        assertTrue(repo.isEnabled)
    }
}

class HideMelonDSLauncherUseCaseTest {
    @Test
    fun testHideMelonDSLauncher() = runBlocking {
        val repo = FakeMelonDSRepository()
        repo.isEnabled = true
        val useCase = HideMelonDSLauncherUseCase(repo)
        useCase()
        assertFalse(repo.isEnabled)
    }
}

class ShowMelonDSLauncherUseCaseTest {
    @Test
    fun testShowMelonDSLauncher() = runBlocking {
        val repo = FakeMelonDSRepository()
        val useCase = ShowMelonDSLauncherUseCase(repo)
        val launcher = useCase()
        assertEquals("melonds-launcher", launcher.id)
        assertEquals("Melon DS Launcher", launcher.name)
        assertEquals("melonds", launcher.urlGameExecute)
    }
}
