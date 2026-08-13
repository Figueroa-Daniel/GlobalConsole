package org.example.globalconsole.presesentation.viewModel.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.presesentation.viewModel.home.fakes.FakeFindHGLauncherUseCase
import org.example.globalconsole.presesentation.viewModel.home.fakes.FakeGetGamesP2UseCase
import org.example.globalconsole.presesentation.viewModel.home.fakes.FakeShowHGLauncherUseCase
import org.example.globalconsole.presesentation.viewModel.home.HomeUiState
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pruebas unitarias para [HomeViewModel].
 * Verifica las transiciones de estado y el filtrado de juegos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-05
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeGetGamesP2UseCase: FakeGetGamesP2UseCase
    private lateinit var homeViewModel: HomeViewModel

    /**
     * Configura el dispatcher de test y los fakes antes de cada prueba.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeGetGamesP2UseCase = FakeGetGamesP2UseCase()
        homeViewModel = HomeViewModel(getGamesP2UseCase = fakeGetGamesP2UseCase)
    }

    /**
     * Restaura el dispatcher principal tras cada prueba.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifica que el estado inicial antes de cargar sea [HomeUiState.Loading].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @Test
    fun loadGames_initialState_isLoading() = runTest {
        assertIs<HomeUiState.Loading>(homeViewModel.uiState.value)
    }

    /**
     * Verifica que tras cargar juegos correctamente el estado sea [HomeUiState.Success]
     * con la lista de juegos esperada.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @Test
    fun loadGames_withGames_stateIsSuccess() = runTest {
        val game1 = GameP2("1", "Metal Gear Solid 3", "/iso/mgs3.iso", null, Platforms.PCSX2)
        val game2 = GameP2("2", "Gran Turismo 4", "/iso/gt4.iso", null, Platforms.PCSX2)
        fakeGetGamesP2UseCase.games.addAll(listOf(game1, game2))

        homeViewModel.loadGames()
        advanceUntilIdle()

        val state = homeViewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(2, state.games.size)
    }

    /**
     * Verifica que cuando no hay juegos disponibles el estado sea [HomeUiState.Empty].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @Test
    fun loadGames_withNoGames_stateIsEmpty() = runTest {
        homeViewModel.loadGames()
        advanceUntilIdle()

        assertIs<HomeUiState.Empty>(homeViewModel.uiState.value)
    }

    /**
     * Verifica que cuando el repositorio falla el estado sea [HomeUiState.Error].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @Test
    fun loadGames_whenErrorOccurs_stateIsError() = runTest {
        fakeGetGamesP2UseCase.shouldThrowError = true

        homeViewModel.loadGames()
        advanceUntilIdle()

        assertIs<HomeUiState.Error>(homeViewModel.uiState.value)
    }

    /**
     * Verifica que al buscar por nombre se filtran correctamente los juegos en [HomeUiState.Success].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @Test
    fun onSearchQueryChanged_withMatchingQuery_filtersGames() = runTest {
        val game1 = GameP2("1", "Metal Gear Solid 3", "/iso/mgs3.iso", null, Platforms.PCSX2)
        val game2 = GameP2("2", "Gran Turismo 4", "/iso/gt4.iso", null, Platforms.PCSX2)
        fakeGetGamesP2UseCase.games.addAll(listOf(game1, game2))

        homeViewModel.loadGames()
        advanceUntilIdle()

        homeViewModel.onSearchQueryChanged("metal")
        advanceUntilIdle()

        val state = homeViewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(1, state.filteredGames.size)
        assertEquals("Metal Gear Solid 3", state.filteredGames[0].name)
    }

    /**
     * Verifica que al limpiar la búsqueda se restauran todos los juegos en [filteredGames].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    @Test
    fun onSearchQueryChanged_withEmptyQuery_restoresAllGames() = runTest {
        val game1 = GameP2("1", "Metal Gear Solid 3", "/iso/mgs3.iso", null, Platforms.PCSX2)
        val game2 = GameP2("2", "Gran Turismo 4", "/iso/gt4.iso", null, Platforms.PCSX2)
        fakeGetGamesP2UseCase.games.addAll(listOf(game1, game2))

        homeViewModel.loadGames()
        advanceUntilIdle()
        homeViewModel.onSearchQueryChanged("metal")
        advanceUntilIdle()
        homeViewModel.onSearchQueryChanged("")
        advanceUntilIdle()

        val state = homeViewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(2, state.filteredGames.size)
    }

    /**
     * Verifica que cuando Heroic Games Launcher está habilitado aparece como entrada
     * en la lista de juegos junto a los juegos de PCSX2.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun loadGames_withHeroicEnabled_includesHeroicInGamesList() = runTest {
        val fakeFindHGLauncher = FakeFindHGLauncherUseCase().apply { heroicEnabled = true }
        val fakeShowHGLauncher = FakeShowHGLauncherUseCase()
        val viewModel = HomeViewModel(
            getGamesP2UseCase = fakeGetGamesP2UseCase,
            findHGLauncherUseCase = fakeFindHGLauncher,
            showHGLauncherUseCase = fakeShowHGLauncher
        )
        val game = GameP2("1", "Gran Turismo 4", "/iso/gt4.iso", null, Platforms.PCSX2)
        fakeGetGamesP2UseCase.games.add(game)

        viewModel.loadGames()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(2, state.games.size)
        assertTrue(state.games.any { it.name == "Heroic Games" })
    }

    /**
     * Verifica que cuando Heroic Games Launcher está deshabilitado no aparece
     * en la lista de juegos.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Test
    fun loadGames_withHeroicDisabled_doesNotIncludeHeroicInGamesList() = runTest {
        val fakeFindHGLauncher = FakeFindHGLauncherUseCase().apply { heroicEnabled = false }
        val fakeShowHGLauncher = FakeShowHGLauncherUseCase()
        val viewModel = HomeViewModel(
            getGamesP2UseCase = fakeGetGamesP2UseCase,
            findHGLauncherUseCase = fakeFindHGLauncher,
            showHGLauncherUseCase = fakeShowHGLauncher
        )
        val game = GameP2("1", "Gran Turismo 4", "/iso/gt4.iso", null, Platforms.PCSX2)
        fakeGetGamesP2UseCase.games.add(game)

        viewModel.loadGames()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<HomeUiState.Success>(state)
        assertEquals(1, state.games.size)
        assertFalse(state.games.any { it.name == "Heroic Games" })
    }
}

