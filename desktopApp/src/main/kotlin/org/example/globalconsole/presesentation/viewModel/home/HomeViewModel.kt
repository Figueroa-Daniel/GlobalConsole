package org.example.globalconsole.presesentation.viewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.HeroicGames.domain.usecase.ExecuteHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.FindHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.ShowHGLauncherUseCase
import org.example.globalconsole.melonDS.domain.usecase.FindMelonDSLauncherUseCase
import org.example.globalconsole.melonDS.domain.usecase.ShowMelonDSLauncherUseCase
import org.example.globalconsole.melonDS.domain.usecase.ExecuteLauncherMelonDSUseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase
import org.example.globalconsole.melonDS.domain.usecase.ExecuteGameMelonDSUseCase
import org.example.globalconsole.melonDS.domain.usecase.GetGamesDSUseCase
import org.example.globalconsole.PS3Launcher.domain.usecase.ExecutePS3LauncherUseCase
import org.example.globalconsole.PS3Launcher.domain.usecase.FindPS3LauncherUseCase
import org.example.globalconsole.PS3Launcher.domain.usecase.ShowPS3LauncherUseCase
import org.example.globalconsole.PS3Launcher.domain.usecase.ClosePS3LauncherUseCase

/**
 * ViewModel centralizado de la pantalla principal de GlobalConsole.
 * Agrega juegos de todas las fuentes disponibles y gestiona la búsqueda en local.
 * Delega las acciones de lanzamiento y eliminación al UseCase correspondiente
 * según la plataforma del juego seleccionado.
 *
 * @param getGamesP2UseCase UseCase para obtener la lista de juegos de PCSX2.
 * @param executeGameP2UseCase UseCase para lanzar un juego de PCSX2 en el emulador.
 * @param deleteGameP2UseCase UseCase para eliminar un juego de PCSX2 del sistema.
 * @param executeHGLauncherUseCase UseCase para lanzar Heroic Games Launcher.
 * @param findHGLauncherUseCase UseCase para consultar si Heroic debe aparecer en la biblioteca.
 * @param showHGLauncherUseCase UseCase para obtener los datos de Heroic Games Launcher como entidad de dominio.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-05
 */
