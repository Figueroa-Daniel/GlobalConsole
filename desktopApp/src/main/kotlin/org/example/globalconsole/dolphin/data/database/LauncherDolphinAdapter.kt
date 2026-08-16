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
            currentProcess?.let { process ->
                process.descendants().forEach { it.destroyForcibly() }
                process.destroyForcibly()
            }
            currentProcess = null

            // Fallback robusto a nivel de SO para asegurar el cierre (Dolphin a veces sobrevive al destroy)
            val os = System.getProperty("os.name").lowercase()
            if (os.contains("linux")) {
                Runtime.getRuntime().exec(arrayOf("flatpak", "kill", "org.DolphinEmu.dolphin-emu"))
                Runtime.getRuntime().exec(arrayOf("killall", "-9", "dolphin-emu"))
            } else if (os.contains("windows")) {
                Runtime.getRuntime().exec(arrayOf("taskkill", "/IM", "Dolphin.exe", "/F"))
            }

            println("Dolphin Launcher process termination requested.")
            true
        } catch (e: Exception) {
            System.err.println("Error destroying Dolphin process: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
