package org.example.globalconsole.HeroicGames.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.HeroicGames.data.database.LauncherHeroicGamesAdapter
import org.example.globalconsole.HeroicGames.data.dto.HGLauncherDto
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository
import java.io.File

/**
 * Implementación de [HGLauncherRepository] que gestiona los datos del launcher,
 * la preferencia de visibilidad del usuario y la ejecución nativa del proceso.
 *
 * La persistencia de la preferencia `heroic_enabled` se almacena en `config.json`
 * bajo la clave reservada `heroic_enabled`, mantenida en este repositorio para
 * respetar la separación de módulos de la arquitectura Clean.
 *
 * @property adapter Adaptador responsable de la ejecución nativa de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class HGLauncherRepositoryImpl(
    private val adapter: LauncherHeroicGamesAdapter
) : HGLauncherRepository {

    /**
     * Modelo de datos serializable que representa la sección de configuración
     * de Heroic Games Launcher en `config.json`.
     *
     * @param heroicEnabled True si el launcher debe mostrarse en la biblioteca.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    @Serializable
    private data class HeroicConfig(
        val heroicEnabled: Boolean = false
    )

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
     * Persiste la preferencia de visibilidad de Heroic Games Launcher en `config.json`.
     * Si el archivo no existe lo crea. Si existe, actualiza únicamente la clave `heroicEnabled`.
     *
     * @param enabled True para mostrar el launcher en la biblioteca, false para ocultarlo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun saveHeroicEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val updated = HeroicConfig(heroicEnabled = enabled)
        configFile.writeText(json.encodeToString(updated))
    }

    /**
     * Lee y deserializa la sección de configuración de Heroic desde `config.json`.
     * Si el archivo no existe o no puede parsearse, retorna una instancia por defecto.
     *
     * @return La configuración actual o una instancia vacía de [HeroicConfig].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    private fun readConfig(): HeroicConfig {
        if (!configFile.exists()) return HeroicConfig()
        return try {
            json.decodeFromString<HeroicConfig>(configFile.readText())
        } catch (e: Exception) {
            HeroicConfig()
        }
    }
}