package org.example.globalconsole.PS3Launcher.data.database

/**
 * Adaptador de lanzador encargado de la ejecución nativa de RPCS3 (Emulador de PS3).
 * Controla el ciclo de vida del proceso y detecta el sistema operativo subyacente
 * para seleccionar el método de lanzamiento correcto.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
open class LauncherPS3Adapter {
    private var activeProcess: Process? = null

    /**
     * Cierra forzosamente el proceso del emulador si está en ejecución.
     *
     * @return True si se cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-22
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
     * Punto de entrada público. Detecta el sistema operativo actual y delega la ejecución
     * al método correspondiente para Linux o Windows.
     *
     * @return True si RPCS3 se ejecutó y cerró correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-22
     */
    open fun executeLauncher(): Boolean {
        val os = System.getProperty("os.name").lowercase()

        return when {
            os.contains("linux") -> {
                if (!isInstalledOnLinux()) {
                    println("RPCS3 no está instalado en este sistema Linux (Flatpak).")
                    false
                } else {
                    executeOnLinux()
                }
            }
            os.contains("windows") -> {
                // TODO: Ejecución en Windows pendiente de implementar
                println("La ejecución de RPCS3 en Windows no está soportada actualmente.")
                false
            }
            else -> {
                println("Sistema operativo no soportado: $os")
                false
            }
        }
    }

    /**
     * Verifica si RPCS3 está instalado como Flatpak en Linux.
     *
     * @return True si el paquete Flatpak está instalado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-22
     */
    private fun isInstalledOnLinux(): Boolean {
        return try {
            val process = ProcessBuilder("flatpak", "info", "net.rpcs3.RPCS3")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (e: Exception) {
            System.err.println("Error al verificar la instalación en Linux: ${e.message}")
            false
        }
    }

    /**
     * Lanza RPCS3 en Linux mediante Flatpak.
     * Bloquea el hilo de ejecución hasta que el proceso se cierre.
     *
     * @return True si el proceso se inició y finalizó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-22
     */
    private fun executeOnLinux(): Boolean {
        val command = listOf("flatpak", "run", "net.rpcs3.RPCS3")

        return try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.inheritIO()
            val process = processBuilder.start()
            activeProcess = process
            println("RPCS3 iniciado con: ${command.joinToString(" ")}")

            process.waitFor()
            activeProcess = null
            true
        } catch (e: Exception) {
            System.err.println("Error al lanzar RPCS3 en Linux: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
