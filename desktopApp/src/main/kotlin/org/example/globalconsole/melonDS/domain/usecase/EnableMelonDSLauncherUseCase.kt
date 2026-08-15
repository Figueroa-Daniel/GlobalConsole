package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * UseCase para habilitar Melon DS Launcher en la biblioteca principal.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class EnableMelonDSLauncherUseCase(private val repository: MelonDSRepository) {
    suspend operator fun invoke() {
        repository.saveMelonDSEnabled(true)
    }
}
