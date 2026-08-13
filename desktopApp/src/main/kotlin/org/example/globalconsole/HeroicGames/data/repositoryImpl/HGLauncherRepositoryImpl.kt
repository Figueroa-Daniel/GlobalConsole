package org.example.globalconsole.HeroicGames.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.HeroicGames.data.database.LauncherHeroicGamesAdapter
import org.example.globalconsole.HeroicGames.data.dto.HGLauncherDto
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository
import org.example.globalconsole.config.AppConfig
import java.io.File

/**
 * Implementación de [HGLauncherRepository] que gestiona los datos del launcher,
 * la preferencia de visibilidad del usuario y la ejecución nativa del proceso.
 *
 * Utiliza [AppConfig] como modelo de serialización unificado para garantizar que
 * las escrituras de la preferencia `heroicEnabled` no destruyan otros campos del
 * archivo `config.json` gestionados por repositorios de otros módulos
 * (ej. `emulatorPaths` de `SettingsRepositoryImpl`).
 *
 * **Patrón de escritura segura:**
 * 1. Lee el archivo completo y deserializa en [AppConfig].
 * 2. Aplica el cambio únicamente en el campo `heroicEnabled`.
 * 3. Reescribe el objeto completo con todos los demás campos intactos.
 *
 * @property adapter Adaptador responsable de la ejecución nativa de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class HGLauncherRepositoryImpl(
    private val adapter: LauncherHeroicGamesAdapter
) : HGLauncherRepository {

    private val configFile = File("config.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override suspend fun isHGLauncherInstalled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Persiste la preferencia de ocultación de Heroic Games Launcher
     * llamando a [saveHeroicEnabled] con `false`.
     *
     * @return True si la operación se realizó correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun hideHGLauncher(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            saveHeroicEnabled(false)
            true
        } catch (e: Exception) {
            System.err.println("Error al ocultar Heroic Games Launcher: ${e.message}")
            false
        }
    }

    /**
     * Retorna el DTO con los datos estáticos de Heroic Games Launcher.
     * El [HGLauncherDto.urlGameExecute] referencia el ID de la aplicación Flatpak,
     * que es el punto de entrada canónico del launcher en Linux.
     *
     * @return [HGLauncherDto] con los datos del launcher.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun showHGLauncher(): HGLauncherDto = withContext(Dispatchers.IO) {
        HGLauncherDto(
            id = "heroic-launcher",
            name = "Heroic Games",
            urlGameExecute = "com.heroicgameslauncher.hgl"
        )
    }

    /**
     * Delega la ejecución de Heroic Games Launcher al adaptador nativo del sistema.
     *
     * @return True si el launcher se inició y cerró correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun executeHGLauncher(): Boolean = withContext(Dispatchers.IO) {
        adapter.executeLauncher()
    }

    /**
     * Recupera la preferencia de visibilidad de Heroic Games Launcher desde `config.json`.
     * Retorna false por defecto si la clave no existe o el archivo no está disponible.
     *
     * @return True si el launcher está habilitado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun isHeroicEnabled(): Boolean = withContext(Dispatchers.IO) {
        readConfig().heroicEnabled
    }

    /**
     * Persiste la preferencia de visibilidad de Heroic Games Launcher en `config.json`,
     * conservando el resto de campos del archivo (ej. rutas de emuladores).
     *
     * @param enabled True para mostrar el launcher en la biblioteca, false para ocultarlo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun saveHeroicEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = readConfig()
        val updated = current.copy(heroicEnabled = enabled)
        configFile.writeText(json.encodeToString(updated))
    }

    /**
     * Lee y deserializa el archivo `config.json` en [AppConfig].
     * Si el archivo no existe o no puede parsearse, retorna una instancia por defecto.
     *
     * @return La configuración actual o una instancia vacía de [AppConfig].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
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