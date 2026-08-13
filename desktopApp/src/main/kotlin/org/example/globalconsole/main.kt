package org.example.globalconsole

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Punto de entrada principal para la aplicación de escritorio GlobalConsole.
 * Realiza la inyección manual de dependencias y lanza la interfaz Compose con el
 * grafo de navegación [AppNavHost] y el soporte de mandos GLFW.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
fun main() = application {
    // Inicialización de Koin
    startKoin {
        modules(
            dataModule,
            domainModule,
            presentationModule
        )
    }

    // Resolver dependencias desde el contenedor
    val viewModel = getKoin().get<HomeViewModel>()
    val settingsViewModel = getKoin().get<SettingsViewModel>()

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
            settingsViewModel = settingsViewModel,
            gamepadManager = gamepadManager
        )
    }
}

