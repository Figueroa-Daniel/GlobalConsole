package org.example.globalconsole.presesentation.viewModel.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import org.example.globalconsole.presesentation.viewModel.home.fakes.FakeGetGamesP2UseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests unitarios para la funcionalidad de búsqueda reactiva del [HomeViewModel].
 *
 * Verifica que el filtrado de juegos funcione correctamente tanto con búsquedas
 * parciales, búsquedas vacías y búsquedas sin resultados.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelSearchTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeGetGamesUseCase: FakeGetGamesP2UseCase
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeGetGamesUseCase = FakeGetGamesP2UseCase().apply {
            games = mutableListOf(
                GameP2(id = "1", name = "God of War", urlGameExecute = "/isos/gow.iso", image = null, platform = Platforms.PCSX2),
                GameP2(id = "2", name = "Gran Turismo", urlGameExecute = "/isos/gt.iso", image = null, platform = Platforms.PCSX2),
                GameP2(id = "3", name = "Shadow of the Colossus", urlGameExecute = "/isos/shadow.iso", image = null, platform = Platforms.PCSX2)
            )
        }
        viewModel = HomeViewModel(getGamesP2UseCase = fakeGetGamesUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que al buscar por un nombre parcial, solo se devuelven juegos que coinciden.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun onSearchQueryChanged_filtersByName() = runTest {
        viewModel.loadGames()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("God")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(1, state.filteredGames.size, "Solo debe aparecer 'God of War'")
        assertEquals("God of War", state.filteredGames.first().name)
    }

    /**
     * Verifica que al limpiar la búsqueda, se devuelve la lista completa de juegos.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun onSearchQueryChanged_emptyQueryRestoresAll() = runTest {
        viewModel.loadGames()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("God")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(3, state.filteredGames.size, "Al limpiar la búsqueda deben aparecer los 3 juegos")
    }

    /**
     * Verifica que el query de búsqueda se actualiza en el StateFlow al cambiar.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun onSearchQueryChanged_updatesSearchQueryStateFlow() = runTest {
        viewModel.loadGames()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("mario")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("mario", viewModel.searchQuery.value,
            "El StateFlow de searchQuery debe reflejar el texto introducido")
    }
}
