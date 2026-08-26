package org.example.globalconsole.azahar.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa un juego de Nintendo 3DS gestionado por el emulador Azahar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
data class Game3DS(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String?,
    override val platform: Platforms
) : Game(id, name, urlGameExecute, image, platform)
