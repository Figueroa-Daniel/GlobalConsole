package org.example.globalconsole.melonDS.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa el Launcher de Melon DS como un "Juego"
 * en la biblioteca principal.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
data class MelonDSLauncher(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String? = null,
    override val platform: Platforms
) : Game(id, name, urlGameExecute, image, platform)
