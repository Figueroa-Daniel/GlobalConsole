package org.example.globalconsole.duckstation.data.database

import org.example.globalconsole.settings.ROUTE_DUCKSTATION_EXECUTABLE

/**
 * Adaptador encargado de la ejecución nativa de los juegos para el emulador DuckStation.
 * Soporta AppImage (Linux), Flatpak (Linux fallback) y Windows de forma modular.
 * Si [ROUTE_DUCKSTATION_EXECUTABLE] está definida se usa como ruta directa (AppImage/custom);
 * en caso contrario, en Linux se usa Flatpak como fallback automático.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class GameDuckStationAdapter {
    private var activeProcess: Process? = null

    /**
     * Inicia la ejecución de una ROM de PlayStation 1.
     *
     * @param executeUrl Ruta absoluta del archivo a ejecutar.
     * @return True si el proceso del emulador se inició y terminó correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    fun executeGame(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Game execution URL is null or blank.")
            return false
        }

        val os = System.getProperty("os.name").lowercase()

        return if (os.contains("linux")) {
            executeGameForLinux(executeUrl)
        } else if (os.contains("windows")) {
            executeGameForWindows(executeUrl)
        } else {
            println("Unsupported operating system: $os")
            false
        }
    }

    /**
     * Cierra forzosamente el proceso del emulador si está en ejecución.
     * Incluye fallback a nivel de SO para garantizar el cierre completo del proceso.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    fun closeProcess(): Boolean {
        return try {
            activeProcess?.let { process ->
                process.descendants().forEach { it.destroyForcibly() }
                process.destroyForcibly()
            }
            activeProcess = null

            val os = System.getProperty("os.name").lowercase()
            if (os.contains("linux")) {
                Runtime.getRuntime().exec(arrayOf("flatpak", "kill", "org.duckstation.DuckStation"))
                Runtime.getRuntime().exec(arrayOf("killall", "-9", "duckstation-qt"))
            } else if (os.contains("windows")) {
                Runtime.getRuntime().exec(arrayOf("taskkill", "/IM", "duckstation-qt-x64-ReleaseLTCG.exe", "/F"))
                Runtime.getRuntime().exec(arrayOf("taskkill", "/IM", "DuckStation.exe", "/F"))
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun executeGameForWindows(executeUrl: String): Boolean {
        val command = listOf(
            ROUTE_DUCKSTATION_EXECUTABLE ?: "DuckStation.exe",
            "-fullscreen",
            executeUrl
        )
        return executeCommand(command)
    }

    private fun executeGameForLinux(executeUrl: String): Boolean {
        val command = if (!ROUTE_DUCKSTATION_EXECUTABLE.isNullOrBlank()) {
            listOf(ROUTE_DUCKSTATION_EXECUTABLE!!, "-fullscreen", executeUrl)
        } else {
            listOf("flatpak", "run", "org.duckstation.DuckStation", "-fullscreen", executeUrl)
        }
        return executeCommand(command)
    }

    private fun executeCommand(command: List<String>): Boolean {
        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched DuckStation with command: ${command.joinToString(" ")}")
            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching DuckStation: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
