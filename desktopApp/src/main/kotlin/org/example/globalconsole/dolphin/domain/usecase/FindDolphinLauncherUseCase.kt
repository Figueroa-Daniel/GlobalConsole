package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.DolphinRepository

/**
 * UseCase para verificar si el launcher de Dolphin está habilitado.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class FindDolphinLauncherUseCase(private val repository: DolphinRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.isDolphinEnabled()
    }
}
