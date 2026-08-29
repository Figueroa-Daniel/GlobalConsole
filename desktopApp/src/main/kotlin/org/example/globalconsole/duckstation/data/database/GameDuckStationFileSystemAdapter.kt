package org.example.globalconsole.duckstation.data.database

import org.example.globalconsole.duckstation.domain.entitys.GameDuckStation
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import java.io.File

/**
 * Adaptador para interactuar con el sistema de archivos local y obtener
 * la lista de ROMs/ISOs de DuckStation (PlayStation 1).
 * El escaneo se realiza de forma recursiva sobre el directorio configurado.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-29
 */
class GameDuckStationFileSystemAdapter(
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase
) {
    /**
     * Obtiene la lista de juegos de DuckStation desde la ruta configurada, filtrando
     * recursivamente por las extensiones de PS1 soportadas.
     *
     * @return Lista de entidades [GameDuckStation].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-29
     */
    suspend fun getGamesFromDirectory(): List<GameDuckStation> {
        val path = getEmulatorPathUseCase("duckstationGames") ?: return emptyList()
        val directory = File(path)

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        val supportedExtensions = listOf("bin", "cue", "iso", "img", "chd")

        return directory.walkTopDown()
            .filter { file ->
                file.isFile && supportedExtensions.any { ext -> file.name.lowercase().endsWith(".$ext") }
            }
            .map { file ->
                GameDuckStation(
                    id = "duckstation_${file.absolutePath.hashCode()}",
                    name = file.nameWithoutExtension,
                    urlGameExecute = file.absolutePath,
                    image = null,
                    platform = Platforms.DUCKSTATION
                )
            }
            .toList()
    }
}
