package org.example.globalconsole.settings.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.settings.domain.SettingsRepository
import java.io.File

/**
 * Implementación de [SettingsRepository] que persiste las rutas de emuladores
 * en el archivo `config.json` compartido de la aplicación.
 *
 * Utiliza [AppConfig] como modelo de serialización unificado para garantizar que
 * las escrituras de rutas de emuladores no destruyan otros campos del archivo
 * gestionados por repositorios de otros módulos (ej. `heroicEnabled` de `HGLauncherRepositoryImpl`).
 *
 * **Patrón de escritura segura:**
 * 1. Lee el archivo completo y deserializa en [AppConfig].
 * 2. Aplica el cambio únicamente en el campo `emulatorPaths`.
 * 3. Reescribe el objeto completo con todos los demás campos intactos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 * @updated 2026-08-13
 */
class SettingsRepositoryImpl : SettingsRepository {

    private val configFile = File("config.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Persiste la ruta asociada al emulador indicado en el archivo `config.json`,
     * conservando el resto de campos del archivo.
     *
     * @param emulatorId Identificador único del emulador (ej. "pcsx2").
     * @param path Ruta absoluta del directorio de juegos.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    override suspend fun saveEmulatorPath(emulatorId: String, path: String) {
        val current = readConfig()
        val updated = current.copy(
            emulatorPaths = current.emulatorPaths + (emulatorId to path)
        )
        configFile.writeText(json.encodeToString(updated))
    }

    /**
     * Recupera la ruta configurada para el emulador indicado desde el archivo `config.json`.
     *
     * @param emulatorId Identificador único del emulador (ej. "pcsx2").
     * @return La ruta configurada o null si no existe o el archivo no se puede leer.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    override suspend fun getEmulatorPath(emulatorId: String): String? =
        readConfig().emulatorPaths[emulatorId]

    /**
     * Lee y deserializa el archivo `config.json` en [AppConfig].
     * Si el archivo no existe o no puede parsearse, retorna una instancia por defecto.
     *
     * @return La configuración actual o una instancia vacía de [AppConfig].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    private fun readConfig(): AppConfig {
        if (!configFile.exists()) return AppConfig()
        return try {
            json.decodeFromString<AppConfig>(configFile.readText())
        } catch (e: Exception) {
            AppConfig()
        }
    }
}
