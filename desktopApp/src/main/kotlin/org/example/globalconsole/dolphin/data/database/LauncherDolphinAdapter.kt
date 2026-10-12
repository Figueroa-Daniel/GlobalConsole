package org.example.globalconsole.dolphin.data.database

import kotlinx.coroutines.CancellationException
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.usecase.BuildEmulatorCommandUseCase
import java.io.File

/**
 * Ejecuta DOLPHIN con la ruta persistida y conserva el ciclo de vida del proceso.
 * @property commandUseCase Resuelve ejecutable y argumentos para el sistema actual.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class LauncherDolphinAdapter(
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
            val command = commandUseCase(EmulatorTarget.DOLPHIN, System.getProperty("os.name"))
            val builder = ProcessBuilder(command).inheritIO()
            File(command.first()).takeIf { it.isAbsolute }?.parentFile?.let { builder.directory(it) }
            val process = builder.start()
            activeProcess = process
            process.waitFor() == 0
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            System.err.println("No se pudo ejecutar DOLPHIN: ${e.message}")
            false
        } finally {
            activeProcess = null
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
            activeProcess?.let { process ->
                process.descendants().forEach { it.destroyForcibly() }
                process.destroyForcibly()
            }
            activeProcess = null

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
