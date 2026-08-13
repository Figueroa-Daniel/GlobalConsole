package org.example.globalconsole.HeroicGames.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.HeroicGames.domain.usecase.fakes.FakeHGLauncherRepository
import org.example.globalconsole.generalDomain.entititys.Platforms
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pruebas unitarias para los use cases de visibilidad de Heroic Games Launcher:
 * [FindHGLauncherUseCase], [EnableHGLauncherUseCase], [HideHGLauncherUseCase]
 * y [ShowHGLauncherUseCase].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class HGLauncherVisibilityUseCasesTest {

    private val fakeRepository = FakeHGLauncherRepository()

    // ─────────────────────────────────────────────────────────────
    // FindHGLauncherUseCase
    // ─────────────────────────────────────────────────────────────

    /**
     * Verifica que [FindHGLauncherUseCase] retorna false cuando Heroic está deshabilitado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun findHGLauncher_whenDisabled_returnsFalse() = runTest {
        fakeRepository.heroicEnabled = false
        val useCase = FindHGLauncherUseCase(fakeRepository)
        assertFalse(useCase())
    }

    /**
     * Verifica que [FindHGLauncherUseCase] retorna true cuando Heroic está habilitado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun findHGLauncher_whenEnabled_returnsTrue() = runTest {
        fakeRepository.heroicEnabled = true
        val useCase = FindHGLauncherUseCase(fakeRepository)
        assertTrue(useCase())
    }

    // ─────────────────────────────────────────────────────────────
    // EnableHGLauncherUseCase
    // ─────────────────────────────────────────────────────────────

    /**
     * Verifica que [EnableHGLauncherUseCase] establece la preferencia a true en el repositorio.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun enableHGLauncher_setsHeroicEnabledToTrue() = runTest {
        fakeRepository.heroicEnabled = false
        val useCase = EnableHGLauncherUseCase(fakeRepository)
        useCase()
        assertTrue(fakeRepository.heroicEnabled)
    }

    // ─────────────────────────────────────────────────────────────
    // HideHGLauncherUseCase
    // ─────────────────────────────────────────────────────────────

    /**
     * Verifica que [HideHGLauncherUseCase] establece la preferencia a false en el repositorio.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun hideHGLauncher_setsHeroicEnabledToFalse() = runTest {
        fakeRepository.heroicEnabled = true
        val useCase = HideHGLauncherUseCase(fakeRepository)
        useCase()
        assertFalse(fakeRepository.heroicEnabled)
    }

    /**
     * Verifica que [HideHGLauncherUseCase] retorna true cuando la operación se realiza correctamente.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun hideHGLauncher_onSuccess_returnsTrue() = runTest {
        fakeRepository.shouldHideFail = false
        val useCase = HideHGLauncherUseCase(fakeRepository)
        assertTrue(useCase())
    }

    /**
     * Verifica que [HideHGLauncherUseCase] retorna false cuando el repositorio falla.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun hideHGLauncher_onFailure_returnsFalse() = runTest {
        fakeRepository.shouldHideFail = true
        val useCase = HideHGLauncherUseCase(fakeRepository)
        assertFalse(useCase())
    }

    // ─────────────────────────────────────────────────────────────
    // ShowHGLauncherUseCase
    // ─────────────────────────────────────────────────────────────

    /**
     * Verifica que [ShowHGLauncherUseCase] retorna un [HGLauncher] correctamente mapeado
     * con los datos esperados del launcher y la plataforma correcta.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun showHGLauncher_returnsMappedDomainEntity() = runTest {
        val useCase = ShowHGLauncherUseCase(fakeRepository)
        val launcher = useCase()
        assertEquals("heroic-launcher", launcher.id)
        assertEquals("Heroic Games", launcher.name)
        assertEquals("com.heroicgameslauncher.hgl", launcher.urlGameExecute)
        assertEquals(Platforms.HEORIC_GAMES_LAUCHER, launcher.platform)
    }

    /**
     * Verifica el ciclo completo de enable → find → hide → find.
     * Garantiza que los use cases se coordinan correctamente a través del repositorio.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun fullVisibilityCycle_enableThenHide_stateIsConsistent() = runTest {
        val find = FindHGLauncherUseCase(fakeRepository)
        val enable = EnableHGLauncherUseCase(fakeRepository)
        val hide = HideHGLauncherUseCase(fakeRepository)

        assertFalse(find())
        enable()
        assertTrue(find())
        hide()
        assertFalse(find())
    }
}
