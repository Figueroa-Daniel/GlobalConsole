package org.example.globalconsole.melonDS.domain.usecase

import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.melonDS.data.repository.MelonDSRepository
import org.example.globalconsole.melonDS.domain.entitys.MelonDSLauncher

/**
 * UseCase para obtener el DTO del launcher de Melon DS y mapearlo a su entidad de dominio [MelonDSLauncher].
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class ShowMelonDSLauncherUseCase(private val repository: MelonDSRepository) {
    suspend operator fun invoke(): MelonDSLauncher {
        val dto = repository.showMelonDSLauncher()
        return MelonDSLauncher(
            id = dto.id,
            name = dto.name,
            urlGameExecute = dto.urlGameExecute,
            image = dto.image,
            platform = Platforms.MELONDS
        )
    }
}
