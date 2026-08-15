package org.example.globalconsole.HeroicGames.data.database

/**
 * Adaptador de lanzador encargado de la ejecución nativa de Heroic Games Launcher.
 * Controla el ciclo de vida del proceso y detecta el sistema operativo subyacente
 * para seleccionar el método de verificación y lanzamiento correcto.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-12
 */
open class LauncherHeroicGamesAdapter {
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
     * Punto de entrada público. Detecta el sistema operativo actual y delega la ejecución
     * al método correspondiente para Linux o Windows.
     *
     * @return True si Heroic Games Launcher se ejecutó y cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    open fun executeLauncher(): Boolean {
        val os = System.getProperty("os.name").lowercase()

        return when {
            os.contains("linux") -> {
                if (!isInstalledOnLinux()) {
                    println("Heroic Games Launcher no está instalado en este sistema Linux.")
                    false
                } else {
                    executeOnLinux()
                }
            }
            os.contains("windows") -> {
                if (!isInstalledOnWindows()) {
                    println("Heroic Games Launcher no está instalado en este sistema Windows.")
                    false
                } else {
                    executeOnWindows()
                }
            }
            else -> {
                println("Sistema operativo no soportado: $os")
                false
            }
        }
    }

    /**
     * Verifica si Heroic Games Launcher está instalado como Flatpak en Linux.
     * Ejecuta `flatpak info com.heroicgameslauncher.hgl` y comprueba el código de salida.
     *
     * @return True si el paquete Flatpak está instalado (exit code 0), false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    private fun isInstalledOnLinux(): Boolean {
        return try {
            val process = ProcessBuilder("flatpak", "info", "com.heroicgameslauncher.hgl")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (e: Exception) {
            System.err.println("Error al verificar la instalación en Linux: ${e.message}")
            false
        }
    }

    /**
     * Lanza Heroic Games Launcher en Linux mediante Flatpak con el flag de pantalla completa.
     * Bloquea el hilo de ejecución hasta que el proceso del launcher se cierre.
     *
     * @return True si el proceso se inició y finalizó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    private fun executeOnLinux(): Boolean {
        val command = listOf(
            "flatpak", "run", "com.heroicgameslauncher.hgl", "--fullscreen"
        )

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Heroic Games Launcher iniciado con: ${command.joinToString(" ")}")

            // Bloquea el hilo actual hasta que el launcher se cierre
            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error al lanzar Heroic en Linux: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Verifica si Heroic Games Launcher está instalado en Windows comprobando la existencia
     * del ejecutable en la ruta de instalación estándar mediante un comando de consola.
     *
     * @return True si el ejecutable existe, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    private fun isInstalledOnWindows(): Boolean {
        val command = listOf(
            "cmd", "/c",
            """if exist "%LocalAppData%\Programs\heroic\Heroic.exe" (echo 1) else (echo 0)"""
        )

        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            output == "1"
        } catch (e: Exception) {
            System.err.println("Error al verificar la instalación en Windows: ${e.message}")
            false
        }
    }

    /**
     * Lanza Heroic Games Launcher en Windows usando la ruta absoluta del ejecutable
     * con el flag de pantalla completa. Bloquea el hilo hasta que el proceso termine.
     *
     * @return True si el proceso se inició y finalizó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    private fun executeOnWindows(): Boolean {
        val heroicPath = System.getenv("LOCALAPPDATA") + "\\Programs\\heroic\\Heroic.exe"
        val command = listOf(heroicPath, "--fullscreen")

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("Heroic Games Launcher iniciado con: ${command.joinToString(" ")}")

            // Bloquea el hilo actual hasta que el launcher se cierre
            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error al lanzar Heroic en Windows: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}