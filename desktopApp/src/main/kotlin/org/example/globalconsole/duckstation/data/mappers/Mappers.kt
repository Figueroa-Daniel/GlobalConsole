package org.example.globalconsole.duckstation.data.mappers

import org.example.globalconsole.duckstation.data.dto.DuckStationLauncherDto
import org.example.globalconsole.duckstation.domain.entitys.DuckStationLauncher

/**
 * Función de extensión para mapear un [DuckStationLauncherDto] a un [DuckStationLauncher].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
fun DuckStationLauncherDto.toDomain(): DuckStationLauncher {
    return DuckStationLauncher(
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = this.platform
    )
}
