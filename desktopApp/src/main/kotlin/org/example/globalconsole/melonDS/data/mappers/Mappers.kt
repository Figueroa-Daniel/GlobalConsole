package org.example.globalconsole.melonDS.data.mappers

import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.juegosPcsx2.data.dto.GameP2Dto
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2
import org.example.globalconsole.melonDS.data.dto.GameDsDto
import org.example.globalconsole.melonDS.domain.entitys.GameDS

fun GameDsDto.toDomain() =
    GameDS(
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = Platforms.PCSX2
    )


fun List<GameDsDto>.toDomain(): List<GameDS> {
    return map { it.toDomain() }
}