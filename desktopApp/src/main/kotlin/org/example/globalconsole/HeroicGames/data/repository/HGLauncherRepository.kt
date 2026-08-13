package org.example.globalconsole.HeroicGames.data.repository

import org.example.globalconsole.HeroicGames.data.dto.HGLauncherDto

/**
 * Contrato del repositorio de Heroic Games Launcher.
 * Gestiona la detección, visibilidad, ejecución y preferencias de usuario
 * relacionadas exclusivamente con este launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-12
 */
interface HGLauncherRepository {

    /**
     * Comprueba si Heroic Games Launcher está instalado en el sistema operativo actual.
     *
     * @return True si el launcher está instalado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    suspend fun isHGLauncherInstalled(): Boolean

    /**
     * Persiste la preferencia del usuario para ocultar Heroic Games Launcher
     * de la biblioteca principal.
     *
     * @return True si la operación se realizó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    suspend fun hideHGLauncher(): Boolean

    /**
     * Retorna el DTO con los datos del launcher listos para ser mapeados a dominio.
     *
     * @return [HGLauncherDto] con los datos del launcher.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    suspend fun showHGLauncher(): HGLauncherDto

    /**
     * Delega la ejecución del proceso nativo de Heroic Games Launcher.
     *
     * @return True si el launcher se ejecutó y cerró correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    suspend fun executeHGLauncher(): Boolean

    /**
     * Recupera la preferencia del usuario sobre si Heroic Games Launcher
     * debe aparecer en la biblioteca principal de GlobalConsole.
     *
     * @return True si el launcher está habilitado, false por defecto.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    suspend fun isHeroicEnabled(): Boolean

    /**
     * Persiste la preferencia del usuario sobre si Heroic Games Launcher
     * debe mostrarse u ocultarse en la biblioteca principal.
     *
     * @param enabled True para mostrar el launcher en la biblioteca, false para ocultarlo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    suspend fun saveHeroicEnabled(enabled: Boolean)
}