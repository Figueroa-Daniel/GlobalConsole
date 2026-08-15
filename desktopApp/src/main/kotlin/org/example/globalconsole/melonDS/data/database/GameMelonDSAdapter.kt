package org.example.globalconsole.melonDS.data.database

import org.example.globalconsole.settings.ROUTE_MELONDS_EXECUTABLE

/**
 * Adaptador encargado de la ejecución nativa de los juegos para el emulador Melon DS.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class GameMelonDSAdapter {
    /**
     * Inicia la ejecución de una ROM de DS/DSi determinando el sistema operativo actual (Linux o Windows).
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return True si el proceso del emulador se inició y terminó correctamente, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
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

    private var activeProcess: Process? = null

    /**
     * Cierra forzosamente el proceso del emulador si está en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    fun closeProcess(): Boolean {
        return try {
            activeProcess?.destroy()
            activeProcess = null
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Ejecuta el emulador Melon DS en Windows utilizando el binario local y argumentos de pantalla completa/ruta.
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return True si el proceso se ejecutó correctamente, false en caso de error.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    private fun executeGameForWindows(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Windows game execution URL is null or blank.")
            return false
        }
        val command = listOf(
            ROUTE_MELONDS_EXECUTABLE ?: "melonds.exe",
            "-f",
            executeUrl
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched MelonDS with command: ${command.joinToString(" ")}")
            
            // Bloquea el hilo actual hasta que el emulador se cierre
            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching MelonDS on Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Ejecuta el emulador Melon DS en Linux utilizando Flatpak y argumentos de pantalla completa/ruta.
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return True si el proceso se ejecutó correctamente, false en caso de error.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    private fun executeGameForLinuxFlatpak(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Linux Flatpak game execution URL is null or blank.")
            return false
        }
        val command = listOf(
            "flatpak",
            "run",
            "net.kuribo64.melonDS",
            "-f",
            executeUrl
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched MelonDS with command: ${command.joinToString(" ")}")
            
            // Bloquea el hilo actual hasta que el emulador se cierre
            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching MelonDS Flatpak: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}