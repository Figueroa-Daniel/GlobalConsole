package org.example.globalconsole.localApps.domain.usecase

import kotlinx.coroutines.test.runTest
import kotlin.test.*
import org.example.globalconsole.localApps.domain.usecase.fakes.FakeLocalAppsRepository

/**
 * Contratos de selección, biblioteca y ciclo de vida de aplicaciones locales.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalAppsUseCasesTest {

/**
 * Verifica onlyInstalledGamesCanBeEnabled.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    @Test
    fun onlyInstalledGamesCanBeEnabled() = runTest {
        val repo = FakeLocalAppsRepository()
        assertFalse(AddLocalAppUseCase(repo)("missing"))
        assertTrue(AddLocalAppUseCase(repo)("app:test"))
        assertFalse(AddLocalAppUseCase(repo)("app:test"))
        assertTrue(IsEnabledLocalAppUseCase(repo)("app:test"))
        assertEquals(1, GetLocalAppsUseCase(repo)().size)
    }

/**
 * Verifica disablingKeepsInstalledGame.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    @Test
    fun disablingKeepsInstalledGame() = runTest {
        val repo = FakeLocalAppsRepository()
        AddLocalAppUseCase(repo)("app:test")
        assertTrue(RemoveLocalAppUseCase(repo)("app:test"))
        assertFalse(RemoveLocalAppUseCase(repo)("app:test"))
        assertTrue(GetLocalAppsUseCase(repo)().isEmpty())
        assertEquals(1, GetInstalledLocalAppsUseCase(repo)().size)
    }

/**
 * Verifica executionAndClosePropagateFailures.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    @Test
    fun executionAndClosePropagateFailures() = runTest {
        val repo = FakeLocalAppsRepository()
        val game = repo.installed.single()
        assertTrue(ExecuteLocalAppUseCase(repo)(game))
        assertEquals(game, repo.executed)
        assertTrue(CloseLocalAppUseCase(repo)())
        assertEquals(1, repo.closeCalls)
        repo.result = false
        assertFalse(ExecuteLocalAppUseCase(repo)(game))
        assertFalse(CloseLocalAppUseCase(repo)())
    }
}
