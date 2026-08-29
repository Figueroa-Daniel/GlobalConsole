package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.azahar.data.repository.AzaharRepository

/**
 * UseCase para ocultar Azahar Launcher de la biblioteca principal.
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class HideAzaharLauncherUseCase(private val repository: AzaharRepository) {
    /**
     * Oculta el launcher de Azahar de la biblioteca.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke() {
        repository.saveAzaharEnabled(false)
    }
}
