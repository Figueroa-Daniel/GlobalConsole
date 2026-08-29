package org.example.globalconsole.duckstation.data.repository

import org.example.globalconsole.duckstation.data.dto.DuckStationLauncherDto

/**
 * Repositorio para la gestión del emulador DuckStation (PlayStation 1).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
interface DuckStationRepository {
    suspend fun executeLauncher(): Boolean
    suspend fun closeLauncher(): Boolean
    suspend fun executeGame(executeUrl: String?): Boolean

    suspend fun isDuckStationEnabled(): Boolean
    suspend fun saveDuckStationEnabled(enabled: Boolean)
    suspend fun showDuckStationLauncher(): DuckStationLauncherDto
}
