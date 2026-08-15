package org.example.globalconsole.melonDS.data.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object que representa el Launcher de Melon DS.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
@Serializable
data class MelonDSLauncherDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null
)