class HomeViewModel(
    private val getGamesP2UseCase: GetGamesP2UseCase,
    private val executeGameP2UseCase: ExecuteGameP2UseCase? = null,
    private val deleteGameP2UseCase: DeleteGameP2UseCase? = null,
    private val executeHGLauncherUseCase: ExecuteHGLauncherUseCase? = null,
    private val findHGLauncherUseCase: FindHGLauncherUseCase? = null,
    private val showHGLauncherUseCase: ShowHGLauncherUseCase? = null,
    private val executeGameMelonDSUseCase: ExecuteGameMelonDSUseCase? = null,
    private val executeLauncherMelonDSUseCase: ExecuteLauncherMelonDSUseCase? = null,
    private val findMelonDSLauncherUseCase: FindMelonDSLauncherUseCase? = null,
    private val showMelonDSLauncherUseCase: ShowMelonDSLauncherUseCase? = null,
    private val getGamesDSUseCase: GetGamesDSUseCase? = null,
    private val closeGameP2UseCase: org.example.globalconsole.juegosPcsx2.domain.usecase.CloseGameP2UseCase? = null,
    private val closeGameDSUseCase: org.example.globalconsole.melonDS.domain.usecase.CloseGameDSUseCase? = null,
    private val closeLauncherMelonDSUseCase: org.example.globalconsole.melonDS.domain.usecase.CloseLauncherMelonDSUseCase? = null,
    private val closeHGLauncherUseCase: org.example.globalconsole.HeroicGames.domain.usecase.CloseHGLauncherUseCase? = null,
    private val getGamesDolphinUseCase: org.example.globalconsole.dolphin.domain.usecase.GetGamesDolphinUseCase? = null,
    private val executeGameDolphinUseCase: org.example.globalconsole.dolphin.domain.usecase.ExecuteGameDolphinUseCase? = null,
    private val closeGameDolphinUseCase: org.example.globalconsole.dolphin.domain.usecase.CloseGameDolphinUseCase? = null,
    private val executeLauncherDolphinUseCase: org.example.globalconsole.dolphin.domain.usecase.ExecuteLauncherDolphinUseCase? = null,
    private val closeLauncherDolphinUseCase: org.example.globalconsole.dolphin.domain.usecase.CloseLauncherDolphinUseCase? = null,
    private val findDolphinLauncherUseCase: org.example.globalconsole.dolphin.domain.usecase.FindDolphinLauncherUseCase? = null,
    private val showDolphinLauncherUseCase: org.example.globalconsole.dolphin.domain.usecase.ShowDolphinLauncherUseCase? = null,
    private val executePS3LauncherUseCase: ExecutePS3LauncherUseCase? = null,
    private val findPS3LauncherUseCase: FindPS3LauncherUseCase? = null,
    private val showPS3LauncherUseCase: ShowPS3LauncherUseCase? = null,
    private val closePS3LauncherUseCase: ClosePS3LauncherUseCase? = null,
    private val executeGame3DSUseCase: org.example.globalconsole.azahar.domain.usecase.ExecuteGame3DSUseCase? = null,
    private val executeLauncherAzaharUseCase: org.example.globalconsole.azahar.domain.usecase.ExecuteLauncherAzaharUseCase? = null,
    private val findAzaharLauncherUseCase: org.example.globalconsole.azahar.domain.usecase.FindAzaharLauncherUseCase? = null,
    private val showAzaharLauncherUseCase: org.example.globalconsole.azahar.domain.usecase.ShowAzaharLauncherUseCase? = null,
    private val getGames3DSUseCase: org.example.globalconsole.azahar.domain.usecase.GetGames3DSUseCase? = null,
    private val closeGame3DSUseCase: org.example.globalconsole.azahar.domain.usecase.CloseGame3DSUseCase? = null,
    private val closeLauncherAzaharUseCase: org.example.globalconsole.azahar.domain.usecase.CloseLauncherAzaharUseCase? = null,
    private val getGamesDuckStationUseCase: org.example.globalconsole.duckstation.domain.usecase.GetGamesDuckStationUseCase? = null,
    private val executeGameDuckStationUseCase: org.example.globalconsole.duckstation.domain.usecase.ExecuteGameDuckStationUseCase? = null,
    private val closeGameDuckStationUseCase: org.example.globalconsole.duckstation.domain.usecase.CloseGameDuckStationUseCase? = null,
    private val executeLauncherDuckStationUseCase: org.example.globalconsole.duckstation.domain.usecase.ExecuteLauncherDuckStationUseCase? = null,
    private val closeLauncherDuckStationUseCase: org.example.globalconsole.duckstation.domain.usecase.CloseLauncherDuckStationUseCase? = null,
    private val findDuckStationLauncherUseCase: org.example.globalconsole.duckstation.domain.usecase.FindDuckStationLauncherUseCase? = null,
    private val showDuckStationLauncherUseCase: org.example.globalconsole.duckstation.domain.usecase.ShowDuckStationLauncherUseCase? = null,
    private val deleteGameDuckStationUseCase: org.example.globalconsole.duckstation.domain.usecase.DeleteGameDuckStationUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    /**
     * Estado observable de la pantalla principal. Emite uno de los cuatro estados de [HomeUiState].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    /**
     * Query de búsqueda activa. Cadena vacía indica que no hay filtro aplicado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _groupingMode = MutableStateFlow(GroupingMode.NONE)
    val groupingMode: StateFlow<GroupingMode> = _groupingMode.asStateFlow()

    private val _launcherPosition = MutableStateFlow(LauncherPosition.INLINE)
    val launcherPosition: StateFlow<LauncherPosition> = _launcherPosition.asStateFlow()

    fun setGroupingMode(mode: GroupingMode) {
        _groupingMode.value = mode
        refreshItems()
    }

    fun setLauncherPosition(position: LauncherPosition) {
        _launcherPosition.value = position
        refreshItems()
    }

    private fun refreshItems() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val items = buildListItems(currentState.filteredGames, _groupingMode.value, _launcherPosition.value)
            _uiState.value = currentState.copy(items = items)
        }
    }

    private fun buildListItems(games: List<Game>, grouping: GroupingMode, position: LauncherPosition): List<HomeListItem> {
        val items = mutableListOf<HomeListItem>()
        
        // Separamos emuladores (launchers) de los juegos normales
        val launchers = games.filter { it.id.contains("-launcher", ignoreCase = true) || it.platform == Platforms.HEORIC_GAMES_LAUCHER || it.platform == Platforms.PS3 }
        val normalGames = games.filterNot { launchers.contains(it) }

        if (position == LauncherPosition.TOP && launchers.isNotEmpty()) {
            items.add(HomeListItem.Header("Emuladores"))
            items.addAll(launchers.sortedBy { it.name }.map { HomeListItem.GameItem(it) })
        }

        if (grouping == GroupingMode.NONE) {
            val gamesToRender = if (position == LauncherPosition.TOP) normalGames else games
            if (gamesToRender.isNotEmpty()) {
                if (position == LauncherPosition.TOP) {
                    items.add(HomeListItem.Header("Juegos"))
                }
                items.addAll(gamesToRender.sortedBy { it.name }.map { HomeListItem.GameItem(it) })
            }
            return items
        }

        // Agrupación (Platform o Series)
        val groupedMap = mutableMapOf<String, MutableList<Game>>()
        val gamesToGroup = if (position == LauncherPosition.TOP) normalGames else games
        
        gamesToGroup.forEach { game ->
            val groupName = when (grouping) {
                GroupingMode.PLATFORM -> game.platform.name
                GroupingMode.CONSOLE_SERIES -> {
                    when (game.platform) {
                        Platforms.PCSX2, Platforms.DUCKSTATION, Platforms.PS3 -> "PlayStation"
                        Platforms.MELONDS, Platforms.AZAHAR, Platforms.DOLPHIN -> "Nintendo"
                        Platforms.HEORIC_GAMES_LAUCHER, Platforms.LOCALGAME -> "PC"
                    }
                }
                else -> ""
            }
            groupedMap.getOrPut(groupName) { mutableListOf() }.add(game)
        }

        // Ordenamos las categorías por nombre
        groupedMap.keys.sorted().forEach { groupName ->
            val groupGames = groupedMap[groupName]!!
            items.add(HomeListItem.Header(groupName))
            
            val groupLaunchers = groupGames.filter { launchers.contains(it) }.sortedBy { it.name }
            val groupNormalGames = groupGames.filterNot { launchers.contains(it) }.sortedBy { it.name }
            
            items.addAll(groupLaunchers.map { HomeListItem.GameItem(it) })
            items.addAll(groupNormalGames.map { HomeListItem.GameItem(it) })
        }
        
        return items
    }

    /**
     * Carga todos los juegos de todas las fuentes disponibles y actualiza [uiState].
     * Combina las listas de cada plataforma en una lista única ordenada por nombre.
     * En caso de error en cualquier fuente, transiciona al estado [HomeUiState.Error].
     *
     * @author Daniel Figueroa Vidal
     * @return Unit
     * @throws Exception cualquier excepción del repositorio se captura y se refleja en [HomeUiState.Error].
     * @since 2026-08-05
     */
    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val pcsx2Games = getGamesP2UseCase()

                // Si Heroic Games Launcher está habilitado por el usuario, se añade
                // como una entrada en la biblioteca obtenida a través del caso de uso.
                val heroicEntry: List<Game> = if (findHGLauncherUseCase?.invoke() == true) {
                    val launcher = showHGLauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val melonDSEntry: List<Game> = if (findMelonDSLauncherUseCase?.invoke() == true) {
                    val launcher = showMelonDSLauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val dsGames = getGamesDSUseCase?.invoke()?.map { gameDs ->
                    Game(
                        id = gameDs.id,
                        name = gameDs.name,
                        urlGameExecute = gameDs.urlGameExecute,
                        image = null,
                        platform = Platforms.MELONDS
                    )
                } ?: emptyList()

                val dolphinEntry: List<Game> = if (findDolphinLauncherUseCase?.invoke() == true) {
                    val launcher = showDolphinLauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val dolphinGames = getGamesDolphinUseCase?.invoke()?.map { gameDolphin ->
                    Game(
                        id = gameDolphin.id,
                        name = gameDolphin.name,
                        urlGameExecute = gameDolphin.urlGameExecute,
                        image = gameDolphin.image,
                        platform = Platforms.DOLPHIN
                    )
                } ?: emptyList()

                val ps3Entry: List<Game> = if (findPS3LauncherUseCase?.invoke() == true) {
                    val launcher = showPS3LauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val azaharEntry: List<Game> = if (findAzaharLauncherUseCase?.invoke() == true) {
                    val launcher = showAzaharLauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val azaharGames = getGames3DSUseCase?.invoke()?.map { game3ds ->
                    Game(
                        id = game3ds.id,
                        name = game3ds.name,
                        urlGameExecute = game3ds.urlGameExecute,
                        image = game3ds.image,
                        platform = Platforms.AZAHAR
                    )
                } ?: emptyList()

                val duckStationEntry: List<Game> = if (findDuckStationLauncherUseCase?.invoke() == true) {
                    val launcher = showDuckStationLauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val duckStationGames = getGamesDuckStationUseCase?.invoke()?.map { gameDuckStation ->
                    Game(
                        id = gameDuckStation.id,
                        name = gameDuckStation.name,
                        urlGameExecute = gameDuckStation.urlGameExecute,
                        image = null,
                        platform = Platforms.DUCKSTATION
                    )
                } ?: emptyList()

                val allGames: List<Game> = (pcsx2Games + heroicEntry + melonDSEntry + dsGames + dolphinEntry + dolphinGames + ps3Entry + azaharEntry + azaharGames + duckStationEntry + duckStationGames).sortedBy { it.name }

                _uiState.value = if (allGames.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    val items = buildListItems(allGames, _groupingMode.value, _launcherPosition.value)
                    HomeUiState.Success(games = allGames, filteredGames = allGames, items = items)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(message = e.message ?: "Error desconocido al cargar juegos")
            }
        }
    }

    /**
     * Actualiza el query de búsqueda y filtra la lista de juegos por nombre en memoria.
     * El filtrado es case-insensitive y busca coincidencias parciales en el nombre del juego.
     * Solo tiene efecto si el estado actual es [HomeUiState.Success].
     *
     * @param query Texto introducido por el usuario en el campo de búsqueda.
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val filtered = if (query.isBlank()) {
                currentState.games
            } else {
                currentState.games.filter { game ->
                    game.name.contains(query, ignoreCase = true)
                }
            }
            val items = buildListItems(filtered, _groupingMode.value, _launcherPosition.value)
            _uiState.value = currentState.copy(filteredGames = filtered, items = items)
        }
    }

    /**
     * Lanza la ejecución del juego seleccionado delegando al UseCase correspondiente
     * según la plataforma del juego ([Game.platform]).
     *
     * @param game Juego seleccionado por el usuario en la biblioteca.
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    fun onGameSelected(game: Game) {
        if (_uiState.value is HomeUiState.GameRunning) return

        viewModelScope.launch {
            // Entramos en estado de suspensión
            _uiState.value = HomeUiState.GameRunning(game)

            val success = when (game.platform) {
                Platforms.PCSX2 -> executeGameP2UseCase?.invoke(game.id) ?: false
                Platforms.LOCALGAME -> {
                    // TODO: Implementar lanzamiento de juego local nativo
                    false
                }
                Platforms.HEORIC_GAMES_LAUCHER -> executeHGLauncherUseCase?.invoke() ?: false
                Platforms.MELONDS -> {
                    if (game.id == "melonds-launcher") {
                        executeLauncherMelonDSUseCase?.invoke() ?: false
                    } else {
                        executeGameMelonDSUseCase?.invoke(game.urlGameExecute) ?: false
                    }
                }
                Platforms.DOLPHIN -> {
                    if (game.id == "dolphin-launcher-id") {
                        executeLauncherDolphinUseCase?.invoke() ?: false
                    } else {
                        executeGameDolphinUseCase?.invoke(game.id) ?: false
                    }
                }
                Platforms.PS3 -> executePS3LauncherUseCase?.invoke() ?: false
                Platforms.AZAHAR -> {
                    if (game.id == "azahar-launcher") {
                        executeLauncherAzaharUseCase?.invoke() ?: false
                    } else {
                        executeGame3DSUseCase?.invoke(game.urlGameExecute) ?: false
                    }
                }
                Platforms.DUCKSTATION -> {
                    if (game.id == "duckstation-launcher-id") {
                        executeLauncherDuckStationUseCase?.invoke() ?: false
                    } else {
                        executeGameDuckStationUseCase?.invoke(game.id) ?: false
                    }
                }
            }

            // Al terminar la ejecución, volvemos a cargar la vista
            loadGames()
        }
    }

    /**
     * Cierra el juego o emulador que se encuentre actualmente en ejecución.
     */
    fun closeActiveGame() {
        val state = _uiState.value
        if (state !is HomeUiState.GameRunning) return

        viewModelScope.launch {
            val game = state.game
            when (game.platform) {
                Platforms.PCSX2 -> closeGameP2UseCase?.invoke()
                Platforms.LOCALGAME -> {}
                Platforms.HEORIC_GAMES_LAUCHER -> closeHGLauncherUseCase?.invoke()
                Platforms.MELONDS -> {
                    if (game.id == "melonds-launcher") {
                        closeLauncherMelonDSUseCase?.invoke()
                    } else {
                        closeGameDSUseCase?.invoke()
                    }
                }
                Platforms.DOLPHIN -> {
                    if (game.id == "dolphin-launcher-id") {
                        closeLauncherDolphinUseCase?.invoke()
                    } else {
                        closeGameDolphinUseCase?.invoke()
                    }
                }
                Platforms.PS3 -> closePS3LauncherUseCase?.invoke()
                Platforms.AZAHAR -> {
                    if (game.id == "azahar-launcher") {
                        closeLauncherAzaharUseCase?.invoke()
                    } else {
                        closeGame3DSUseCase?.invoke()
                    }
                }
                Platforms.DUCKSTATION -> {
                    if (game.id == "duckstation-launcher-id") {
                        closeLauncherDuckStationUseCase?.invoke()
                    } else {
                        closeGameDuckStationUseCase?.invoke()
                    }
                }
            }
        }
    }

    /**
     * Elimina el juego indicado del sistema de almacenamiento.
     * La acción delega al UseCase de la plataforma correspondiente.
     *
     * @param game Juego a eliminar del sistema.
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    fun onGameDeleted(game: Game) {
        viewModelScope.launch {
            when (game.platform) {
                Platforms.PCSX2 -> deleteGameP2UseCase?.invoke(game.id)
                Platforms.LOCALGAME -> {
                    // TODO: Implementar eliminación de juego local nativo
                }
                Platforms.HEORIC_GAMES_LAUCHER -> {
                    // TODO: Implementar eliminación de entrada de Heroic Games
                }
                Platforms.MELONDS -> {
                    // TODO: Implementar eliminación de juego de Melon DS
                }
                Platforms.DOLPHIN -> {
                    // TODO: Implementar eliminación de juego de Dolphin
                }
                Platforms.PS3 -> {
                    // TODO: Implementar eliminación de entrada de PS3
                }
                Platforms.AZAHAR -> {
                    // TODO: Implementar eliminación de juego de Azahar
                }
                Platforms.DUCKSTATION -> deleteGameDuckStationUseCase?.invoke(game.id)
            }
        }
    }
}
