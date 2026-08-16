package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.DolphinRepository

/**
 * UseCase para habilitar el launcher de Dolphin en la biblioteca.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class EnableDolphinLauncherUseCase(private val repository: DolphinRepository) {
    suspend operator fun invoke() {
        repository.saveDolphinEnabled(true)
    }
}
