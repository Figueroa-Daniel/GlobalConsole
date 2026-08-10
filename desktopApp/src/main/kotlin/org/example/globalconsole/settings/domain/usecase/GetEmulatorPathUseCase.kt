package org.example.globalconsole.settings.domain.usecase

import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso para recuperar la ruta del directorio de juegos de un emulador.
 * Delega directamente al repositorio sin lógica adicional.
 *
 * @param repository Contrato de persistencia de configuración.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class GetEmulatorPathUseCase(
    private val repository: SettingsRepository
) {

    /**
     * Recupera la ruta configurada para el emulador indicado.
     *
     * @param emulatorId Identificador único del emulador (ej. "pcsx2").
     * @return La ruta configurada, o null si no existe.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    suspend operator fun invoke(emulatorId: String): String? =
        repository.getEmulatorPath(emulatorId)
}
