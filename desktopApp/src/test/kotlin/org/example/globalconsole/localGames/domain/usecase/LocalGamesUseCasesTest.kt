package org.example.globalconsole.localGames.domain.usecase

import kotlinx.coroutines.test.runTest
import kotlin.test.*
import org.example.globalconsole.localGames.domain.usecase.fakes.FakeLocalGamesRepository

/**
 * Contratos de selección, biblioteca y ciclo de vida de juegos locales.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalGamesUseCasesTest {

/**
 * Verifica onlyInstalledGamesCanBeEnabled.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    @Test
    fun onlyInstalledGamesCanBeEnabled() = runTest {
        val repo = FakeLocalGamesRepository()
        assertFalse(AddLocalGameUseCase(repo)("missing"))
        assertTrue(AddLocalGameUseCase(repo)("local:test"))
        assertFalse(AddLocalGameUseCase(repo)("local:test"))
        assertTrue(IsEnabledLocalGameUseCase(repo)("local:test"))
        assertEquals(1, GetLocalGamesUseCase(repo)().size)
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
        val repo = FakeLocalGamesRepository()
        AddLocalGameUseCase(repo)("local:test")
        assertTrue(RemoveLocalGameUseCase(repo)("local:test"))
        assertFalse(RemoveLocalGameUseCase(repo)("local:test"))
        assertTrue(GetLocalGamesUseCase(repo)().isEmpty())
        assertEquals(1, GetInstalledLocalGamesUseCase(repo)().size)
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
        val repo = FakeLocalGamesRepository()
        val game = repo.installed.single()
        assertTrue(ExecuteLocalGameUseCase(repo)(game))
        assertEquals(game, repo.executed)
        assertTrue(CloseLocalGameUseCase(repo)())
        assertEquals(1, repo.closeCalls)
        repo.result = false
        assertFalse(ExecuteLocalGameUseCase(repo)(game))
        assertFalse(CloseLocalGameUseCase(repo)())
    }
}
