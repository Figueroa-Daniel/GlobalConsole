package org.example.globalconsole.azahar.data.mappers

import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.azahar.data.dto.Game3DSDto
import org.example.globalconsole.azahar.domain.entitys.Game3DS

/**
 * Mapea un [Game3DSDto] a su entidad de dominio [Game3DS].
 *
 * @return Entidad de dominio con la plataforma [Platforms.AZAHAR].
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
fun Game3DSDto.toDomain() =
    Game3DS(
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = Platforms.AZAHAR
    )

/**
 * Mapea una lista de [Game3DSDto] a una lista de entidades de dominio [Game3DS].
 *
 * @return Lista de entidades de dominio.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
fun List<Game3DSDto>.toDomain(): List<Game3DS> {
    return map { it.toDomain() }
}
