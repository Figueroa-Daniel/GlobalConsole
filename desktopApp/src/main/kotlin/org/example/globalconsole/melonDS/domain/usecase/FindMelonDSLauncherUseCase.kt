package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * UseCase para consultar si Melon DS Launcher está habilitado en la biblioteca principal.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class FindMelonDSLauncherUseCase(private val repository: MelonDSRepository) {
    suspend operator fun invoke(): Boolean {
        return repository.isMelonDSEnabled()
    }
}
