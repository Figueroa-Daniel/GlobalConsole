package org.example.globalconsole.generalDomain.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.generalDomain.presentation.state.HomeUiState
import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase

/**
 * ViewModel centralizado de la pantalla principal de GlobalConsole.
 * Agrega juegos de todas las fuentes disponibles (actualmente solo PCSX2),
 * gestiona la búsqueda en local y delega las acciones de lanzamiento y eliminación
 * al UseCase correspondiente según la plataforma del juego.
 *
 * @param getGamesP2UseCase UseCase para obtener la lista de juegos de PCSX2.
 * @param executeGameP2UseCase UseCase para lanzar un juego de PCSX2 en el emulador.
 * @param deleteGameP2UseCase UseCase para eliminar un juego de PCSX2 del sistema.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-05
 */
class HomeViewModel(
    private val getGamesP2UseCase: GetGamesP2UseCase,
    private val executeGameP2UseCase: ExecuteGameP2UseCase? = null,  // Opcional hasta que se conecte la UI
    private val deleteGameP2UseCase: DeleteGameP2UseCase? = null      // Opcional hasta que se conecte la UI
    // TODO: Añadir aquí futura fuente de juegos de Heroic Games Launcher
    // private val getHeroicGamesUseCase: GetHeroicGamesUseCase,
    // TODO: Añadir aquí futura fuente de juegos nativos de PC
    // private val getLocalGamesUseCase: GetLocalGamesUseCase,
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
                // TODO: Añadir aquí la carga de juegos de Heroic Games Launcher
                // val heroicGames = getHeroicGamesUseCase()
                // TODO: Añadir aquí la carga de juegos nativos de PC
                // val localGames = getLocalGamesUseCase()

                val allGames: List<Game> = (pcsx2Games)
                    // TODO: Combinar con otras fuentes cuando estén disponibles:
                    // + heroicGames + localGames
                    .sortedBy { it.name }

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
            when (game.platform) {
                Platforms.PCSX2 -> executeGameP2UseCase?.invoke(game.id)
                Platforms.LOCALGAME -> {
                    // TODO: Implementar lanzamiento de juego local nativo
                }
                // TODO: Añadir aquí el caso de Heroic Games Launcher cuando esté disponible
            }
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
                // TODO: Añadir aquí el caso de Heroic Games Launcher cuando esté disponible
            }
        }
    }
}
