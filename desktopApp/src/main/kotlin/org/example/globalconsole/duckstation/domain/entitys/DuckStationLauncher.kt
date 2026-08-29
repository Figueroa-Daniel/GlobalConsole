package org.example.globalconsole.duckstation.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa el Launcher de DuckStation como un "Juego"
 * en la biblioteca principal.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
data class DuckStationLauncher(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String? = null,
    override val platform: Platforms = Platforms.DUCKSTATION
) : Game(id, name, urlGameExecute, image, platform)
