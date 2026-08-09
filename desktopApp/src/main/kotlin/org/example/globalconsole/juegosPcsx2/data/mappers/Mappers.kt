package org.example.globalconsole.juegosPcsx2.data.mappers

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.juegosPcsx2.data.dto.GameP2Dto
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Convierte un objeto de transferencia de datos [GameP2Dto] en una entidad de dominio [GameP2].
 *
 * @return La entidad de dominio [GameP2] mapeada.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
fun GameP2Dto.toDomain() =
    GameP2(
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = Platforms.PCSX2
    )

/**
 * Convierte una lista de objetos de transferencia de datos [GameP2Dto] en una lista de entidades de dominio [GameP2].
 *
 * @return Una lista de entidades de dominio [GameP2].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
fun List<GameP2Dto>.toDomain(): List<GameP2> {
    return map { it.toDomain() }
}