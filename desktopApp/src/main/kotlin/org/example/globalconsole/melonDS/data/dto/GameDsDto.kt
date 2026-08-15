package org.example.globalconsole.melonDS.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameDsDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null,
)
