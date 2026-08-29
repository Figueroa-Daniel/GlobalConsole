package org.example.globalconsole.duckstation.data.database

import org.example.globalconsole.settings.ROUTE_DUCKSTATION_EXECUTABLE

/**
 * Adaptador encargado de controlar la ejecución nativa y ciclo de vida del Launcher de DuckStation.
 * Soporta AppImage (Linux), Flatpak (Linux fallback) y Windows.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class LauncherDuckStationAdapter {

    private var currentProcess: Process? = null

    /**
     * Ejecuta el launcher de DuckStation sin cargar ningún juego, dependiendo del SO.
     *
     * @return True si se inició el proceso correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend fun executeLauncher(): Boolean {
        val os = System.getProperty("os.name").lowercase()

        return if (os.contains("linux")) {
            executeLauncherForLinux()
        } else if (os.contains("windows")) {
            executeLauncherForWindows()
        } else {
            println("Unsupported operating system: $os")
            false
        }
    }

    private fun executeLauncherForWindows(): Boolean {
        val command = listOf(ROUTE_DUCKSTATION_EXECUTABLE ?: "DuckStation.exe")
        return launchProcess(command)
    }

    private fun executeLauncherForLinux(): Boolean {
        val command = if (!ROUTE_DUCKSTATION_EXECUTABLE.isNullOrBlank()) {
            listOf(ROUTE_DUCKSTATION_EXECUTABLE!!)
        } else {
            listOf("flatpak", "run", "org.duckstation.DuckStation")
        }
        return launchProcess(command)
    }

    private fun launchProcess(command: List<String>): Boolean {
        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            currentProcess = processBuilder.start()
            println("Launched DuckStation Launcher with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching DuckStation Launcher: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Cierra el launcher destruyendo el proceso actual con fallback a nivel de SO.
     *
     * @return True si el proceso se destruyó correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend fun closeLauncher(): Boolean {
        return try {
            currentProcess?.let { process ->
                process.descendants().forEach { it.destroyForcibly() }
                process.destroyForcibly()
            }
            currentProcess = null

            val os = System.getProperty("os.name").lowercase()
            if (os.contains("linux")) {
                Runtime.getRuntime().exec(arrayOf("flatpak", "kill", "org.duckstation.DuckStation"))
                Runtime.getRuntime().exec(arrayOf("killall", "-9", "duckstation-qt"))
            } else if (os.contains("windows")) {
                Runtime.getRuntime().exec(arrayOf("taskkill", "/IM", "duckstation-qt-x64-ReleaseLTCG.exe", "/F"))
                Runtime.getRuntime().exec(arrayOf("taskkill", "/IM", "DuckStation.exe", "/F"))
            }

            println("DuckStation Launcher process termination requested.")
            true
        } catch (e: Exception) {
            System.err.println("Error destroying DuckStation process: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
