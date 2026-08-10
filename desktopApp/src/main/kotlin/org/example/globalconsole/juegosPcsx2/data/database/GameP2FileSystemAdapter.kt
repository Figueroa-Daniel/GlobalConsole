package org.example.globalconsole.juegosPcsx2.data.database

import org.example.globalconsole.juegosPcsx2.data.dto.GameP2Dto
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import java.io.File

/**
 * Adaptador de acceso al sistema de archivos encargado de escanear directorios y gestionar
 * los archivos físicos de juegos (ISOs) para el emulador PCSX2.
 * La ruta del directorio se obtiene dinámicamente desde [GetEmulatorPathUseCase],
 * eliminando la dependencia de variables globales y haciendo el adaptador escalable.
 *
 * @param getEmulatorPathUseCase UseCase para obtener la ruta configurada del emulador.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class GameP2FileSystemAdapter(
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase
) {

    /**
     * Realiza un escaneo recursivo en el directorio configurado para PCSX2
     * y retorna una lista de todos los archivos con extensión `.iso`.
     *
     * @return Lista de objetos [GameP2Dto] con la información de los archivos de juego encontrados.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun getGamesInSystemFile(): List<GameP2Dto> {
        val routePcsx2Games = getEmulatorPathUseCase("pcsx2") ?: return emptyList()
        val pcsxFolder = File(routePcsx2Games)
        val listGames = mutableListOf<GameP2Dto>()
        var id: Int = 0

        if (pcsxFolder.exists() && pcsxFolder.isDirectory) {
            pcsxFolder.walkTopDown().forEach { file ->
                if (file.isFile && file.extension.equals("iso", ignoreCase = true)) {
                    id++
                    listGames.add(GameP2Dto(id = "pcsx2$id", name = file.nameWithoutExtension, urlGameExecute = file.absolutePath))
                }
            }
        }
        return listGames
    }

    /**
     * Elimina el archivo de juego correspondiente en el sistema de archivos (soporta Windows y Linux).
     *
     * @param id Identificador único del juego a eliminar.
     * @return True si el archivo se eliminó con éxito, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun deleteGameInFile(id: String): Boolean {
        val routePcsx2Games = getEmulatorPathUseCase("pcsx2")
        if (routePcsx2Games.isNullOrBlank()) {
            println("Games directory route is null or blank.")
            return false
        }

        val os = System.getProperty("os.name").lowercase()

        return if (os.contains("linux") || os.contains("windows")) {
            deleteGameForSupportedOs(id, routePcsx2Games)
        } else {
            println("Unsupported operating system for file deletion: $os")
            false
        }
    }

    /**
     * Auxiliar para buscar y eliminar el juego en sistemas operativos soportados.
     *
     * @param id Identificador único del juego.
     * @param path Ruta del directorio de ISOs.
     * @return True si el archivo fue encontrado y eliminado con éxito.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    private fun deleteGameForSupportedOs(id: String, path: String): Boolean {
        val pcsxFolder = File(path)
        var currentId = 0
        var fileToDelete: File? = null

        if (pcsxFolder.exists() && pcsxFolder.isDirectory) {
            pcsxFolder.walkTopDown().forEach { file ->
                if (file.isFile && file.extension.equals("iso", ignoreCase = true)) {
                    currentId++
                    if ("pcsx2$currentId" == id) {
                        fileToDelete = file
                        return@forEach
                    }
                }
            }
        }

        return fileToDelete?.let { file ->
            try {
                if (file.exists()) {
                    val deleted = file.delete()
                    if (deleted) {
                        println("Deleted game file successfully: ${file.absolutePath}")
                    } else {
                        println("Failed to delete game file: ${file.absolutePath}")
                    }
                    deleted
                } else {
                    println("Game file does not exist: ${file.absolutePath}")
                    false
                }
            } catch (e: Exception) {
                System.err.println("Error deleting game file: ${e.message}")
                e.printStackTrace()
                false
            }
        } ?: false
    }
}