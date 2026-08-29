package org.example.globalconsole.azahar.data.database

import org.example.globalconsole.azahar.data.dto.Game3DSDto
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import java.io.File

/**
 * Adaptador de acceso al sistema de archivos encargado de escanear directorios y gestionar
 * los archivos físicos de juegos (.3ds) para el emulador Azahar.
 *
 * @param getEmulatorPathUseCase UseCase para obtener la ruta configurada del emulador.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
class Game3DSFileSystemAdapter(
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase
) {

    /**
     * Realiza un escaneo recursivo en el directorio configurado para Azahar
     * y retorna una lista de todos los archivos con extensión `.3ds`.
     *
     * @return Lista de objetos [Game3DSDto] con la información de los archivos de juego encontrados.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend fun getGamesInSystemFile(): List<Game3DSDto> {
        val routeAzaharGames = getEmulatorPathUseCase("azahar") ?: return emptyList()
        val dsFolder = File(routeAzaharGames)
        val listGames = mutableListOf<Game3DSDto>()
        var id: Int = 0

        if (dsFolder.exists() && dsFolder.isDirectory) {
            dsFolder.walkTopDown().forEach { file ->
                if (file.isFile && file.extension.equals("3ds", ignoreCase = true)) {
                    id++
                    val imagePath = file.parentFile?.listFiles()?.firstOrNull { 
                        it.isFile && it.extension.lowercase() in listOf("png", "jpg", "jpeg") 
                    }?.absolutePath
                    listGames.add(
                        Game3DSDto(
                            id = "azahar$id",
                            name = file.nameWithoutExtension,
                            urlGameExecute = file.absolutePath,
                            image = imagePath
                        )
                    )
                }
            }
        }
        return listGames
    }

    /**
     * Elimina el archivo de juego correspondiente en el sistema de archivos.
     *
     * @param id Identificador del juego a eliminar.
     * @return True si se eliminó correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-26
     */
    suspend fun deleteGameInFile(id: String): Boolean {
        val routeAzaharGames = getEmulatorPathUseCase("azahar") ?: return false
        val dsFolder = File(routeAzaharGames)
        var fileToDelete: File? = null
        var currentId = 0

        if (dsFolder.exists() && dsFolder.isDirectory) {
            dsFolder.walkTopDown().forEach { file ->
                if (file.isFile && file.extension.equals("3ds", ignoreCase = true)) {
                    currentId++
                    if ("azahar$currentId" == id) {
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
