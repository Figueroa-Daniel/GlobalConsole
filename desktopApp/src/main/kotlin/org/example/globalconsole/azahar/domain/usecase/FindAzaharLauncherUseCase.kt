package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.AzaharRepository

/**
 * UseCase para consultar si Azahar Launcher está habilitado en la biblioteca principal.
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class FindAzaharLauncherUseCase(private val repository: AzaharRepository) {
    /**
     * Consulta si el launcher de Azahar está habilitado.
     *
     * @return True si está habilitado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(): Boolean {
        return repository.isAzaharEnabled()
    }
}
