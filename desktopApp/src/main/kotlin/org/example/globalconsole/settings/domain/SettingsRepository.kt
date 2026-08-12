package org.example.globalconsole.settings.domain

/**
 * Contrato del repositorio encargado de gestionar la persistencia de las rutas
 * de los emuladores configurados por el usuario y las preferencias de launchers externos.
 * Pertenece a la capa de dominio y es agnóstico al mecanismo de almacenamiento concreto.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
interface SettingsRepository {

    /**
     * Persiste la ruta del directorio de juegos asociada a un emulador específico.
     *
     * @param emulatorId Identificador único del emulador (ej. "pcsx2").
     * @param path Ruta absoluta al directorio de juegos.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    suspend fun saveEmulatorPath(emulatorId: String, path: String)

    /**
     * Recupera la ruta del directorio de juegos asociada a un emulador específico.
     *
     * @param emulatorId Identificador único del emulador (ej. "pcsx2").
     * @return La ruta configurada, o null si no se ha configurado ninguna.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    suspend fun getEmulatorPath(emulatorId: String): String?

    /**
     * Persiste la preferencia del usuario para mostrar u ocultar Heroic Games Launcher
     * en la biblioteca principal de GlobalConsole.
     *
     * @param enabled True para mostrar el launcher en la biblioteca, false para ocultarlo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    suspend fun saveHeroicEnabled(enabled: Boolean)

    /**
     * Recupera la preferencia del usuario sobre si Heroic Games Launcher debe aparecer
     * en la biblioteca. Por defecto retorna false si no se ha configurado ningún valor.
     *
     * @return True si Heroic Games Launcher está habilitado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    suspend fun isHeroicEnabled(): Boolean
}

