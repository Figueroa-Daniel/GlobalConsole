package org.example.globalconsole.melonDS.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.melonDS.data.database.GameMelonDSAdapter
import org.example.globalconsole.melonDS.data.database.LauncherMelonDSAdapter
import org.example.globalconsole.melonDS.data.dto.MelonDSLauncherDto
import org.example.globalconsole.melonDS.data.repository.MelonDSRepository
import java.io.File

/**
 * Implementación del repositorio de Melon DS que delega las operaciones a los adaptadores de la capa de datos.
 *
 * @property launcherAdapter Adaptador para gestionar el launcher de Melon DS.
 * @property gameAdapter Adaptador para gestionar la ejecución de juegos de Melon DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class MelonDSRepositoryImpl(
    private val launcherAdapter: LauncherMelonDSAdapter = LauncherMelonDSAdapter(),
    private val gameAdapter: GameMelonDSAdapter = GameMelonDSAdapter()
): MelonDSRepository {

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

    override suspend fun isMelonDSEnabled(): Boolean = withContext(Dispatchers.IO) {
        readConfig().melonDSEnabled
    }

    override suspend fun saveMelonDSEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = readConfig()
        val updated = current.copy(melonDSEnabled = enabled)
        configFile.writeText(json.encodeToString(updated))
    }

    override suspend fun showMelonDSLauncher(): MelonDSLauncherDto = withContext(Dispatchers.IO) {
        MelonDSLauncherDto(
            id = "melonds-launcher",
            name = "Melon DS Launcher",
            urlGameExecute = "melonds" // Generic internal URL
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