package org.example.globalconsole.PS3Launcher.data.mappers

import org.example.globalconsole.PS3Launcher.data.dto.PS3LauncherDto
import org.example.globalconsole.PS3Launcher.domain.entitys.PS3Launcher

/**
 * Extensión para mapear un objeto [PS3LauncherDto] de la capa de datos
 * a una entidad [PS3Launcher] de la capa de dominio.
 *
 * @return La entidad [PS3Launcher] equivalente.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
fun PS3LauncherDto.toDomain() = PS3Launcher(
    id = id,
    name = name,
    urlGameExecute = urlGameExecute,
    image = image
)
