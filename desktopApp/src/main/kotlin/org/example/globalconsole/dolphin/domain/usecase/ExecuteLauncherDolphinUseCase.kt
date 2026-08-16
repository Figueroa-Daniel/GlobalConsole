package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.DolphinRepository

/**
 * UseCase para ejecutar el launcher de Dolphin en modo interfaz.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class ExecuteLauncherDolphinUseCase(private val repository: DolphinRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.executeLauncher()
    }
}
