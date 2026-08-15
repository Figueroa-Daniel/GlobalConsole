package org.example.globalconsole.melonDS.data.database

import org.example.globalconsole.melonDS.data.dto.GameDsDto
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import java.io.File

/**
 * Adaptador de acceso al sistema de archivos encargado de escanear directorios y gestionar
 * los archivos físicos de juegos (.nds) para el emulador Melon DS.
 *
 * @param getEmulatorPathUseCase UseCase para obtener la ruta configurada del emulador.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class GameDSFileSystemAdapter(
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase
) {

    /**
     * Realiza un escaneo recursivo en el directorio configurado para Melon DS
     * y retorna una lista de todos los archivos con extensión `.nds`.
     *
     * @return Lista de objetos [GameDsDto] con la información de los archivos de juego encontrados.
     */
    suspend fun getGamesInSystemFile(): List<GameDsDto> {
        val routeMelonDSGames = getEmulatorPathUseCase("melonds") ?: return emptyList()
        val dsFolder = File(routeMelonDSGames)
        val listGames = mutableListOf<GameDsDto>()
        var id: Int = 0

        if (dsFolder.exists() && dsFolder.isDirectory) {
            dsFolder.walkTopDown().forEach { file ->
                if (file.isFile && file.extension.equals("nds", ignoreCase = true)) {
                    id++
                    listGames.add(
                        GameDsDto(
                            id = "melonds$id",
                            name = file.nameWithoutExtension,
                            urlGameExecute = file.absolutePath
                        )
                    )
                }
            }
        }
        return listGames
    }

    /**
     * Elimina el archivo de juego correspondiente en el sistema de archivos.
     */
    suspend fun deleteGameInFile(id: String): Boolean {
        val routeMelonDSGames = getEmulatorPathUseCase("melonds") ?: return false
        val dsFolder = File(routeMelonDSGames)
        var fileToDelete: File? = null
        var currentId = 0

        if (dsFolder.exists() && dsFolder.isDirectory) {
            dsFolder.walkTopDown().forEach { file ->
                if (file.isFile && file.extension.equals("nds", ignoreCase = true)) {
                    currentId++
                    if ("melonds$currentId" == id) {
                        fileToDelete = file
                        return@forEach
                    }
                }
            }
        }

        return fileToDelete?.let { file ->
            try {
                if (file.exists()) {
                    file.delete()
                } else false
            } catch (e: Exception) {
                false
            }
        } ?: false
    }
}