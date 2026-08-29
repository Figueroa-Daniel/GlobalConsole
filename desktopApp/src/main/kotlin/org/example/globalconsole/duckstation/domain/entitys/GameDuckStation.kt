package org.example.globalconsole.duckstation.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa un juego de PlayStation 1 ejecutable con DuckStation.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
data class GameDuckStation(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String?,
    override val platform: Platforms = Platforms.DUCKSTATION
) : Game(id, name, urlGameExecute, image, platform)
