package org.example.globalconsole.melonDS.data.repository

interface MelonDSRepository {
    suspend fun executeLauncher():Boolean
    suspend fun closeLauncher():Boolean
}