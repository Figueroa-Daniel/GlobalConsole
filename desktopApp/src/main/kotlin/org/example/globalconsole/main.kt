package org.example.globalconsole

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController
import org.example.globalconsole.juegosPcsx2.data.database.GameP2FileSystemAdapter
import org.example.globalconsole.juegosPcsx2.data.database.GamePCSX2Adapter
import org.example.globalconsole.juegosPcsx2.data.repositoryImpl.GameP2RepositoryImpl
import org.example.globalconsole.juegosPcsx2.domain.usecase.DeleteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.ExecuteGameP2UseCase
import org.example.globalconsole.juegosPcsx2.domain.usecase.GetGamesP2UseCase
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.navigation.AppNavHost
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel

/**
 * Punto de entrada principal para la aplicación de escritorio GlobalConsole.
 * Realiza la inyección manual de dependencias y lanza la interfaz Compose con el
 * grafo de navegación [AppNavHost] y el soporte de mandos GLFW.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
fun main() = application {
    // Inyección manual de dependencias de PCSX2
    val fileSystemAdapter = GameP2FileSystemAdapter()
    val pcsx2Adapter = GamePCSX2Adapter()
    val repository = GameP2RepositoryImpl(fileSystemAdapter, pcsx2Adapter)

    val getGamesP2UseCase = GetGamesP2UseCase(repository)
    val executeGameP2UseCase = ExecuteGameP2UseCase(repository)
    val deleteGameP2UseCase = DeleteGameP2UseCase(repository)

    val viewModel = HomeViewModel(
        getGamesP2UseCase = getGamesP2UseCase,
        executeGameP2UseCase = executeGameP2UseCase,
        deleteGameP2UseCase = deleteGameP2UseCase
    )

    // El gestor es único para la aplicación
    val gamepadManager = remember { GamepadManager() }

    Window(
        onCloseRequest = ::exitApplication,
        title = "GlobalConsole",
    ) {
        val coroutineScope = rememberCoroutineScope()
        
        // Ciclo de vida del gamepad acoplado a la ventana
        DisposableEffect(Unit) {
            gamepadManager.start(coroutineScope)
            onDispose {
                gamepadManager.stop()
            }
        }

        val navController = rememberNavController()
        AppNavHost(
            navController = navController,
            viewModel = viewModel,
            gamepadManager = gamepadManager
        )
    }
}

