package org.example.globalconsole.dolphin.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa un juego de Wii o GameCube ejecutable con Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
data class GameDolphin(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String?,
    override val platform: Platforms = Platforms.DOLPHIN
) : Game(id, name, urlGameExecute, image, platform)
