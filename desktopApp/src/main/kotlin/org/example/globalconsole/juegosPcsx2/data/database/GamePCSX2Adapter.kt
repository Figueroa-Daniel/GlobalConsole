package org.example.globalconsole.juegosPcsx2.data.database

import kotlinx.coroutines.CancellationException
import org.example.globalconsole.settings.data.SettingsRepositoryImpl
import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.usecase.BuildEmulatorCommandUseCase
import java.io.File

/**
 * Ejecuta PCSX2 con la ruta persistida y conserva el ciclo de vida del proceso.
 * @property commandUseCase Resuelve ejecutable y argumentos para el sistema actual.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class GamePCSX2Adapter(
    private val commandUseCase: BuildEmulatorCommandUseCase = BuildEmulatorCommandUseCase(SettingsRepositoryImpl())
) {
    private var activeProcess: Process? = null

    /**
     * Inicia el juego y espera su finalización sin usar un shell.
     * @param executeUrl Ruta del juego.
     * @return True si el proceso termina correctamente; false si no puede ejecutarse.
     * @throws CancellationException Si se cancela la operación.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    suspend fun executeGame(executeUrl: String?): Boolean {
        if (executeUrl.isNullOrBlank()) return false
        return try {
            val command = commandUseCase(EmulatorTarget.PCSX2, System.getProperty("os.name"), executeUrl)
            val builder = ProcessBuilder(command).inheritIO()
            File(command.first()).takeIf { it.isAbsolute }?.parentFile?.let { builder.directory(it) }
            val process = builder.start()
            activeProcess = process
            process.waitFor() == 0
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            System.err.println("No se pudo ejecutar PCSX2: ${e.message}")
            false
        } finally {
            activeProcess = null
        }
    }

    /**
     * Cierra forzosamente el proceso del emulador si está en ejecución.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
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
}
