package org.example.globalconsole.dolphin.data.database

import org.example.globalconsole.settings.ROUTE_DOLPHIN_EXECUTABLE

/**
 * Adaptador encargado de la ejecución nativa de los juegos para el emulador Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class GameDolphinAdapter {
    private var activeProcess: Process? = null

    /**
     * Inicia la ejecución de una ROM de Wii/GameCube.
     *
     * @param executeUrl Ruta absoluta del archivo a ejecutar.
     * @return True si el proceso del emulador se inició y terminó correctamente.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-16
     */
    fun executeGame(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Game execution URL is null or blank.")
            return false
        }

        val os = System.getProperty("os.name").lowercase()

        return if (os.contains("linux")) {
            executeGameForLinuxFlatpak(executeUrl)
        } else if (os.contains("windows")) {
            executeGameForWindows(executeUrl)
        } else {
            println("Unsupported operating system: $os")
            false
        }
    }

    /**
     * Cierra forzosamente el proceso del emulador si está en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-16
     */
    fun closeProcess(): Boolean {
        return try {
            activeProcess?.let { process ->
                process.descendants().forEach { it.destroyForcibly() }
                process.destroyForcibly()
            }
            activeProcess = null
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun executeGameForWindows(executeUrl: String?): Boolean {
        val command = listOf(
            ROUTE_DOLPHIN_EXECUTABLE ?: "Dolphin.exe",
            "-b",
            "-e",
            executeUrl!!
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched Dolphin with command: ${command.joinToString(" ")}")

            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching Dolphin on Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun executeGameForLinuxFlatpak(executeUrl: String?): Boolean {
        val command = listOf(
            "flatpak",
            "run",
            "org.DolphinEmu.dolphin-emu",
            "-b",
            "-e",
            executeUrl!!
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched Dolphin with command: ${command.joinToString(" ")}")

            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching Dolphin Flatpak: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
