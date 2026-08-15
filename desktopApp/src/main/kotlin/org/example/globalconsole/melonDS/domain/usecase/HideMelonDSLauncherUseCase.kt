package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * UseCase para ocultar Melon DS Launcher de la biblioteca principal.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class HideMelonDSLauncherUseCase(private val repository: MelonDSRepository) {
    suspend operator fun invoke() {
        repository.saveMelonDSEnabled(false)
    }
}
