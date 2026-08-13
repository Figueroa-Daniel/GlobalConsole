package org.example.globalconsole.HeroicGames.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

data class HGLauncher(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String? = null,
    override val platform: Platforms
) : Game(id, name, urlGameExecute, image, platform) {
}