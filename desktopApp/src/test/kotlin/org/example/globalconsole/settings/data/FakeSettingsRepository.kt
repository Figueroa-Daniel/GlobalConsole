package org.example.globalconsole.settings.data

import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Implementación falsa de [SettingsRepository] para uso exclusivo en tests.
 * Almacena las rutas en memoria, sin acceso a disco.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class FakeSettingsRepository : SettingsRepository {

    private val paths = mutableMapOf<String, String>()

    /**
     * Guarda la ruta asociada al emulador indicado en memoria.
     *
     * @param emulatorId Identificador del emulador.
     * @param path Ruta del directorio de juegos.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    override suspend fun saveEmulatorPath(emulatorId: String, path: String) {
        paths[emulatorId] = path
    }

    /**
     * Recupera la ruta guardada para el emulador indicado.
     *
     * @param emulatorId Identificador del emulador.
     * @return La ruta guardada o null si no existe.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    override suspend fun getEmulatorPath(emulatorId: String): String? = paths[emulatorId]
}
