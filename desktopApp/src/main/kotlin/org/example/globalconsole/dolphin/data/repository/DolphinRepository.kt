package org.example.globalconsole.dolphin.data.repository

import org.example.globalconsole.dolphin.data.dto.DolphinLauncherDto

/**
 * Repositorio para la gestión de Dolphin (Wii/GameCube).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
interface DolphinRepository {
    suspend fun executeLauncher(): Boolean
    suspend fun closeLauncher(): Boolean
    suspend fun executeGame(executeUrl: String?): Boolean

    suspend fun isDolphinEnabled(): Boolean
    suspend fun saveDolphinEnabled(enabled: Boolean)
    suspend fun showDolphinLauncher(): DolphinLauncherDto
}
