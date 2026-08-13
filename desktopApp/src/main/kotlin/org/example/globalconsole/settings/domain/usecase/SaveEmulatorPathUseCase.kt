package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso para persistir la ruta del directorio de juegos de un emulador.
 * Valida que tanto el identificador del emulador como la ruta no sean vacíos
 * antes de delegar al repositorio.
 *
 * @param repository Contrato de persistencia de configuración.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class SaveEmulatorPathUseCase(
    private val repository: SettingsRepository
) {

    /**
     * Valida y persiste la ruta del emulador indicado.
     *
     * @param emulatorId Identificador único del emulador (ej. "pcsx2").
     * @param path Ruta absoluta al directorio de juegos.
     * @throws IllegalArgumentException si el emulatorId o la ruta están vacíos o en blanco.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    suspend operator fun invoke(emulatorId: String, path: String) {
        require(emulatorId.isNotBlank()) { "El identificador del emulador no puede estar vacío." }
        repository.saveEmulatorPath(emulatorId, path)
    }
}
