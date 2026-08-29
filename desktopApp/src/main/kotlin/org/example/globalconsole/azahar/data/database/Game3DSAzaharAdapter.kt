package org.example.globalconsole.azahar.data.database

import org.example.globalconsole.settings.ROUTE_AZAHAR_EXECUTABLE

/**
 * Adaptador encargado de la ejecución nativa de los juegos para el emulador Azahar (Nintendo 3DS).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class Game3DSAzaharAdapter {

    private var activeProcess: Process? = null

    /**
     * Inicia la ejecución de una ROM de 3DS determinando el sistema operativo actual (Linux o Windows).
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return True si el proceso del emulador se inició y terminó correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
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
     * @since 2026-08-26
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

    /**
     * Ejecuta el emulador Azahar en Windows utilizando el binario local y argumentos de pantalla completa/ruta.
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return True si el proceso se ejecutó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    private fun executeGameForWindows(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Windows game execution URL is null or blank.")
            return false
        }
        val command = listOf(
            ROUTE_AZAHAR_EXECUTABLE ?: "azahar.exe",
            "-f",
            executeUrl
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched Azahar with command: ${command.joinToString(" ")}")

            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching Azahar on Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Ejecuta el emulador Azahar en Linux utilizando Flatpak y argumentos de pantalla completa/ruta.
     *
     * @param executeUrl Ruta absoluta del archivo ROM a ejecutar.
     * @return True si el proceso se ejecutó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    private fun executeGameForLinuxFlatpak(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Linux Flatpak game execution URL is null or blank.")
            return false
        }
        val command = listOf(
            "flatpak",
            "run",
            "org.azahar_emu.Azahar",
            "-f",
            executeUrl
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Launched Azahar with command: ${command.joinToString(" ")}")

            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error launching Azahar Flatpak: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
