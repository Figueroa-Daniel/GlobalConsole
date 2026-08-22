package org.example.globalconsole.PS3Launcher.data.repository

import org.example.globalconsole.PS3Launcher.data.dto.PS3LauncherDto

/**
 * Contrato del repositorio para gestionar los datos y la ejecución
 * del launcher de PS3 (RPCS3).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
interface PS3LauncherRepository {

    /**
     * Recupera el DTO con los datos del launcher.
     */
    suspend fun showPS3Launcher(): PS3LauncherDto

    /**
     * Oculta el launcher de la biblioteca persistiendo el cambio en la configuración.
     */
    suspend fun hidePS3Launcher(): Boolean

    /**
     * Ejecuta el proceso nativo del launcher.
     */
    suspend fun executePS3Launcher(): Boolean

    /**
     * Cierra el proceso del launcher.
     */
    suspend fun closeLauncher(): Boolean

    /**
     * Verifica si el launcher está habilitado para mostrarse en la biblioteca.
     */
    suspend fun isPS3Enabled(): Boolean

    /**
     * Persiste la preferencia de visibilidad del launcher.
     */
    suspend fun savePS3Enabled(enabled: Boolean)
}
