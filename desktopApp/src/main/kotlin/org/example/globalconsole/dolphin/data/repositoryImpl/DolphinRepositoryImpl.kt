package org.example.globalconsole.dolphin.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.dolphin.data.database.LauncherDolphinAdapter
import org.example.globalconsole.dolphin.data.database.GameDolphinAdapter
import org.example.globalconsole.dolphin.data.dto.DolphinLauncherDto
import org.example.globalconsole.dolphin.data.repository.DolphinRepository
import org.example.globalconsole.generalDomain.entititys.Platforms
import java.io.File

/**
 * Implementación del repositorio de Dolphin que orquesta los adaptadores nativos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class DolphinRepositoryImpl(
    private val launcherAdapter: LauncherDolphinAdapter,
    private val gameAdapter: GameDolphinAdapter
) : DolphinRepository {

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

    override suspend fun isDolphinEnabled(): Boolean = withContext(Dispatchers.IO) {
        readConfig().dolphinEnabled
    }

    override suspend fun saveDolphinEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = readConfig()
        val updated = current.copy(dolphinEnabled = enabled)
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

    override suspend fun showDolphinLauncher(): DolphinLauncherDto {
        return DolphinLauncherDto(
            id = "dolphin-launcher-id",
            name = "Dolphin Emulator",
            urlGameExecute = "launcher_dolphin",
            image = null,
            platform = Platforms.DOLPHIN
        )
    }
}
