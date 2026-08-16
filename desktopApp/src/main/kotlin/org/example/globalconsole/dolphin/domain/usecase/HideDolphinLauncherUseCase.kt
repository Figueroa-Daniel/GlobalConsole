package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.repository.DolphinRepository

/**
 * UseCase para ocultar el launcher de Dolphin de la biblioteca.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class HideDolphinLauncherUseCase(private val repository: DolphinRepository) {
    suspend operator fun invoke() {
        repository.saveDolphinEnabled(false)
    }
}
