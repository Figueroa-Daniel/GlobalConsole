package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.DolphinRepository

/**
 * UseCase para cerrar el launcher de Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class CloseLauncherDolphinUseCase(private val repository: DolphinRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.closeLauncher()
    }
}
