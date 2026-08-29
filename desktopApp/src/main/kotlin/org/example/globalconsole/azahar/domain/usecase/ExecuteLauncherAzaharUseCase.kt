package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.AzaharRepository

/**
 * Caso de uso para iniciar la ejecución del Launcher de Azahar.
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class ExecuteLauncherAzaharUseCase(private val repository: AzaharRepository) {
    /**
     * Inicia el proceso del launcher.
     *
     * @return true si se inició correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(): Boolean {
        return repository.executeLauncher()
    }
}
