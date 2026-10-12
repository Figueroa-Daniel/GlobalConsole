package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.EmulatorTarget
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Recupera el ejecutable sin mezclarlo con la carpeta de juegos.
 * @property repository Persistencia compartida.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class GetEmulatorExecutableUseCase(private val repository: SettingsRepository) {
    /**
     * Lee la ruta o devuelve vacío para selección automática.
     * @param target Emulador configurado.
     * @return Ruta configurada.
     * @throws Exception Si falla la persistencia.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    suspend operator fun invoke(target: EmulatorTarget): String =
        repository.getEmulatorPath("executable.${target.id}").orEmpty().trim()
}
