package org.example.globalconsole

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

/**
 * Punto de entrada de la aplicación de escritorio de GlobalConsole.
 * Configura la ventana principal del sistema de Compose Desktop e invoca el composable raíz [App].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "globalconsole",
    ) {
        App()
    }
}