package org.example.globalconsole.navigatorWeb.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.generalDomain.entititys.Platforms

data class NavWebObj(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String? = null,
    override val platform: Platforms = Platforms.NAVWEB,
    val navigatorType: NavigatorsWeb
): Game(id, name, urlGameExecute, image, platform)
