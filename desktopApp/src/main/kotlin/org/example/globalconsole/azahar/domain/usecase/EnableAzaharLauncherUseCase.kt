package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.AzaharRepository

/**
 * UseCase para habilitar Azahar Launcher en la biblioteca principal.
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class EnableAzaharLauncherUseCase(private val repository: AzaharRepository) {
    /**
     * Habilita el launcher de Azahar en la biblioteca.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke() {
        repository.saveAzaharEnabled(true)
    }
}
