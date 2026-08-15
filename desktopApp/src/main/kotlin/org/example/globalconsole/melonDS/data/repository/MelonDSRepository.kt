package org.example.globalconsole.melonDS.data.repository

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
}