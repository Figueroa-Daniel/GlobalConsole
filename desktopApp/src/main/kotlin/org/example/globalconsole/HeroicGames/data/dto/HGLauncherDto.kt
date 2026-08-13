package org.example.globalconsole.HeroicGames.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HGLauncherDto(
    val id:String = "HGLauncher",
    val name: String = "Heroic Games",
    val urlGameExecute: String,
    val image: String? = null,
)
