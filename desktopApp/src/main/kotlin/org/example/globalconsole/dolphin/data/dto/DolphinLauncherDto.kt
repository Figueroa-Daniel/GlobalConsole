package org.example.globalconsole.dolphin.data.dto

import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Data Transfer Object para el Launcher de Dolphin.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
data class DolphinLauncherDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String?,
    val platform: Platforms = Platforms.DOLPHIN
)
