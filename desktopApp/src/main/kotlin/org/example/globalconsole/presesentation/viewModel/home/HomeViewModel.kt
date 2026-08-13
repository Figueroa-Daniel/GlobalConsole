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
import org.example.globalconsole.HeroicGames.domain.usecase.ShowHGLauncherUseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase
import org.example.globalconsole.settings.domain.usecase.IsHeroicEnabledUseCase

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
 * @param isHeroicEnabledUseCase UseCase para consultar si Heroic debe aparecer en la biblioteca.
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
    private val isHeroicEnabledUseCase: IsHeroicEnabledUseCase? = null,
    private val showHGLauncherUseCase: ShowHGLauncherUseCase? = null
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
                val heroicEntry: List<Game> = if (isHeroicEnabledUseCase?.invoke() == true) {
                    val launcher = showHGLauncherUseCase?.invoke()
                    if (launcher != null) listOf(launcher) else emptyList()
                } else {
                    emptyList()
                }

                val allGames: List<Game> = (pcsx2Games + heroicEntry).sortedBy { it.name }

                _uiState.value = if (allGames.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Success(games = allGames, filteredGames = allGames)
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
            _uiState.value = currentState.copy(filteredGames = filtered)
        }
    }

    /**
     * Lanza la ejecución del juego seleccionado delegando al UseCase correspondiente
     * según la plataforma del juego ([Game.platform]).
     *
     * Nota: Actualmente solo PCSX2 está implementado. Este método crecerá con cada
     * nueva plataforma. En un futuro se valorará refactorizar a un UseCase genérico
     * (ver docs/sugestiones/ejecutar-juego-dispatch.md).
     *
     * @param game Juego seleccionado por el usuario en la biblioteca.
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    fun onGameSelected(game: Game) {
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
            }
            
            // Al terminar la ejecución, volvemos a cargar la vista
            loadGames()
        }
    }

    /**
     * Elimina el juego indicado del sistema de almacenamiento.
     * La acción delega al UseCase de la plataforma correspondiente.
     *
     * Nota: La acción de eliminación está preparada en el ViewModel pero aún no está
     * conectada a ningún flujo de UI. Se activará cuando se diseñe el flujo de confirmación.
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
            }
        }
    }
}
