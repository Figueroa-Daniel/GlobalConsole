package org.example.globalconsole.dolphin.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa el Launcher de Dolphin como un "Juego"
 * en la biblioteca principal.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
data class DolphinLauncher(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String? = null,
    override val platform: Platforms = Platforms.DOLPHIN
) : Game(id, name, urlGameExecute, image, platform)
