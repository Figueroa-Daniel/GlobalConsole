package org.example.globalconsole

import org.example.globalconsole.presesentation.view.components.MetroTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.globalconsole.presesentation.viewModel.home.HomeUiState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.navigation.AppNavHost
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import org.example.globalconsole.di.dataModule
import org.example.globalconsole.di.domainModule
import org.example.globalconsole.di.presentationModule
import org.koin.core.context.startKoin

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import org.example.globalconsole.presesentation.view.components.OskOverlay

/**
 * Punto de entrada principal para la aplicación de escritorio GlobalConsole.
 * Realiza la inyección manual de dependencias y lanza la interfaz Compose con el
 * grafo de navegación [AppNavHost] y el soporte de mandos GLFW.
 *
 * Incluye una ventana flotante superior (Always on Top) para el teclado en pantalla
 * que permite escribir sobre navegadores y aplicaciones externas suspendidas mediante START + R2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
fun main() = application {
    // Inicialización de Koin
    val koin = remember {
        startKoin {
            modules(dataModule, domainModule, presentationModule)
        }.koin
    }

    // Resolver dependencias desde el contenedor
    val viewModel = remember(koin) { koin.get<HomeViewModel>() }
    val settingsViewModel = remember(koin) { koin.get<SettingsViewModel>() }

    // El gestor es único para la aplicación
    val gamepadManager = remember { GamepadManager() }
    val appearance by viewModel.appearance.collectAsState()
    val isOskOverlayVisible by gamepadManager.isOskOverlayVisible.collectAsState()

    Window(
        onCloseRequest = ::exitApplication,
        title = "GlobalConsole",
    ) {
        val coroutineScope = rememberCoroutineScope()
        val homeState by viewModel.uiState.collectAsState()
        val gameRunning = homeState is HomeUiState.GameRunning
        var wasGameRunning by remember { mutableStateOf(false) }
        LaunchedEffect(gameRunning) {
            if (wasGameRunning && !gameRunning) {
                window.toFront()
                window.requestFocus()
            }
            wasGameRunning = gameRunning
        }
        
        // Sincronizar la sensibilidad del ratón en tiempo real
        val mouseSensitivity by settingsViewModel.mouseSensitivity.collectAsState()
        LaunchedEffect(mouseSensitivity) {
            gamepadManager.mouseSensitivity = mouseSensitivity
        }

        // Ciclo de vida del gamepad acoplado a la ventana
        DisposableEffect(Unit) {
            gamepadManager.start(coroutineScope)
            onDispose {
                gamepadManager.stop()
            }
        }

        val navController = rememberNavController()
        MetroTheme(appearance) {
            AppNavHost(
                navController = navController,
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                gamepadManager = gamepadManager
            )
        }
    }

    // Ventana flotante de teclado en pantalla sobre navegadores y juegos externos
    Window(
        onCloseRequest = { gamepadManager.hideOskOverlay() },
        title = "GlobalConsole - Teclado",
        visible = isOskOverlayVisible,
        alwaysOnTop = true,
        focusable = false,
        undecorated = true,
        resizable = false,
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.BottomCenter),
            size = DpSize(1000.dp, 440.dp)
        )
    ) {
        if (isOskOverlayVisible) {
            MetroTheme(appearance) {
                OskOverlay(
                    gamepadManager = gamepadManager,
                    onConfirm = { text ->
                        gamepadManager.injectTextToSystem(text, pressEnter = true)
                    },
                    onDismiss = {
                        gamepadManager.hideOskOverlay()
                    },
                    onExternalDelete = gamepadManager::sendBackspaceToSystem
                )
            }
        }
    }
}
