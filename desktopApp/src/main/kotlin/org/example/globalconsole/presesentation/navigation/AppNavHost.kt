package org.example.globalconsole.presesentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.view.screen.HomeScreen
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel

/**
 * Grafo de navegación principal de la aplicación GlobalConsole.
 * Orquesta todos los destinos de la aplicación a través de un [NavHost] de Compose Multiplatform.
 *
 * Para añadir una nueva pantalla:
 * 1. Declarar la ruta en [AppRoutes].
 * 2. Añadir un bloque `composable<AppRoutes.NuevaRuta> { ... }` aquí.
 * 3. Pasar el [NavHostController] a la pantalla si necesita navegar a otros destinos.
 *
 * @param navController Controlador de navegación que gestiona la pila de destinos.
 * @param viewModel ViewModel de la pantalla principal, inyectado desde [main.kt].
 * @param gamepadManager Gestor de gamepad físico opcional para navegación por hardware.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: HomeViewModel,
    gamepadManager: GamepadManager? = null
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home
    ) {
        composable<AppRoutes.Home> {
            HomeScreen(
                viewModel = viewModel,
                gamepadManager = gamepadManager
            )
        }

        // TODO: Añadir nuevas pantallas conforme se creen:
        // composable<AppRoutes.GameDetail> { backStackEntry ->
        //     val route: AppRoutes.GameDetail = backStackEntry.toRoute()
        //     GameDetailScreen(gameId = route.gameId)
        // }
        // composable<AppRoutes.Settings> {
        //     SettingsScreen()
        // }
    }
}

