package org.example.globalconsole.juegosPcsx2.data.database

import org.example.globalconsole.juegosPcsx2.data.dto.GameP2Dto
import org.example.globalconsole.settings.ROUTE_PCSX2_GAMES
import java.io.File

/**
 * Adaptador de acceso al sistema de archivos encargado de escanear directorios y gestionar
 * los archivos físicos de juegos (ISOs) para el emulador PCSX2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class GameP2FileSystemAdapter {

    /**
     * Realiza un escaneo recursivo en el directorio configurado en [ROUTE_PCSX2_GAMES]
     * y retorna una lista de todos los archivos con extensión `.iso`.
     *
     * @return Lista de objetos [GameP2Dto] con la información de los archivos de juego encontrados.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun getGamesInSystemFile(): List<GameP2Dto> {
        val pcsxFolder = File(ROUTE_PCSX2_GAMES)
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
     * Elimina el archivo de juego correspondiente en el sistema de archivos (pendiente de implementar).
     *
     * @param id Identificador único del juego a eliminar.
     * @return True si el archivo se eliminó con éxito, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    fun deleteGameInFile(id: String): Boolean {
        return false
    }
}