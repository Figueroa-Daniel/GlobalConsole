package org.example.globalconsole.juegosPcsx2.data.database

import org.example.globalconsole.settings.ROUTE_PCSX2_GAMES

/**
 * Adaptador de sistema de emulación encargado de la ejecución nativa del emulador PCSX2.
 * Controla el ciclo de vida del proceso de emulación y detecta el sistema operativo subyacente.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class GamePCSX2Adapter {
    /**
     * Inicia la ejecución de una ISO de PS2 determinando el sistema operativo actual (Linux o Windows).
     *
     * @param executeUrl Ruta absoluta del archivo ISO a ejecutar.
     * @return True si el proceso del emulador se inició correctamente, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
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
     * Ejecuta el emulador PCSX2 en Windows utilizando el binario local y argumentos de pantalla completa.
     *
     * @param executeUrl Ruta absoluta del archivo ISO a ejecutar.
     * @return True si el proceso se inició correctamente, false en caso de error.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    private fun executeGameForWindows(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Windows game execution URL is null or blank.")
            return false
        }
        // Assuming PCSX2 is installed at "C:\Program Files\PCSX2\pcsx2-qt.exe"
        val command = listOf(
            "$ROUTE_PCSX2_GAMES/pcsx2-qt.exe",
            "-fullscreen",
            "-nogui",
            executeUrl
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO() // This will make the launched process use the same stdin/stdout/stderr as the current process
            processBuilder.start()
            println("Launched PCSX2 with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching PCSX2 on Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Ejecuta el emulador PCSX2 en Linux utilizando Flatpak y argumentos de pantalla completa.
     *
     * @param executeUrl Ruta absoluta del archivo ISO a ejecutar.
     * @return True si el proceso se inició correctamente, false en caso de error.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    private fun executeGameForLinuxFlatpak(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) {
            println("Linux Flatpak game execution URL is null or blank.")
            return false
        }
        val command = listOf(
            "flatpak",
            "run",
            "net.pcsx2.PCSX2",
            "-fullscreen",
            "-nogui",
            executeUrl
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO() // This will make the launched process use the same stdin/stdout/stderr as the current process
            processBuilder.start()
            println("Launched PCSX2 with command: ${command.joinToString(" ")}")
            true
        } catch (e: Exception) {
            System.err.println("Error launching PCSX2: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}