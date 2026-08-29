package org.example.globalconsole.azahar.data.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object que representa el Launcher de Azahar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
@Serializable
data class AzaharLauncherDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null
)
