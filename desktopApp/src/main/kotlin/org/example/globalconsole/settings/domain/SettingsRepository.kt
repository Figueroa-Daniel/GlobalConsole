package org.example.globalconsole.settings.domain

/**
 * Contrato del repositorio encargado de gestionar la persistencia de las rutas
 * de los emuladores configurados por el usuario.
 * Pertenece a la capa de dominio y es agnóstico al mecanismo de almacenamiento concreto.
 *
 * La gestión de preferencias específicas de launchers externos (como Heroic Games Launcher)
 * se delega a los repositorios propios de cada módulo.
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
}
