package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Resuelve ejecutable y argumentos sin interpretar un comando de shell.
 * @property repository Persistencia de rutas.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class BuildEmulatorCommandUseCase(private val repository: SettingsRepository) {
    /**
     * Prioriza la ruta configurada y conserva el destino automático por sistema.
     * @param target Emulador a ejecutar.
     * @param osName Nombre del sistema operativo.
     * @param gamePath Archivo de juego; null abre el launcher.
     * @return Argumentos separados para ProcessBuilder.
     * @throws IllegalArgumentException Si el sistema o el juego no son válidos.
     * @throws Exception Si falla la lectura de configuración.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    suspend operator fun invoke(target: EmulatorTarget, osName: String, gamePath: String? = null): List<String> {
        val os = osName.lowercase()
        require(os.contains("linux") || os.contains("windows")) { "Sistema no soportado: $osName" }
        require(gamePath == null || gamePath.isNotBlank()) { "Ruta de juego vacía" }
        val configured = GetEmulatorExecutableUseCase(repository)(target)
        val command = when {
            configured.isNotEmpty() -> listOf(configured)
            os.contains("windows") -> listOf(target.windowsExecutable)
            else -> listOf("flatpak", "run", target.flatpakId)
        }
        if (gamePath == null) return command
        val arguments = when (target) {
            EmulatorTarget.PCSX2 -> listOf("-fullscreen", "-nogui")
            EmulatorTarget.DUCKSTATION -> listOf("-fullscreen")
            EmulatorTarget.DOLPHIN -> listOf("-b", "-e")
            EmulatorTarget.MELONDS, EmulatorTarget.AZAHAR -> listOf("-f")
            EmulatorTarget.RPCS3 -> error("RPCS3 se abre como launcher")
        }
        return command + arguments + gamePath
    }
}
