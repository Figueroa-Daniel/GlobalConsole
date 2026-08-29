package org.example.globalconsole.duckstation.data.dto

import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Data Transfer Object para el Launcher de DuckStation.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
data class DuckStationLauncherDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String?,
    val platform: Platforms = Platforms.DUCKSTATION
)
