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
        val path = getEmulatorPathUseCase("duckstation") ?: return emptyList()
        val directory = File(path)

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        val supportedExtensions = listOf("bin", "cue", "iso", "img", "chd")

        val candidates = directory.walkTopDown()
            .filter { it.isFile && it.extension.lowercase() in supportedExtensions }.toList()
        val referenced = candidates.filter { it.extension.equals("cue", true) }.flatMap { cue ->
            val contents = try { cue.readText() } catch (_: java.io.IOException) { "" }
            CUE_FILE.findAll(contents).map { match ->
                val name = match.groups[1]?.value ?: match.groups[2]!!.value
                File(cue.parentFile, name.replace('\\', '/')).toPath().normalize().toAbsolutePath()
            }.toList()
        }.toSet()
        val games = candidates.filterNot {
            it.extension.lowercase() in listOf("bin", "img") && it.toPath().normalize().toAbsolutePath() in referenced
        }
        val covers = org.example.globalconsole.generalData.GameCoverFiles(games)
        return games
            .map { file ->
                GameDuckStation(
                    id = "duckstation_${file.absolutePath.hashCode()}",
                    name = file.nameWithoutExtension,
                    urlGameExecute = file.absolutePath,
                    image = covers.find(file),
                    platform = Platforms.DUCKSTATION
                )
            }
            .toList()
    }
    private companion object {
        val CUE_FILE = Regex("""^\s*FILE\s+(?:"([^"]+)"|(\S+))\s+\S+""", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
    }
}
