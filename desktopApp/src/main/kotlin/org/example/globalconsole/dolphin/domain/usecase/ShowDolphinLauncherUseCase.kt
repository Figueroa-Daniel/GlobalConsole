package org.example.globalconsole.dolphin.domain.usecase

import org.example.globalconsole.dolphin.data.dto.DolphinLauncherDto
import org.example.globalconsole.dolphin.data.repository.DolphinRepository
import org.example.globalconsole.dolphin.domain.entitys.DolphinLauncher
import org.example.globalconsole.dolphin.data.mappers.toDomain

/**
 * UseCase para obtener la información del launcher de Dolphin para ser mostrada.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class ShowDolphinLauncherUseCase(private val repository: DolphinRepository) {
    suspend operator fun invoke(): DolphinLauncher {
        return repository.showDolphinLauncher().toDomain()
    }
}
