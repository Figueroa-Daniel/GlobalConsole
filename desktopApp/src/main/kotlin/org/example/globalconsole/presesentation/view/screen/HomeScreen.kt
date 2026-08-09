package org.example.globalconsole.presesentation.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.globalconsole.presesentation.viewModel.home.HomeUiState
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.view.components.GameTile
import org.example.globalconsole.presesentation.view.components.MetroTopBar
import org.example.globalconsole.presesentation.view.components.SetupPathDialog
import org.example.globalconsole.settings.ROUTE_PCSX2_GAMES

/**
 * Pantalla principal orquestadora de la interfaz de GlobalConsole.
 * Renderiza el estado del [HomeViewModel] utilizando una cuadrícula de estilo Metro.
 *
 * @param viewModel ViewModel principal de la aplicación.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showPathDialog by remember { mutableStateOf(false) }

    // Al iniciar, si no hay ruta configurada en RAM, mostramos el diálogo
    LaunchedEffect(Unit) {
        val currentPath = ROUTE_PCSX2_GAMES
        if (currentPath.isNullOrBlank()) {
            showPathDialog = true
        } else {
            viewModel.loadGames()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barra superior Metro
            MetroTopBar(
                searchQuery = searchQuery,
                onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                onRefreshClick = { viewModel.loadGames() },
                onSettingsClick = { showPathDialog = true }
            )

            // Contenedor Principal
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
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 180.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.filteredGames) { game ->
                                GameTile(
                                    game = game,
                                    onClick = { viewModel.onGameSelected(game) }
                                )
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
                            Box(
                                modifier = Modifier
                                    .background(Color.White)
                                    .clickable { showPathDialog = true }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "CAMBIAR RUTA",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
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
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.White, RectangleShape)
                                    .clickable { viewModel.loadGames() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "REINTENTAR",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mostrar diálogo modal para configurar ruta
        if (showPathDialog) {
            SetupPathDialog(
                onDismiss = { showPathDialog = false },
                onConfirm = {
                    showPathDialog = false
                    viewModel.loadGames()
                }
            )
        }
    }
}
