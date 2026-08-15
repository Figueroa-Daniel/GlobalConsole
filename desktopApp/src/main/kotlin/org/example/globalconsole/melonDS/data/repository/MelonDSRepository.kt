package org.example.globalconsole.melonDS.data.repository

import org.example.globalconsole.melonDS.data.dto.MelonDSLauncherDto

/**
 * Repositorio para la gestión de Melon DS.
 * 
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
interface MelonDSRepository {
    suspend fun executeLauncher(): Boolean
    suspend fun closeLauncher(): Boolean
    suspend fun executeGame(executeUrl: String?): Boolean
    
    suspend fun isMelonDSEnabled(): Boolean
    suspend fun saveMelonDSEnabled(enabled: Boolean)
    suspend fun showMelonDSLauncher(): MelonDSLauncherDto
}