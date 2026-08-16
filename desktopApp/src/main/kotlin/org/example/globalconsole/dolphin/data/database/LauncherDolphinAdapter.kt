package org.example.globalconsole.dolphin.data.database

import org.example.globalconsole.settings.ROUTE_DOLPHIN_EXECUTABLE

/**
 * Adaptador encargado de controlar la ejecución nativa y ciclo de vida del Launcher de Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class LauncherDolphinAdapter {

    private var currentProcess: Process? = null

    /**
     * Ejecutará el launcher dependiendo del SO (Linux/Windows).
     *
     * @return true si se inició el proceso correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-16
     */
    suspend fun executeLauncher(): Boolean {
        val os = System.getProperty("os.name").lowercase()

        return if (os.contains("linux")) {
            executeLauncherForLinuxFlatpak()
        } else if (os.contains("windows")) {
            executeLauncherForWindows()
        } else {
            println("Unsupported operating system: $os")
            false
        }
    }

    private fun executeLauncherForWindows(): Boolean {
        val command = listOf(
            ROUTE_DOLPHIN_EXECUTABLE ?: "Dolphin.exe"
        )
        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            currentProcess = processBuilder.start()
            println("Launched Dolphin Launcher with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching Dolphin on Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun executeLauncherForLinuxFlatpak(): Boolean {
        val command = listOf(
            "flatpak",
            "run",
            "org.DolphinEmu.dolphin-emu"
        )
        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            currentProcess = processBuilder.start()
            println("Launched Dolphin Launcher with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching Dolphin Flatpak: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Cerrará el launcher destruyendo el proceso actual.
     *
     * @return true si el proceso se destruyó correctamente, false si no había proceso activo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-16
     */
    suspend fun closeLauncher(): Boolean {
        return try {
            if (currentProcess?.isAlive == true) {
                currentProcess?.descendants()?.forEach { it.destroyForcibly() }
                currentProcess?.destroyForcibly()
                println("Dolphin Launcher process destroyed.")
                currentProcess = null
                true
            } else {
                println("No active Dolphin Launcher process found.")
                false
            }
        } catch (e: Exception) {
            System.err.println("Error destroying Dolphin process: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
