package org.example.globalconsole.systemApps.data

import java.io.File
import java.util.Locale

/**
 * Lee accesos Linux compartidos por juegos y aplicaciones sin ejecutarlos.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal object DesktopApplicationReader {
    /**
     * Extrae exclusivamente Desktop Entry y valida sus propiedades de visibilidad.
     * @param file Archivo desktop de origen.
     * @param desktopId Identificador con prioridad XDG ya resuelta.
     * @return Metadatos o null cuando el acceso no es visible y ejecutable.
     * @throws java.io.IOException Si no puede leerse el archivo.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    fun read(file: File, desktopId: String): DesktopApplicationEntry? {
        val values = linkedMapOf<String, String>()
        var main = false
        file.forEachLine { raw ->
            val line = raw.trim()
            if (line.startsWith("[")) main = line == "[Desktop Entry]"
            else if (main && !line.startsWith("#") && '=' in line) {
                values.putIfAbsent(line.substringBefore('='), line.substringAfter('='))
            }
        }
        if (values["Type"] != "Application" ||
                        values["Hidden"].equals("true", true) ||
                        values["NoDisplay"].equals("true", true) ||
                        values["Terminal"].equals("true", true)
        )
                return null
        val locale = Locale.getDefault()
        val name =
                values["Name[${locale.language}_${locale.country}]"]
                        ?: values["Name[${locale.language}]"] ?: values["Name"] ?: return null
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
                            expanded.append(
                                    when (token[i]) {
                                        '%' -> "%"
                                        'c' -> name
                                        'k' -> file.absolutePath
                                        else -> return null
                                    }
                            )
                        }
                        i++
                    }
                    args.add(expanded.toString())
                }
            }
        }
        if (args.isEmpty() || args.first().isBlank()) return null
        val tryExec = values["TryExec"]
        if (tryExec != null && !executableExists(tryExec)) return null
        return DesktopApplicationEntry(
                desktopId,
                name,
                args,
                resolveIcon(icon),
                values["Path"]?.takeIf { File(it).isDirectory },
                values["Categories"].orEmpty().split(';').filter { it.isNotBlank() }.toSet(),
                values["X-Flatpak"] ?: values["X-SnapInstanceName"] ?: desktopId
        )
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
        val decoded =
                command.replace("\\s", " ")
                        .replace("\\n", "\n")
                        .replace("\\t", "\t")
                        .replace("\\r", "\r")
                        .replace("\\\\", "\\")
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var quoted = false
        var escaped = false
        var started = false
        for (char in decoded) {
            when {
                escaped -> {
                    current.append(char)
                    escaped = false
                    started = true
                }
                char == '\\' -> escaped = true
                char == '"' -> {
                    quoted = !quoted
                    started = true
                }
                char.isWhitespace() && !quoted ->
                        if (started) {
                            result.add(current.toString())
                            current.clear()
                            started = false
                        }
                else -> {
                    current.append(char)
                    started = true
                }
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
            else
                    System.getenv("PATH").orEmpty().split(File.pathSeparator).any {
                        File(it, command).canExecute()
                    }

    /**
     * Resuelve iconos comunes de temas y Flatpak; deja el fallback visual a Compose.
     * @param icon Ruta absoluta o nombre de icono.
     * @return Archivo local o null si no está disponible.
     * @throws Nothing No lanza excepciones directamente.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    internal fun resolveIcon(icon: String?): String? {
        if (icon.isNullOrBlank()) return null
        val supported = setOf("png", "jpg", "jpeg", "webp")
        if (File(icon).isAbsolute) return icon.takeIf { File(it).isFile && File(it).extension.lowercase() in supported }
        val home = System.getProperty("user.home")
        val knownImageExtensions = setOf("png", "svg", "xpm", "ico", "jpg", "jpeg", "webp")
        val cleanIcon = if (icon.substringAfterLast('.').lowercase() in knownImageExtensions) icon.substringBeforeLast('.') else icon

        // 1. Comprobación directa en app-info de Flatpak (donde Flatpak guarda PNGs de 128x128 y 64x64)
        val flatpakAppDirs = listOf("/var/lib/flatpak/app", "$home/.local/share/flatpak/app")
        val appInfoSizes = listOf("128x128", "64x64", "48x48")
        for (base in flatpakAppDirs) {
            for (size in appInfoSizes) {
                val candidate = File("$base/$cleanIcon/current/active/files/share/app-info/icons/flatpak/$size/$cleanIcon.png")
                if (candidate.isFile) return candidate.absolutePath
            }
        }

        // 2. Comprobación en temas de iconos (incluyendo tamaños 512, 256, 128, 64, 48 y 0x0 de AppImage)
        val roots = listOf(
            "$home/.local/share/icons",
            "/usr/share/icons",
            "$home/.local/share/flatpak/exports/share/icons",
            "/var/lib/flatpak/exports/share/icons"
        )
        val sizes = listOf("512x512", "256x256", "128x128", "64x64", "48x48", "32x32", "0x0")
        val extensions = listOf("png", "jpg", "jpeg", "webp")
        for (root in roots) {
            for (size in sizes) {
                for (ext in extensions) {
                    val file = File(root, "hicolor/$size/apps/$cleanIcon.$ext")
                    if (file.isFile) return file.absolutePath
                }
            }
        }

        // 3. Comprobación en pixmaps del sistema
        for (ext in extensions) {
            val pixmap = File("/usr/share/pixmaps", "$cleanIcon.$ext")
            if (pixmap.isFile) return pixmap.absolutePath
        }
        val exactPixmap = File("/usr/share/pixmaps", icon)
        if (exactPixmap.isFile && exactPixmap.extension.lowercase() in supported) return exactPixmap.absolutePath

        // 4. Búsqueda profunda en ~/.local/share/icons para iconos de temas instalados
        val userIcons = File("$home/.local/share/icons")
        if (userIcons.isDirectory) {
            val match = userIcons.walkTopDown().maxDepth(5).firstOrNull { f ->
                f.isFile && f.nameWithoutExtension == cleanIcon && f.extension.lowercase() in supported
            }
            if (match != null) return match.absolutePath
        }

        return null
    }
}
