package org.example.globalconsole.localGames.data.database

import java.io.File
import org.example.globalconsole.systemApps.data.DesktopApplicationReader
import org.example.globalconsole.localGames.data.dto.LocalGameDto

/**
 * Lee accesos Linux sin ejecutar el contenido durante el escaneo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal object DesktopGameReader {
    private val excluded =
            setOf(
                    "pcsx2",
                    "dolphin",
                    "dolphin-emu",
                    "duckstation",
                    "melonds",
                    "rpcs3",
                    "heroic",
                    "azahar",
                    "retroarch",
                    "steam",
                    "lutris",
                    "globalconsole"
            )

    /**
     * Identifica emuladores y lanzadores ya gestionados o agregadores de juegos.
     * @param name Nombre del acceso.
     * @param arguments Comando de ejecución.
     * @return True si debe omitirse para evitar duplicados.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    fun isExcluded(name: String, arguments: List<String>): Boolean {
        val tokens =
                (listOf(name) + arguments).flatMap { it.lowercase().split(Regex("[^a-z0-9-]+")) }
        val steamGame =
                arguments.any { it.startsWith("steam://rungameid/", true) || it == "-applaunch" }
        return tokens.any { it in excluded && !(it == "steam" && steamGame) }
    }

    /**
     * Conserva únicamente juegos tras resolver el acceso con el lector compartido.
     * @param file Archivo desktop de origen.
     * @param desktopId Identificador XDG con prioridad resuelta.
     * @return Juego visible o null si no pertenece al catálogo.
     * @throws java.io.IOException Si el archivo no se puede leer.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    fun read(file: File, desktopId: String): LocalGameDto? {
        val entry = DesktopApplicationReader.read(file, desktopId) ?: return null
        if ("Game" !in entry.categories || isExcluded(entry.name, entry.arguments)) return null
        return LocalGameDto("local:linux:$desktopId", entry.name, entry.arguments, entry.iconPath, entry.workingDirectory)
    }

    /**
     * Resuelve iconos usando la implementación compartida de aplicaciones nativas.
     * @param icon Ruta o identificador del icono.
     * @return Imagen local compatible o null.
     * @throws Nothing No lanza excepciones directamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    internal fun resolveIcon(icon: String?): String? = DesktopApplicationReader.resolveIcon(icon)
}
