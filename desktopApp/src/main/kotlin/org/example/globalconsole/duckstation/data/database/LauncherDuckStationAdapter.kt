package org.example.globalconsole.duckstation.data.database

import kotlinx.coroutines.CancellationException
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.usecase.BuildEmulatorCommandUseCase
import java.io.File

/**
 * Ejecuta DUCKSTATION con la ruta persistida y conserva el ciclo de vida del proceso.
 * @property commandUseCase Resuelve ejecutable y argumentos para el sistema actual.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class LauncherDuckStationAdapter(
    private val commandUseCase: BuildEmulatorCommandUseCase = BuildEmulatorCommandUseCase(SettingsRepositoryImpl())
) {
    private var activeProcess: Process? = null

    /**
     * Inicia el launcher y espera su finalización sin usar un shell.
     * @return True si el proceso termina correctamente; false si no puede ejecutarse.
     * @throws CancellationException Si se cancela la operación.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    suspend fun executeLauncher(): Boolean {
        return try {
            val command = commandUseCase(EmulatorTarget.DUCKSTATION, System.getProperty("os.name"))
            val builder = ProcessBuilder(command).inheritIO()
            File(command.first()).takeIf { it.isAbsolute }?.parentFile?.let { builder.directory(it) }
            val process = builder.start()
            activeProcess = process
            process.waitFor() == 0
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            System.err.println("No se pudo ejecutar DUCKSTATION: ${e.message}")
            false
        } finally {
            activeProcess = null
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

            println("DuckStation Launcher process termination requested.")
            true
        } catch (e: Exception) {
            System.err.println("Error destroying DuckStation process: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
