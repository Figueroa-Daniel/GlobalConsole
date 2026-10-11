package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.test.runTest
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.navigatorWeb.domain.usecase.fakes.FakeNavWebRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pruebas unitarias de los use cases de navegadores web: habilitar, deshabilitar,
 * ocultar, mostrar, consultar, ejecutar y cerrar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class NavigatorUseCasesTest {

    private val repository = FakeNavWebRepository(
        installed = listOf(NavigatorsWeb.FIREFOX, NavigatorsWeb.CHROME)
    )

    @Test
    fun getInstalled_returnsSystemNavigators() = runTest {
        assertEquals(
            listOf(NavigatorsWeb.FIREFOX, NavigatorsWeb.CHROME),
            GetInstalledNavsUseCase(repository)()
        )
    }

    @Test
    fun add_installedNavigator_enablesIt() = runTest {
        assertTrue(AddNavUseCase(repository)(NavigatorsWeb.FIREFOX))
        assertTrue(IsEnabledNavUseCase(repository)(NavigatorsWeb.FIREFOX))
    }

    @Test
    fun add_notInstalledNavigator_returnsFalse() = runTest {
        assertFalse(AddNavUseCase(repository)(NavigatorsWeb.BRAVE))
        assertFalse(IsEnabledNavUseCase(repository)(NavigatorsWeb.BRAVE))
    }

    @Test
    fun add_twice_secondReturnsFalse() = runTest {
        val add = AddNavUseCase(repository)
        assertTrue(add(NavigatorsWeb.CHROME))
        assertFalse(add(NavigatorsWeb.CHROME))
    }

    @Test
    fun remove_enabledNavigator_disablesIt() = runTest {
        AddNavUseCase(repository)(NavigatorsWeb.CHROME)
        assertTrue(RemoveNavUseCase(repository)(NavigatorsWeb.CHROME))
        assertFalse(IsEnabledNavUseCase(repository)(NavigatorsWeb.CHROME))
    }

    @Test
    fun remove_notEnabledNavigator_returnsFalse() = runTest {
        assertFalse(RemoveNavUseCase(repository)(NavigatorsWeb.CHROME))
    }

    @Test
    fun getNavigatorsWeb_returnsOnlyEnabledNavigatorsWithNavWebPlatform() = runTest {
        AddNavUseCase(repository)(NavigatorsWeb.FIREFOX)
        val result = GetNavigatorsWebUseCase(repository)()
        assertEquals(listOf(NavigatorsWeb.FIREFOX), result.map { it.navigatorType })
        assertEquals(Platforms.NAVWEB, result.single().platform)
    }

    @Test
    fun execute_delegatesToRepository() = runTest {
        AddNavUseCase(repository)(NavigatorsWeb.FIREFOX)
        val navigator = GetNavigatorsWebUseCase(repository)().single()
        assertTrue(ExecuteNavUseCase(repository)(navigator))
        assertEquals(navigator, repository.lastExecuted)
    }

    @Test
    fun execute_whenProcessFails_returnsFalse() = runTest {
        AddNavUseCase(repository)(NavigatorsWeb.FIREFOX)
        repository.processResult = false
        assertFalse(ExecuteNavUseCase(repository)(GetNavigatorsWebUseCase(repository)().single()))
    }

    @Test
    fun close_delegatesToRepository() = runTest {
        assertTrue(CloseNavUseCase(repository)())
        assertEquals(1, repository.closeCalls)
    }
}
