package org.example.globalconsole.azahar.domain.usecase

import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.azahar.data.repository.AzaharRepository
import org.example.globalconsole.azahar.domain.entitys.AzaharLauncher

/**
 * UseCase para obtener el DTO del launcher de Azahar y mapearlo a su entidad de dominio [AzaharLauncher].
 *
 * @property repository Repositorio de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class ShowAzaharLauncherUseCase(private val repository: AzaharRepository) {
    /**
     * Obtiene los datos del launcher como entidad de dominio.
     *
     * @return Entidad [AzaharLauncher] con la información del launcher.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend operator fun invoke(): AzaharLauncher {
        val dto = repository.showAzaharLauncher()
        return AzaharLauncher(
            id = dto.id,
            name = dto.name,
            urlGameExecute = dto.urlGameExecute,
            image = dto.image,
            platform = Platforms.AZAHAR
        )
    }
}
