package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Persiste el ejecutable sin mezclarlo con la carpeta de juegos.
 * @property repository Persistencia compartida.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class SaveEmulatorExecutableUseCase(private val repository: SettingsRepository) {
    /**
     * Guarda una ruta; vacío restaura la selección automática.
     * @param target Emulador configurado.
     * @param path Ruta del archivo, sin argumentos.
     * @return Unit.
     * @throws Exception Si falla la persistencia; IllegalArgumentException si la ruta contiene caracteres de control.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    suspend operator fun invoke(target: EmulatorTarget, path: String) {
        require(path.none { it == '\u0000' || it == '\n' || it == '\r' }) { "Ruta de ejecutable inválida" }
        repository.saveEmulatorPath("executable.${target.id}", path.trim())
    }
}
