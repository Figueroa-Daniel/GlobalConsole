package org.example.globalconsole.HeroicGames.data.mappers

import org.example.globalconsole.HeroicGames.data.dto.HGLauncherDto
import org.example.globalconsole.HeroicGames.domain.entitys.HGLauncher
import org.example.globalconsole.generalDomain.entititys.Platforms

fun HGLauncherDto.toDomain() =
    HGLauncher(
        id = id,
        name = name,
        urlGameExecute = urlGameExecute,
        image = image,
        platform = Platforms.HEORIC_GAMES_LAUCHER
    )
