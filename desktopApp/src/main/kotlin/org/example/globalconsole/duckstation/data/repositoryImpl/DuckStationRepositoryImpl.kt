package org.example.globalconsole.duckstation.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.duckstation.data.database.GameDuckStationAdapter
import org.example.globalconsole.duckstation.data.database.LauncherDuckStationAdapter
import org.example.globalconsole.duckstation.data.dto.DuckStationLauncherDto
import org.example.globalconsole.duckstation.data.repository.DuckStationRepository
import org.example.globalconsole.generalDomain.entititys.Platforms
import java.io.File

/**
 * Implementación del repositorio de DuckStation que orquesta los adaptadores nativos
 * y persiste el estado de habilitado/deshabilitado en el archivo config.json.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class DuckStationRepositoryImpl(
    private val launcherAdapter: LauncherDuckStationAdapter,
    private val gameAdapter: GameDuckStationAdapter
) : DuckStationRepository {

    private val configFile = File("config.json")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override suspend fun executeLauncher(): Boolean = withContext(Dispatchers.IO) {
        launcherAdapter.executeLauncher()
    }

    override suspend fun closeLauncher(): Boolean = withContext(Dispatchers.IO) {
        launcherAdapter.closeLauncher()
    }

    override suspend fun executeGame(executeUrl: String?): Boolean = withContext(Dispatchers.IO) {
        gameAdapter.executeGame(executeUrl)
    }

    override suspend fun isDuckStationEnabled(): Boolean = withContext(Dispatchers.IO) {
        readConfig().duckStationEnabled
    }

    override suspend fun saveDuckStationEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = readConfig()
        val updated = current.copy(duckStationEnabled = enabled)
        configFile.writeText(json.encodeToString(updated))
    }

    override suspend fun showDuckStationLauncher(): DuckStationLauncherDto {
        return DuckStationLauncherDto(
            id = "duckstation-launcher-id",
            name = "DuckStation",
            urlGameExecute = "launcher_duckstation",
            image = null,
            platform = Platforms.DUCKSTATION
        )
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
