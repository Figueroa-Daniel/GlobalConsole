package org.example.globalconsole.presesentation.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.viewModel.home.HomeUiState
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.view.components.GameTile
import org.example.globalconsole.presesentation.view.components.GamepadOSK
import org.example.globalconsole.presesentation.view.components.MetroTopBar
import org.example.globalconsole.presesentation.view.components.MetroButton
import org.example.globalconsole.presesentation.view.components.SetupPathDialog
import org.example.globalconsole.presesentation.view.components.TopBarFocus
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel

/**
 * Pantalla principal orquestadora de la interfaz de GlobalConsole.
 * Renderiza el estado del [HomeViewModel] utilizando una cuadrícula de estilo Metro.
 *
 * La navegación por gamepad está confinada al grid mediante [FocusRequester] por índice,
 * evitando que el foco se escape hacia la barra superior u otros elementos de la interfaz.
 *
 * @param viewModel ViewModel principal de la aplicación.
 * @param settingsViewModel ViewModel de configuración de rutas.
 * @param gamepadManager Gestor opcional de gamepad físico para control mediante mando.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    gamepadManager: GamepadManager? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showPathDialog by remember { mutableStateOf(false) }
    var showOSK by remember { mutableStateOf(false) }
    var focusedTopBar by remember { mutableStateOf(TopBarFocus.NONE) }
    
    val inputMode by gamepadManager?.inputMode?.collectAsState() ?: remember { mutableStateOf(org.example.globalconsole.presesentation.input.InputMode.GAMEPAD) }

    // Índice del tile actualmente enfocado por el mando
    var focusedGameIndex by remember { mutableStateOf(0) }

    // Número de columnas del grid, calculado dinámicamente a partir del ancho del contenedor
    var gridColumns by remember { mutableStateOf(4) }
    val density = LocalDensity.current

    // Al iniciar, cargamos la ruta desde el repositorio de configuración.
    // Si no hay ruta configurada, mostramos el diálogo de configuración.
    LaunchedEffect(Unit) {
        settingsViewModel.loadCurrentPath("pcsx2")
        val hasPath = settingsViewModel.uiState.value.let { state ->
            state is org.example.globalconsole.presesentation.viewModel.settings.SettingsUiState.Success &&
                !state.path.isNullOrBlank()
        }
        if (!hasPath) {
            showPathDialog = true
        } else {
            viewModel.loadGames()
        }
    }

    // Suspender la lectura del gamepad si un juego está corriendo
    LaunchedEffect(uiState) {
        gamepadManager?.isSuspended = (uiState is HomeUiState.GameRunning)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MetroTopBar(
                searchQuery = searchQuery,
                onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                focusedButton = focusedTopBar,
                inputMode = inputMode,
                onSearchClick = { showOSK = true },
                onRefreshClick = { viewModel.loadGames() },
                onSettingsClick = { showPathDialog = true }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(24.dp)
            ) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    is HomeUiState.Success -> {
                        val games = state.filteredGames

                        // Un FocusRequester por cada tile del grid
                        val focusRequesters = remember(games.size) {
                            List(games.size) { FocusRequester() }
                        }

                        // Ajustar índice si la lista se reduce (ej: búsqueda filtra juegos)
                        LaunchedEffect(games.size) {
                            if (focusedGameIndex >= games.size && games.isNotEmpty()) {
                                focusedGameIndex = games.size - 1
                            }
                        }

                        // Navegación por gamepad confinada al grid por índice
                        LaunchedEffect(gamepadManager, games.size, gridColumns) {
                            gamepadManager?.events?.collectLatest { event ->
                                when (event) {
                                    is GamepadEvent.DirectionPressed -> {
                                        if (games.isEmpty()) return@collectLatest
                                        
                                        if (focusedTopBar != TopBarFocus.NONE) {
                                            when (event.direction) {
                                                GamepadEvent.Direction.LEFT -> {
                                                    focusedTopBar = when (focusedTopBar) {
                                                        TopBarFocus.SETTINGS -> TopBarFocus.REFRESH
                                                        TopBarFocus.REFRESH -> TopBarFocus.SEARCH
                                                        else -> focusedTopBar
                                                    }
                                                }
                                                GamepadEvent.Direction.RIGHT -> {
                                                    focusedTopBar = when (focusedTopBar) {
                                                        TopBarFocus.SEARCH -> TopBarFocus.REFRESH
                                                        TopBarFocus.REFRESH -> TopBarFocus.SETTINGS
                                                        else -> focusedTopBar
                                                    }
                                                }
                                                GamepadEvent.Direction.DOWN -> {
                                                    focusedTopBar = TopBarFocus.NONE
                                                }
                                                GamepadEvent.Direction.UP -> {}
                                            }
                                        } else {
                                            val current = focusedGameIndex.coerceIn(0, games.size - 1)
                                            val newIndex = when (event.direction) {
                                                GamepadEvent.Direction.UP -> {
                                                    val candidate = current - gridColumns
                                                    if (candidate >= 0) candidate else {
                                                        focusedTopBar = TopBarFocus.SEARCH
                                                        current
                                                    }
                                                }
                                                GamepadEvent.Direction.DOWN -> {
                                                    val candidate = current + gridColumns
                                                    if (candidate < games.size) candidate else current
                                                }
                                                GamepadEvent.Direction.LEFT -> {
                                                    val candidate = current - 1
                                                    if (candidate >= 0 && candidate / gridColumns == current / gridColumns) candidate else current
                                                }
                                                GamepadEvent.Direction.RIGHT -> {
                                                    val candidate = current + 1
                                                    if (candidate < games.size && candidate / gridColumns == current / gridColumns) candidate else current
                                                }
                                            }
    
                                            if (newIndex != current && newIndex < focusRequesters.size && focusedTopBar == TopBarFocus.NONE) {
                                                focusedGameIndex = newIndex
                                                focusRequesters[newIndex].requestFocus()
                                            }
                                        }
                                    }

                                    is GamepadEvent.ButtonPressed -> {
                                        when (event.button) {
                                            GamepadEvent.Button.CONFIRM -> {
                                                if (focusedTopBar != TopBarFocus.NONE) {
                                                    when (focusedTopBar) {
                                                        TopBarFocus.SEARCH -> showOSK = true
                                                        TopBarFocus.REFRESH -> viewModel.loadGames()
                                                        TopBarFocus.SETTINGS -> showPathDialog = true
                                                        else -> {}
                                                    }
                                                } else if (!showPathDialog && !showOSK && games.isNotEmpty()) {
                                                    val idx = focusedGameIndex.coerceIn(0, games.size - 1)
                                                    viewModel.onGameSelected(games[idx])
                                                }
                                            }
                                            GamepadEvent.Button.BACK -> {
                                                if (showPathDialog) showPathDialog = false
                                            }
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }

                        // Medir el ancho del grid para calcular columnas y confinar la navegación
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onSizeChanged { size ->
                                    with(density) {
                                        val newCols = (size.width / 180.dp.toPx())
                                            .toInt()
                                            .coerceAtLeast(1)
                                        if (newCols != gridColumns) gridColumns = newCols
                                    }
                                }
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 180.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                itemsIndexed(games) { index, game ->
                                    GameTile(
                                        game = game,
                                        focusRequester = focusRequesters[index],
                                        inputMode = inputMode,
                                        onClick = { viewModel.onGameSelected(game) },
                                        onFocus = { focusedGameIndex = index }
                                    )
                                }
                            }
                        }
                    }

                    is HomeUiState.Empty -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BIBLIOTECA VACÍA",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se encontraron juegos en la ruta configurada.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MetroButton(
                                text = "CAMBIAR RUTA",
                                onClick = { showPathDialog = true },
                                isPrimary = true
                            )
                        }
                    }

                    is HomeUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ERROR DE SISTEMA",
                                color = Color.Red,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MetroButton(
                                text = "REINTENTAR",
                                onClick = { viewModel.loadGames() }
                            )
                        }
                    }

                    is HomeUiState.GameRunning -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "JUEGO EN EJECUCIÓN",
                                color = Color(0xFF00FFCC),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ejecutando: ${state.game.name}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "El entorno está suspendido. Se reactivará al cerrar el emulador.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            MetroButton(
                                text = "FORZAR REACTIVACIÓN",
                                onClick = { viewModel.loadGames() }
                            )
                        }
                    }
                }
            }
        }

        if (showPathDialog && gamepadManager != null) {
            SetupPathDialog(
                settingsViewModel = settingsViewModel,
                gamepadManager = gamepadManager,
                onDismiss = {
                    showPathDialog = false
                    // Recarga la biblioteca siempre al cerrar el diálogo: cubre el caso
                    // en que el usuario cambia el toggle de Heroic sin pulsar GUARDAR.
                    viewModel.loadGames()
                },
                onConfirm = {
                    showPathDialog = false
                    viewModel.loadGames()
                }
            )
        }

        if (showOSK && gamepadManager != null) {
            GamepadOSK(
                gamepadManager = gamepadManager,
                initialText = searchQuery,
                onTextChanged = { viewModel.onSearchQueryChanged(it) },
                onConfirm = {
                    viewModel.onSearchQueryChanged(it)
                    showOSK = false
                },
                onDismiss = { showOSK = false }
            )
        }

    }
}
