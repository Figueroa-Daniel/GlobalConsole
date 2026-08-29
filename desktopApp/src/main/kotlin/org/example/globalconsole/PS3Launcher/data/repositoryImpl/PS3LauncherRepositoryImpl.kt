package org.example.globalconsole.PS3Launcher.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.PS3Launcher.data.database.LauncherPS3Adapter
import org.example.globalconsole.PS3Launcher.data.dto.PS3LauncherDto
import org.example.globalconsole.PS3Launcher.data.repository.PS3LauncherRepository
import org.example.globalconsole.config.AppConfig
import java.io.File

/**
 * Implementación de [PS3LauncherRepository] que gestiona los datos del launcher,
 * la preferencia de visibilidad del usuario y la ejecución nativa del proceso.
 *
 * Utiliza el mismo patrón de escritura segura que HeroicGames en [AppConfig].
 *
 * @property adapter Adaptador responsable de la ejecución nativa de RPCS3.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
class PS3LauncherRepositoryImpl(
    private val adapter: LauncherPS3Adapter
) : PS3LauncherRepository {

    private val configFile = File("config.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override suspend fun hidePS3Launcher(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            savePS3Enabled(false)
            true
        } catch (e: Exception) {
            System.err.println("Error al ocultar PS3 Launcher: ${e.message}")
            false
        }
    }

    override suspend fun showPS3Launcher(): PS3LauncherDto = withContext(Dispatchers.IO) {
        PS3LauncherDto(
            id = "ps3-launcher",
            name = "RPCS3",
            urlGameExecute = "net.rpcs3.RPCS3"
        )
    }

    override suspend fun executePS3Launcher(): Boolean = withContext(Dispatchers.IO) {
        adapter.executeLauncher()
    }

    override suspend fun closeLauncher(): Boolean = withContext(Dispatchers.IO) {
        adapter.closeProcess()
    }

    override suspend fun isPS3Enabled(): Boolean = withContext(Dispatchers.IO) {
        readConfig().ps3Enabled
    }

    override suspend fun savePS3Enabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = readConfig()
        val updated = current.copy(ps3Enabled = enabled)
        configFile.writeText(json.encodeToString(updated))
    }

    private fun readConfig(): AppConfig {
        if (!configFile.exists()) return AppConfig()
        return try {
            json.decodeFromString<AppConfig>(configFile.readText())
        } catch (e: Exception) {
            AppConfig()
        }
    }
}
