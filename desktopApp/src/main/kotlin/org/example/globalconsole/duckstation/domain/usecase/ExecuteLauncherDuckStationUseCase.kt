package org.example.globalconsole.duckstation.domain.usecase

import org.example.globalconsole.duckstation.data.repository.DuckStationRepository

/**
 * UseCase para ejecutar el launcher de DuckStation sin cargar ningún juego.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class ExecuteLauncherDuckStationUseCase(private val repository: DuckStationRepository) {
    /**
     * Lanza el emulador DuckStation en modo standalone.
     *
     * @return True si el proceso se inició correctamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend operator fun invoke(): Boolean {
        return repository.executeLauncher()
    }
}
