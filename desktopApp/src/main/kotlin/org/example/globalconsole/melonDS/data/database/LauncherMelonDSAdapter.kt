package org.example.globalconsole.melonDS.data.database

import org.example.globalconsole.settings.ROUTE_MELONDS_EXECUTABLE

/**
 * Adaptador encargado de controlar la ejecución nativa y ciclo de vida del Launcher del emulador Melon DS.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class LauncherMelonDSAdapter {

    private var currentProcess: Process? = null

    /**
     * Ejecutará el launcher dependiendo del SO (Linux/Windows).
     *
     * @return true si se inició el proceso correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
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
            ROUTE_MELONDS_EXECUTABLE ?: "melonds.exe"
        )
        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            currentProcess = processBuilder.start()
            println("Launched MelonDS Launcher with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching MelonDS on Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun executeLauncherForLinuxFlatpak(): Boolean {
        val command = listOf(
            "flatpak",
            "run",
            "net.kuribo64.melonDS"
        )
        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            currentProcess = processBuilder.start()
            println("Launched MelonDS Launcher with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching MelonDS Flatpak: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Cerrará el launcher destruyendo el proceso actual.
     * Esta acción debe ser invocada preferiblemente al pulsar el botón Home del mando.
     *
     * @return true si el proceso se destruyó correctamente, false si no había proceso activo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend fun closeLauncher(): Boolean {
        return try {
            if (currentProcess?.isAlive == true) {
                currentProcess?.descendants()?.forEach { it.destroyForcibly() }
                currentProcess?.destroyForcibly()
                println("MelonDS Launcher process destroyed.")
                currentProcess = null
                true
            } else {
                println("No active MelonDS Launcher process found.")
                false
            }
        } catch (e: Exception) {
            System.err.println("Error destroying MelonDS process: ${e.message}")
            e.printStackTrace()
            false
        }
    }

}