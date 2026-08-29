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
import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.presesentation.view.components.GameTile
import org.example.globalconsole.presesentation.view.components.GamepadOSK
import org.example.globalconsole.presesentation.view.components.ImageCropperDialog
import org.example.globalconsole.presesentation.view.components.MetroTopBar
import org.example.globalconsole.presesentation.view.components.MetroButton
import org.example.globalconsole.presesentation.view.components.SetupPathDialog
import org.example.globalconsole.presesentation.view.components.TopBarFocus
import org.example.globalconsole.presesentation.view.components.ViewSettingsDialog
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import java.io.File

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
    var showViewSettings by remember { mutableStateOf(false) }
    var focusedTopBar by remember { mutableStateOf(TopBarFocus.NONE) }

    // Juego seleccionado para abrir el recortador de carátula (null = diálogo cerrado)
    var cropTargetGame by remember { mutableStateOf<Game?>(null) }
    
    val inputMode by gamepadManager?.inputMode?.collectAsState() ?: remember { mutableStateOf(org.example.globalconsole.presesentation.input.InputMode.GAMEPAD) }

    // Índice del tile actualmente enfocado por el mando (navegación con D-Pad/stick izquierdo)
    var focusedGameIndex by remember { mutableStateOf(0) }

    // Índice del tile actualmente bajo el cursor del ratón (navegación con stick derecho)
    var hoveredGameIndex by remember { mutableStateOf(0) }

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
        val isRunning = uiState is HomeUiState.GameRunning
        gamepadManager?.isSuspended = isRunning
        gamepadManager?.isMouseAllowedWhenSuspended = isRunning && (
                (uiState as HomeUiState.GameRunning).game.platform == org.example.globalconsole.generalDomain.entititys.Platforms.MELONDS ||
                (uiState as HomeUiState.GameRunning).game.platform == org.example.globalconsole.generalDomain.entititys.Platforms.PS3
        )
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
                onViewClick = { showViewSettings = true },
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

                        // Un FocusRequester por cada elemento de la lista (incluyendo headers para mantener los índices alineados)
                        val focusRequesters = remember(state.items.size) {
                            List(state.items.size) { FocusRequester() }
                        }

                        // Ajustar índice si la lista se reduce (ej: búsqueda filtra juegos)
                        LaunchedEffect(state.items.size) {
                            if (focusedGameIndex >= state.items.size && state.items.isNotEmpty()) {
                                focusedGameIndex = state.items.size - 1
                            }
                        }

                        // Navegación por gamepad confinada al grid por índice.
                        // IMPORTANTE: Se incluyen los estados de diálogos (cropTargetGame, showOSK, showPathDialog)
                        // como keys para que este LaunchedEffect se reinicie y se detenga cuando algún diálogo modal
                        // está abierto, evitando fugas de eventos del mando (Regla #1).
                        LaunchedEffect(
                            gamepadManager, state.items.size, gridColumns,
                            cropTargetGame != null, showOSK, showPathDialog, showViewSettings
                        ) {
                            // Si algún diálogo está abierto, no procesar eventos aquí (aislamiento de foco)
                            if (cropTargetGame != null || showOSK || showPathDialog || showViewSettings) return@LaunchedEffect
                            
                            gamepadManager?.events?.collectLatest { event ->
                                when (event) {
                                    is GamepadEvent.DirectionPressed -> {
                                        if (state.items.isEmpty()) return@collectLatest
                                        
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
                                                        TopBarFocus.REFRESH -> TopBarFocus.VIEW
                                                        TopBarFocus.VIEW -> TopBarFocus.SETTINGS
                                                        else -> focusedTopBar
                                                    }
                                                }
                                                GamepadEvent.Direction.DOWN -> {
                                                    focusedTopBar = TopBarFocus.NONE
                                                }
                                                GamepadEvent.Direction.UP -> {}
                                            }
                                        } else {
                                            val current = focusedGameIndex.coerceIn(0, (state.items.size - 1).coerceAtLeast(0))
                                            // Lógica para saltar headers
                                            var candidate = current
                                            var moved = false
                                            
                                            // Realizamos un salto base y luego comprobamos si caemos en un header
                                            val newIndexRaw = when (event.direction) {
                                                GamepadEvent.Direction.UP -> {
                                                    val raw = current - gridColumns
                                                    if (raw >= 0) raw else {
                                                        focusedTopBar = TopBarFocus.SEARCH
                                                        current
                                                    }
                                                }
                                                GamepadEvent.Direction.DOWN -> {
                                                    val raw = current + gridColumns
                                                    if (raw < state.items.size) raw else current
                                                }
                                                GamepadEvent.Direction.LEFT -> {
                                                    val raw = current - 1
                                                    if (raw >= 0 && raw / gridColumns == current / gridColumns) raw else current
                                                }
                                                GamepadEvent.Direction.RIGHT -> {
                                                    val raw = current + 1
                                                    if (raw < state.items.size && raw / gridColumns == current / gridColumns) raw else current
                                                }
                                            }

                                            // Si el raw index es un header, seguimos avanzando en la misma dirección (skip)
                                            if (newIndexRaw != current) {
                                                candidate = newIndexRaw
                                                val maxTries = 5
                                                var tries = 0
                                                while (candidate in state.items.indices && state.items[candidate] is org.example.globalconsole.presesentation.viewModel.home.HomeListItem.Header && tries < maxTries) {
                                                    candidate = when (event.direction) {
                                                        GamepadEvent.Direction.UP -> candidate - gridColumns
                                                        GamepadEvent.Direction.DOWN -> candidate + gridColumns
                                                        GamepadEvent.Direction.LEFT -> candidate - 1
                                                        GamepadEvent.Direction.RIGHT -> candidate + 1
                                                    }
                                                    tries++
                                                }
                                                // Asegurarnos de que el candidate final es válido y es un GameItem
                                                if (candidate in state.items.indices && state.items[candidate] !is org.example.globalconsole.presesentation.viewModel.home.HomeListItem.Header) {
                                                    moved = true
                                                }
                                            }
    
                                            if (moved && candidate != current && focusedTopBar == TopBarFocus.NONE) {
                                                focusedGameIndex = candidate
                                                focusRequesters[candidate].requestFocus()
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
                                                        TopBarFocus.VIEW -> showViewSettings = true
                                                        TopBarFocus.SETTINGS -> showPathDialog = true
                                                        else -> {}
                                                    }
                                                } else if (!showPathDialog && !showOSK && !showViewSettings && state.items.isNotEmpty()) {
                                                    val idx = focusedGameIndex.coerceIn(0, state.items.size - 1)
                                                    val item = state.items.getOrNull(idx)
                                                    if (item is org.example.globalconsole.presesentation.viewModel.home.HomeListItem.GameItem) {
                                                        viewModel.onGameSelected(item.game)
                                                    }
                                                }
                                            }
                                            GamepadEvent.Button.BACK -> {
                                                if (showPathDialog) showPathDialog = false
                                            }
                                            GamepadEvent.Button.HOME -> {
                                                viewModel.closeActiveGame()
                                            }
                                            GamepadEvent.Button.OPTIONS -> {
                                                // Triángulo/Y: abrir recortador del tile activo.
                                                // En modo MOUSE usamos el último tile sobrevolado,
                                                // en modo GAMEPAD el último enfocado con el mando.
                                                val activeIndex = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.MOUSE) {
                                                    hoveredGameIndex
                                                } else {
                                                    focusedGameIndex
                                                }
                                                val targetItem = state.items.getOrNull(activeIndex)
                                                if (targetItem is org.example.globalconsole.presesentation.viewModel.home.HomeListItem.GameItem) {
                                                    val targetGame = targetItem.game
                                                    if (!targetGame.image.isNullOrBlank()) {
                                                        cropTargetGame = targetGame
                                                    }
                                                }
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
                                state.items.forEachIndexed { index, item ->
                                    when (item) {
                                        is org.example.globalconsole.presesentation.viewModel.home.HomeListItem.Header -> {
                                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                                Text(
                                                    text = item.title.uppercase(),
                                                    color = Color(0xFF00FFCC),
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Black,
                                                    fontFamily = FontFamily.SansSerif,
                                                    letterSpacing = 2.sp,
                                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                                )
                                            }
                                        }
                                        is org.example.globalconsole.presesentation.viewModel.home.HomeListItem.GameItem -> {
                                            item {
                                                GameTile(
                                                    game = item.game,
                                                    focusRequester = focusRequesters.getOrNull(index) ?: FocusRequester(),
                                                    inputMode = inputMode,
                                                    onClick = { viewModel.onGameSelected(item.game) },
                                                    onFocus = { focusedGameIndex = index },
                                                    onHover = { hoveredGameIndex = index }
                                                )
                                            }
                                        }
                                    }
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
                        LaunchedEffect(gamepadManager) {
                            gamepadManager?.events?.collectLatest { event ->
                                if (event is GamepadEvent.ButtonPressed && event.button == GamepadEvent.Button.HOME) {
                                    viewModel.closeActiveGame()
                                }
                            }
                        }
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

        if (showViewSettings && gamepadManager != null) {
            ViewSettingsDialog(
                homeViewModel = viewModel,
                gamepadManager = gamepadManager,
                onDismiss = { showViewSettings = false }
            )
        }

        // Diálogo de recorte de carátula — se activa con botón Y (Triángulo) del mando
        val cropGame = cropTargetGame
        if (cropGame != null && !cropGame.image.isNullOrBlank()) {
            val outputDir = File(cropGame.image!!).parent ?: ""
            ImageCropperDialog(
                imagePath = cropGame.image!!,
                outputDir = outputDir,
                gamepadManager = gamepadManager,
                onDismiss = { cropTargetGame = null },
                onCropSaved = {
                    cropTargetGame = null
                    // Recargar la biblioteca para reflejar la nueva carátula recortada
                    viewModel.loadGames()
                }
            )
        }

    }
}
