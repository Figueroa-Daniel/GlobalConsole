package org.example.globalconsole.azahar.data.repository

import org.example.globalconsole.azahar.data.dto.AzaharLauncherDto

/**
 * Repositorio para la gestión del launcher y configuración de Azahar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
interface AzaharRepository {
    suspend fun executeLauncher(): Boolean
    suspend fun closeLauncher(): Boolean
    suspend fun executeGame(executeUrl: String?): Boolean

    suspend fun isAzaharEnabled(): Boolean
    suspend fun saveAzaharEnabled(enabled: Boolean)
    suspend fun showAzaharLauncher(): AzaharLauncherDto
}
