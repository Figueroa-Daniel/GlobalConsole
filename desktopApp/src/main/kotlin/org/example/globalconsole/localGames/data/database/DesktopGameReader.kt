package org.example.globalconsole.localGames.data.database

import org.example.globalconsole.localGames.data.dto.LocalGameDto
import java.io.File
import java.util.Locale

/**
 * Lee accesos Linux sin ejecutar el contenido durante el escaneo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal object DesktopGameReader {
    private val excluded = setOf("pcsx2", "dolphin", "dolphin-emu", "duckstation", "melonds", "rpcs3", "heroic", "azahar", "retroarch", "steam", "lutris", "globalconsole")

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
        val tokens = (listOf(name) + arguments).flatMap { it.lowercase().split(Regex("[^a-z0-9-]+")) }
        val steamGame = arguments.any { it.startsWith("steam://rungameid/", true) || it == "-applaunch" }
        return tokens.any { it in excluded && !(it == "steam" && steamGame) }
    }

    /**
     * Extrae exclusivamente Desktop Entry y valida la categoría Game.
     * @param file Archivo desktop de origen.
     * @param desktopId Identificador con prioridad XDG ya resuelta.
     * @return Metadatos o null cuando el acceso no es un juego visible y ejecutable.
     * @throws java.io.IOException Si no puede leerse el archivo.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    fun read(file: File, desktopId: String): LocalGameDto? {
        val values = linkedMapOf<String, String>()
        var main = false
        file.forEachLine { raw ->
            val line = raw.trim()
            if (line.startsWith("[")) main = line == "[Desktop Entry]"
            else if (main && !line.startsWith("#") && '=' in line) {
                values.putIfAbsent(line.substringBefore('='), line.substringAfter('='))
            }
        }
        if (values["Type"] != "Application" || values["Hidden"].equals("true", true) ||
            values["NoDisplay"].equals("true", true) || values["Terminal"].equals("true", true) ||
            "Game" !in values["Categories"].orEmpty().split(';')) return null
        val locale = Locale.getDefault()
        val name = values["Name[${locale.language}_${locale.country}]"] ?: values["Name[${locale.language}]"] ?: values["Name"] ?: return null
        val icon = values["Icon"]
        val exec = values["Exec"] ?: values["X-Flatpak"]?.let { "flatpak run $it" } ?: return null
        val tokens = tokenize(exec) ?: return null
        val args = mutableListOf<String>()
        for (token in tokens) {
            when (token) {
                "%f", "%F", "%u", "%U", "%d", "%D", "%n", "%N", "%v", "%m" -> Unit
                "%i" -> if (!icon.isNullOrBlank()) args.addAll(listOf("--icon", icon))
                else -> {
                    val expanded = StringBuilder()
                    var i = 0
                    while (i < token.length) {
                        if (token[i] != '%') expanded.append(token[i])
                        else {
                            if (++i >= token.length) return null
                            expanded.append(when (token[i]) {
                                '%' -> "%"
                                'c' -> name
                                'k' -> file.absolutePath
                                else -> return null
                            })
                        }
                        i++
                    }
                    args.add(expanded.toString())
                }
            }
        }
        if (args.isEmpty() || args.first().isBlank() || isExcluded(name, args)) return null
        val tryExec = values["TryExec"]
        if (tryExec != null && !executableExists(tryExec)) return null
        return LocalGameDto("local:linux:$desktopId", name, args, resolveIcon(icon), values["Path"]?.takeIf { File(it).isDirectory })
    }

    /**
     * Separa los argumentos Exec respetando comillas dobles y escapes del formato.
     * @param command Valor Exec sin expandir marcadores.
     * @return Argumentos o null si contiene comillas o escapes incompletos.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun tokenize(command: String): List<String>? {
        val decoded = command.replace("\\s", " ").replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r").replace("\\\\", "\\")
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var quoted = false
        var escaped = false
        var started = false
        for (char in decoded) {
            when {
                escaped -> { current.append(char); escaped = false; started = true }
                char == '\\' -> escaped = true
                char == '"' -> { quoted = !quoted; started = true }
                char.isWhitespace() && !quoted -> if (started) {
                    result.add(current.toString()); current.clear(); started = false
                }
                else -> { current.append(char); started = true }
            }
        }
        if (quoted || escaped) return null
        if (started) result.add(current.toString())
        return result
    }

    /**
     * Verifica TryExec en una ruta absoluta o en PATH.
     * @param command Ejecutable requerido.
     * @return True cuando existe y tiene permiso de ejecución.
     * @throws Nothing No lanza excepciones directamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun executableExists(command: String): Boolean =
        if ('/' in command) File(command).canExecute()
        else System.getenv("PATH").orEmpty().split(File.pathSeparator).any { File(it, command).canExecute() }

    /**
     * Resuelve iconos comunes de temas y Flatpak; deja el fallback visual a Compose.
     * @param icon Ruta absoluta o nombre de icono.
     * @return Archivo local o null si no está disponible.
     * @throws Nothing No lanza excepciones directamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun resolveIcon(icon: String?): String? {
        if (icon.isNullOrBlank()) return null
        if (File(icon).isAbsolute) return icon.takeIf { File(it).isFile && File(it).extension.lowercase() in setOf("png", "jpg", "jpeg", "webp") }
        val home = System.getProperty("user.home")
        val roots = listOf("$home/.local/share/icons", "/usr/share/icons", "$home/.local/share/flatpak/exports/share/icons", "/var/lib/flatpak/exports/share/icons")
        for (root in roots) for (size in listOf("256x256", "128x128", "64x64", "48x48", "scalable")) {
            for (ext in listOf("png", "jpg", "jpeg")) {
                val file = File(root, "hicolor/$size/apps/${if (icon.endsWith(".$ext")) icon else "$icon.$ext"}")
                if (file.isFile) return file.absolutePath
            }
        }
        return listOf(File("/usr/share/pixmaps", icon), File("/usr/share/pixmaps", "$icon.png")).firstOrNull { it.isFile }?.absolutePath
    }
}
