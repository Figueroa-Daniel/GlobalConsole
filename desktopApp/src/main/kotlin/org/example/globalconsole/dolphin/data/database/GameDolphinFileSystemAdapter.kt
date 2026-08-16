package org.example.globalconsole.dolphin.data.database

import org.example.globalconsole.dolphin.domain.entitys.GameDolphin
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import java.io.File
import java.util.UUID

/**
 * Adaptador para interactuar con el sistema de archivos local y obtener
 * la lista de ROMs/ISOs de Dolphin (Wii/GameCube).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-16
 */
class GameDolphinFileSystemAdapter(
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase
) {
    /**
     * Obtiene la lista de juegos de Dolphin desde la ruta configurada, filtrando
     * por las extensiones de Wii/GameCube soportadas.
     *
     * @return Lista de entidades [GameDolphin].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-16
     */
    suspend fun getGamesFromDirectory(): List<GameDolphin> {
        val path = getEmulatorPathUseCase("dolphinGames") ?: return emptyList()
        val directory = File(path)

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        val supportedExtensions = listOf("iso", "rvz", "wbfs", "gcm", "ciso")

        return directory.walkTopDown()
            .filter { file ->
                file.isFile && supportedExtensions.any { ext -> file.name.lowercase().endsWith(".$ext") }
            }
            .map { file ->
                GameDolphin(
                    id = "dolphin_${file.absolutePath.hashCode()}",
                    name = file.nameWithoutExtension,
                    urlGameExecute = file.absolutePath,
                    image = null,
                    platform = Platforms.DOLPHIN
                )
            }
            .toList()
    }
}
