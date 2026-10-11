package org.example.globalconsole.localGames.data.database

import com.sun.jna.platform.win32.Shell32Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import org.example.globalconsole.localGames.data.dto.LocalGameDto
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Detecta juegos nativos y mantiene exclusivamente el árbol de procesos que inicia.
 * @param applicationDirectories Rutas desktop ordenadas de mayor a menor prioridad.
 * @param osName Sistema operativo; sustituible en fixtures de detección.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalGamesAccessAdapter(
    private val applicationDirectories: List<File> = defaultApplicationDirectories(),
    private val osName: String = System.getProperty("os.name")
) {
    private val processLock = Any()
    @Volatile private var active: Session? = null

    /**
     * Conserva los descendientes aunque el lanzador finalice antes que el juego.
     * @property process Proceso creado por GlobalConsole.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private class Session(val process: Process) {
        @Volatile var closing = false
        val children = java.util.concurrent.ConcurrentHashMap<Long, ProcessHandle>()
    }

    /**
     * Escanea el sistema sin ejecutar ningún juego.
     * @return Juegos instalados y deduplicados por comando.
     * @throws Exception Si falla la detección nativa de Windows.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun scan(): List<LocalGameDto> = withContext(Dispatchers.IO) {
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
    private fun scanLinux(): List<LocalGameDto> {
        val seen = mutableSetOf<String>()
        return buildList {
            for (root in applicationDirectories) {
                if (!root.isDirectory) continue
                root.walkTopDown().filter { it.isFile && it.extension == "desktop" }.sortedBy { it.path }.forEach { file ->
                    val id = file.relativeTo(root).path.replace(File.separatorChar, '-')
                    if (seen.add(id)) runCatching { DesktopGameReader.read(file, id) }.getOrNull()?.let { add(it) }
                }
            }
        }
    }

    /**
     * Consulta Registro y accesos directos usando las APIs nativas de Windows.
     * @return Metadatos de juegos con un ejecutable comprobado.
     * @throws IllegalStateException Si PowerShell falla o excede el plazo.
     * @throws java.io.IOException Si no puede leer el script o sus resultados.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun scanWindows(): List<LocalGameDto> {
        val script = File.createTempFile("globalconsole-games-", ".ps1")
        val output = File.createTempFile("globalconsole-games-", ".json")
        val errors = File.createTempFile("globalconsole-games-", ".log")
        try {
            val resource = checkNotNull(javaClass.getResourceAsStream("/localGames/detect-windows.ps1"))
            resource.use { input -> script.outputStream().use { input.copyTo(it) } }
            val process = ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", script.absolutePath)
                .redirectOutput(output).redirectError(errors).start()
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                error("La detección de juegos superó los 30 segundos")
            }
            check(process.exitValue() == 0) { "No se pudieron detectar los juegos de Windows: ${errors.readText().take(300)}" }
            val rows = Json.parseToJsonElement(output.readText(Charsets.UTF_8).removePrefix("\uFEFF")).jsonArray
            return rows.mapNotNull { element ->
                val row = element.jsonObject
                val target = row["target"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val name = row["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                if (File(target).nameWithoutExtension.contains(Regex("unins|uninstall|setup", RegexOption.IGNORE_CASE))) return@mapNotNull null
                val arguments = row["arguments"]?.jsonPrimitive?.contentOrNull.orEmpty()
                val args = Shell32Util.CommandLineToArgv("\"$target\" $arguments").toList()
                if (DesktopGameReader.isExcluded(name, args)) return@mapNotNull null
                LocalGameDto(
                    "local:windows:" + row.getValue("id").jsonPrimitive.content.lowercase(), name, args,
                    workingDirectory = row["directory"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() && File(it).isDirectory }
                )
            }
        } finally { script.delete(); output.delete(); errors.delete() }
    }

    /**
     * Lanza argumentos sin shell y espera al proceso y a sus descendientes conocidos.
     * @param game Metadatos resueltos desde la instalación actual.
     * @return True si termina normalmente o mediante cierre solicitado.
     * @throws Exception Si no puede iniciar el ejecutable o se cancela la espera.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun execute(game: LocalGameDto): Boolean = withContext(Dispatchers.IO) {
        require(game.arguments.isNotEmpty()) { "El juego no tiene comando de ejecución" }
        val session = synchronized(processLock) {
            check(active == null) { "Ya hay un juego local en ejecución" }
            val builder = ProcessBuilder(game.arguments).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT)
            game.workingDirectory?.let { builder.directory(File(it)) }
            Session(builder.start()).also { active = it }
        }
        try {
            while (session.process.isAlive || session.children.values.any { it.isAlive }) {
                session.process.descendants().use { stream -> stream.forEach { session.children[it.pid()] = it } }
                delay(50)
            }
            session.closing || session.process.exitValue() == 0
        } finally {
            synchronized(processLock) { if (active === session) active = null }
        }
    }

    /**
     * Cierra únicamente el árbol de procesos iniciado para el juego actual.
     * @return True si los procesos han terminado o no había juego activo.
     * @throws Exception Si el sistema impide terminar procesos o se cancela la espera.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun close(): Boolean = withContext(Dispatchers.IO) {
        val session = synchronized(processLock) { active?.also { it.closing = true } } ?: return@withContext true
        session.process.descendants().use { stream -> stream.forEach { session.children[it.pid()] = it } }
        val handles = session.children.values.toList() + session.process.toHandle()
        handles.filter { it.isAlive }.forEach { it.destroy() }
        delay(500)
        handles.filter { it.isAlive }.forEach { it.destroyForcibly() }
        session.process.waitFor(2, TimeUnit.SECONDS)
        handles.none { it.isAlive }
    }

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
