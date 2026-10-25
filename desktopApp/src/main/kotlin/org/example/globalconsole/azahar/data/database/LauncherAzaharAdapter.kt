package org.example.globalconsole.azahar.data.database

import kotlinx.coroutines.CancellationException
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.usecase.BuildEmulatorCommandUseCase
import java.io.File

/**
 * Ejecuta AZAHAR con la ruta persistida y conserva el ciclo de vida del proceso.
 * @property commandUseCase Resuelve ejecutable y argumentos para el sistema actual.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class LauncherAzaharAdapter(
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
            val command = commandUseCase(EmulatorTarget.AZAHAR, System.getProperty("os.name"))
            val builder = ProcessBuilder(command).inheritIO()
            File(command.first()).takeIf { it.isAbsolute }?.parentFile?.let { builder.directory(it) }
            val process = builder.start()
            activeProcess = process
            process.waitFor() == 0
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            System.err.println("No se pudo ejecutar AZAHAR: ${e.message}")
            false
        } finally {
            activeProcess = null
        }
    }

    /**
     * Cerrará el launcher destruyendo el proceso actual.
     * Esta acción debe ser invocada preferiblemente al pulsar el botón Home del mando.
     *
     * @return true si el proceso se destruyó correctamente, false si no había proceso activo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend fun closeLauncher(): Boolean {
        return try {
            if (activeProcess?.isAlive == true) {
                activeProcess?.descendants()?.forEach { it.destroyForcibly() }
                activeProcess?.destroyForcibly()
                println("Azahar Launcher process destroyed.")
                activeProcess = null
                true
            } else {
                println("No active Azahar Launcher process found.")
                false
            }
        } catch (e: Exception) {
            System.err.println("Error destroying Azahar process: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
