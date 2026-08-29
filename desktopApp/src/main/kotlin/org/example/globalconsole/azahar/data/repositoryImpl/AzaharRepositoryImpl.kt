package org.example.globalconsole.azahar.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.azahar.data.database.Game3DSAzaharAdapter
import org.example.globalconsole.azahar.data.database.LauncherAzaharAdapter
import org.example.globalconsole.azahar.data.dto.AzaharLauncherDto
import org.example.globalconsole.azahar.data.repository.AzaharRepository
import java.io.File

/**
 * Implementación del repositorio de Azahar que delega las operaciones a los adaptadores de la capa de datos.
 *
 * @property launcherAdapter Adaptador para gestionar el launcher de Azahar.
 * @property gameAdapter Adaptador para gestionar la ejecución de juegos de Azahar.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class AzaharRepositoryImpl(
    private val launcherAdapter: LauncherAzaharAdapter = LauncherAzaharAdapter(),
    private val gameAdapter: Game3DSAzaharAdapter = Game3DSAzaharAdapter()
): AzaharRepository {

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

    override suspend fun isAzaharEnabled(): Boolean = withContext(Dispatchers.IO) {
        readConfig().azaharEnabled
    }

    override suspend fun saveAzaharEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = readConfig()
        val updated = current.copy(azaharEnabled = enabled)
        configFile.writeText(json.encodeToString(updated))
    }

    override suspend fun showAzaharLauncher(): AzaharLauncherDto = withContext(Dispatchers.IO) {
        AzaharLauncherDto(
            id = "azahar-launcher",
            name = "Azahar Launcher",
            urlGameExecute = "azahar"
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
