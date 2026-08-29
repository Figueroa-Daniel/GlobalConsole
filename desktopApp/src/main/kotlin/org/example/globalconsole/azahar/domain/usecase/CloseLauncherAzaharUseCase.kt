package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.AzaharRepository

/**
 * Caso de uso para cerrar la ejecución del Launcher de Azahar.
 * Se espera que sea invocado por la UI al pulsar el botón Home del mando.
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class CloseLauncherAzaharUseCase(private val repository: AzaharRepository) {
    /**
     * Cierra el proceso del launcher.
     *
     * @return true si se cerró correctamente, false en caso de error o si no estaba abierto.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeLauncher()
    }
}
