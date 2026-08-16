package org.example.globalconsole.dolphin.data.mappers

import org.example.globalconsole.dolphin.data.dto.DolphinLauncherDto
import org.example.globalconsole.dolphin.domain.entitys.DolphinLauncher

/**
 * Función de extensión para mapear un [DolphinLauncherDto] a un [DolphinLauncher].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
fun DolphinLauncherDto.toDomain(): DolphinLauncher {
    return DolphinLauncher(
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = this.platform
    )
}
