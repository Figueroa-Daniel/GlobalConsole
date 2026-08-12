package org.example.globalconsole.settings.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.settings.domain.SettingsRepository
import java.io.File

/**
 * Implementación de [SettingsRepository] que persiste las rutas de emuladores
 * en un archivo `config.json` en el directorio de trabajo de la aplicación.
 * Usa `kotlinx.serialization` para la serialización del mapa de rutas.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class SettingsRepositoryImpl : SettingsRepository {

    /**
     * Modelo de datos serializable que representa el contenido del archivo config.json.
     *
     * @param emulatorPaths Mapa de identificadores de emulador a rutas de directorio
     *        y preferencias booleanas codificadas como strings (ej. "heroic_enabled").
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    @Serializable
    private data class SettingsConfig(
        val emulatorPaths: Map<String, String> = emptyMap()
    )

    private val configFile = File("config.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Persiste la ruta asociada al emulador indicado en el archivo `config.json`.
     * Si el archivo no existe, lo crea. Si ya contiene rutas, las conserva.
     *
     * @param emulatorId Identificador único del emulador.
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
     * @param emulatorId Identificador único del emulador.
     * @return La ruta configurada o null si no existe o el archivo no se puede leer.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    override suspend fun getEmulatorPath(emulatorId: String): String? =
        readConfig().emulatorPaths[emulatorId]

    /**
     * Persiste la preferencia de Heroic Games Launcher en el mapa de configuración
     * usando la clave reservada `"heroic_enabled"` con valor `"true"` o `"false"`.
     *
     * @param enabled True si el launcher debe mostrarse en la biblioteca.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    override suspend fun saveHeroicEnabled(enabled: Boolean) {
        val current = readConfig()
        val updated = current.copy(
            emulatorPaths = current.emulatorPaths + ("heroic_enabled" to enabled.toString())
        )
        configFile.writeText(json.encodeToString(updated))
    }

    /**
     * Recupera la preferencia de Heroic Games Launcher desde el archivo `config.json`.
     * Retorna false por defecto si la clave no existe o el archivo no está disponible.
     *
     * @return True si Heroic Games Launcher está habilitado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    override suspend fun isHeroicEnabled(): Boolean =
        readConfig().emulatorPaths["heroic_enabled"] == "true"

    /**
     * Lee y deserializa el archivo `config.json`. Si no existe o no puede parsearse,
     * retorna una configuración vacía por defecto.
     *
     * @return La configuración actual o una instancia vacía de [SettingsConfig].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    private fun readConfig(): SettingsConfig {
        if (!configFile.exists()) return SettingsConfig()
        return try {
            json.decodeFromString<SettingsConfig>(configFile.readText())
        } catch (e: Exception) {
            SettingsConfig()
        }
    }
}
