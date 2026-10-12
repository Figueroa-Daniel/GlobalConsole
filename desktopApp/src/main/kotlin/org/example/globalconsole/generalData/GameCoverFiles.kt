package org.example.globalconsole.generalData

import org.example.globalconsole.generalDomain.usecase.SelectGameCoverUseCase
import java.io.File

/**
 * Resuelve carátulas con un listado de imágenes por carpeta durante cada escaneo.
 * @param games Archivos de juegos lógicos, sin pistas duplicadas.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class GameCoverFiles(games: List<File>) {
    private val counts = games.groupingBy { it.parentFile }.eachCount()
    private val images = mutableMapOf<File, List<String>>()
    private val select = SelectGameCoverUseCase()

    /**
     * Devuelve una carátula asociada al archivo sin compartir imágenes ambiguas.
     * @param game Archivo del juego.
     * @return Ruta de la imagen o null.
     * @throws SecurityException Si no se permite leer el directorio.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    fun find(game: File): String? {
        val parent = game.parentFile ?: return null
        val names = images.getOrPut(parent) {
            parent.listFiles().orEmpty().filter { it.isFile && it.extension.lowercase() in listOf("png", "jpg", "jpeg") }.map { it.name }
        }
        return select(game.nameWithoutExtension, names, counts[parent] ?: 0)?.let { File(parent, it).absolutePath }
    }
}
