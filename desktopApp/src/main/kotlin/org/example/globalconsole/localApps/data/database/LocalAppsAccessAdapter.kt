package org.example.globalconsole.localApps.data.database

import com.sun.jna.platform.win32.Shell32Util
import org.example.globalconsole.systemApps.data.NativeApplicationProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import org.example.globalconsole.localApps.data.dto.LocalAppDto
import org.example.globalconsole.systemApps.data.DesktopApplicationReader
import org.example.globalconsole.localApps.domain.usecase.IsSupportedLocalAppUseCase
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Detecta aplicaciones nativos y mantiene exclusivamente el árbol de procesos que inicia.
 * @param applicationDirectories Rutas desktop ordenadas de mayor a menor prioridad.
 * @param osName Sistema operativo; sustituible en fixtures de detección.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalAppsAccessAdapter(
    private val applicationDirectories: List<File> = defaultApplicationDirectories(),
    private val osName: String = System.getProperty("os.name")
) {
    private val process = NativeApplicationProcess()
    private val isSupported = IsSupportedLocalAppUseCase()

    /**
     * Escanea el sistema sin ejecutar ningún aplicación.
     * @return Juegos instalados y deduplicados por comando.
     * @throws Exception Si falla la detección nativa de Windows.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun scan(): List<LocalAppDto> = withContext(Dispatchers.IO) {
        val games = when {
            osName.startsWith("Linux", true) -> scanLinux()
            osName.startsWith("Windows", true) -> scanWindows()
            else -> emptyList()
        }
        games.distinctBy { it.arguments to it.workingDirectory }.sortedBy { it.name.lowercase() }
    }

    /**
     * Aplica la prioridad de accesos desktop, incluidos los ocultos del usuario.
     * @return Juegos Linux visibles.
     * @throws Nothing Los archivos individuales ilegibles se omiten.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun scanLinux(): List<LocalAppDto> {
        val seen = mutableSetOf<String>()
        return buildList {
            for (root in applicationDirectories) {
                if (!root.isDirectory) continue
                root.walkTopDown().filter { it.isFile && it.extension == "desktop" }.sortedBy { it.path }.forEach { file ->
                    val id = file.relativeTo(root).path.replace(File.separatorChar, '-')
                    if (seen.add(id)) runCatching { DesktopApplicationReader.read(file, id) }.getOrNull()?.let { entry ->
                        if (isSupported(id, entry.name, entry.arguments, entry.categories) &&
                            isSupported(entry.applicationId, entry.name, entry.arguments, entry.categories)) {
                            add(LocalAppDto("app:linux:$id", entry.name, entry.arguments, entry.iconPath, entry.workingDirectory))
                        }
                    }
                }
            }
        }
    }

    /**
     * Consulta Registro y accesos directos usando las APIs nativas de Windows.
     * @return Metadatos de aplicaciones con un ejecutable comprobado.
     * @throws IllegalStateException Si PowerShell falla o excede el plazo.
     * @throws java.io.IOException Si no puede leer el script o sus resultados.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun scanWindows(): List<LocalAppDto> {
        val script = File.createTempFile("globalconsole-apps-", ".ps1")
        val output = File.createTempFile("globalconsole-apps-", ".json")
        val errors = File.createTempFile("globalconsole-apps-", ".log")
        try {
            val resource = checkNotNull(javaClass.getResourceAsStream("/localApps/detect-windows.ps1"))
            resource.use { input -> script.outputStream().use { input.copyTo(it) } }
            val process = ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", script.absolutePath)
                .redirectOutput(output).redirectError(errors).start()
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                error("La detección de aplicaciones superó los 30 segundos")
            }
            check(process.exitValue() == 0) { "No se pudieron detectar los aplicaciones de Windows: ${errors.readText().take(300)}" }
            val rows = Json.parseToJsonElement(output.readText(Charsets.UTF_8).removePrefix("\uFEFF")).jsonArray
            return rows.mapNotNull { element ->
                val row = element.jsonObject
                val target = row["target"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val name = row["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                if (File(target).nameWithoutExtension.contains(Regex("unins|uninstall|setup", RegexOption.IGNORE_CASE))) return@mapNotNull null
                val arguments = row["arguments"]?.jsonPrimitive?.contentOrNull.orEmpty()
                val args = Shell32Util.CommandLineToArgv("\"$target\" $arguments").toList()
                if (!isSupported(row.getValue("id").jsonPrimitive.content, name, args, emptySet())) return@mapNotNull null
                LocalAppDto(
                    "app:windows:" + row.getValue("id").jsonPrimitive.content.lowercase(), name, args,
                    iconPath = row["icon"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() && File(it).exists() },
                    workingDirectory = row["directory"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() && File(it).isDirectory }
                )
            }
        } finally { script.delete(); output.delete(); errors.delete() }
    }

    /**
     * Ejecuta el aplicación mediante el gestor nativo compartido.
     * @param game Juego resuelto desde la instalación actual.
     * @return True al terminar correctamente o tras un cierre solicitado.
     * @throws Exception Si falla el lanzamiento o se cancela la espera.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun execute(game: LocalAppDto): Boolean = process.execute(game.arguments, game.workingDirectory)

    /**
     * Cierra el árbol del aplicación activo.
     * @return True cuando todos sus procesos han terminado.
     * @throws Exception Si falla el cierre.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun close(): Boolean = process.close()

    companion object {
        /**
         * Construye las rutas XDG y las exportaciones Flatpak en orden de prioridad.
         * @return Directorios únicos, con los accesos de usuario primero.
         * @throws Nothing No lanza excepciones directamente.
         * @author Daniel Figueroa Vidal
         * @since 2026-10-11
         */
        private fun defaultApplicationDirectories(): List<File> {
            val home = System.getProperty("user.home")
            val userData = System.getenv("XDG_DATA_HOME")?.takeIf { it.isNotBlank() } ?: "$home/.local/share"
            val systemData = System.getenv("XDG_DATA_DIRS")?.takeIf { it.isNotBlank() } ?: "/usr/local/share:/usr/share"
            return (listOf("$userData/applications", "$home/.local/share/flatpak/exports/share/applications") +
                systemData.split(':').map { "$it/applications" } + "/var/lib/flatpak/exports/share/applications").distinct().map(::File)
        }
    }
}
