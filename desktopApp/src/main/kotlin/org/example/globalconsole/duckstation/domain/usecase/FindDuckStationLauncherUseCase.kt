package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.DuckStationRepository

/**
 * UseCase para verificar si el launcher de DuckStation está habilitado.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class FindDuckStationLauncherUseCase(private val repository: DuckStationRepository) {
    /**
     * Consulta si DuckStation está habilitado en la configuración.
     *
     * @return True si está habilitado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(): Boolean {
        return repository.isDuckStationEnabled()
    }
}
