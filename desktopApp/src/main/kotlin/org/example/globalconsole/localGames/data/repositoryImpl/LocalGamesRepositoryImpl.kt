package org.example.globalconsole.localGames.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.localGames.data.database.LocalGamesAccessAdapter
import org.example.globalconsole.localGames.data.mappers.toLocalGameObj
import org.example.globalconsole.localGames.data.repository.LocalGamesRepository
import org.example.globalconsole.localGames.domain.entitys.LocalGameObj
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.AtomicMoveNotSupportedException
import java.util.concurrent.ConcurrentHashMap

/**
 * Combina detección nativa y preferencias atómicas preservando campos ajenos.
 * @param adapter Acceso al sistema y ejecución de procesos.
 * @param configFile Configuración compartida o archivo aislado de pruebas.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalGamesRepositoryImpl(
    private val adapter: LocalGamesAccessAdapter,
    private val configFile: File = File("config.json")
) : LocalGamesRepository {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val mutex = locks.computeIfAbsent(configFile.absoluteFile.normalize().path) { Mutex() }

    /**
     * Actualiza la lista de juegos instalados.
     * @return Metadatos actuales del sistema.
     * @throws Exception Si falla la detección nativa.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun getInstalled(): List<LocalGameObj> = adapter.scan().map { it.toLocalGameObj() }

    /**
     * Cruza las preferencias con juegos que siguen instalados.
     * @return Juegos habilitados disponibles.
     * @throws Exception Si falla la detección o la lectura de preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun getEnabled(): List<LocalGameObj> {
        val enabled = enabledIds()
        return getInstalled().filter { it.id in enabled }
    }

    /**
     * Habilita un juego instalado sin duplicar su identificador.
     * @param id Identificador estable del juego.
     * @return True si cambia la selección; false si no existe o ya está habilitado.
     * @throws Exception Si no puede leer o persistir las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun enable(id: String): Boolean =
        if (getInstalled().none { it.id == id }) false else update(id, true)

    /**
     * Retira el juego de la biblioteca sin borrar archivos del sistema.
     * @param id Identificador a deshabilitar.
     * @return True si cambia la selección.
     * @throws Exception Si no puede persistir las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun disable(id: String): Boolean = update(id, false)

    /**
     * Consulta la preferencia almacenada.
     * @param id Identificador estable del juego.
     * @return True si está habilitado.
     * @throws Exception Si no puede leer las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun isEnabled(id: String): Boolean = id in enabledIds()

    /**
     * Resuelve de nuevo el juego para no ejecutar comandos obsoletos de la interfaz.
     * @param game Juego seleccionado en la biblioteca.
     * @return Resultado del proceso o false si dejó de estar instalado.
     * @throws Exception Si falla el lanzamiento.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun execute(game: LocalGameObj): Boolean {
        val current = adapter.scan().firstOrNull { it.id == game.id } ?: return false
        return adapter.execute(current)
    }

    /**
     * Solicita el cierre del árbol de procesos activo.
     * @return True cuando el juego está cerrado.
     * @throws Exception Si falla el cierre.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun close(): Boolean = adapter.close()

    /**
     * Lee las preferencias bajo el bloqueo compartido por archivo.
     * @return Identificadores habilitados.
     * @throws Exception Si el documento es ilegible o inválido.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun enabledIds(): Set<String> = withContext(Dispatchers.IO) {
        mutex.withLock { json.decodeFromJsonElement<AppConfig>(readDocument()).enabledLocalGames.toSet() }
    }

    /**
     * Lee el documento sin sustituir una configuración dañada por valores por defecto.
     * @return Objeto JSON completo, también con campos desconocidos.
     * @throws Exception Si el archivo existente no contiene un objeto JSON válido.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun readDocument(): JsonObject =
        if (configFile.exists()) json.parseToJsonElement(configFile.readText()).jsonObject else JsonObject(emptyMap())

    /**
     * Modifica solo enabledLocalGames y reemplaza el archivo mediante renombrado.
     * @param id Juego afectado.
     * @param enabled Nueva preferencia.
     * @return True si el archivo cambió.
     * @throws Exception Si falla la validación o escritura, conservando el archivo anterior.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun update(id: String, enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        mutex.withLock {
            val document = readDocument()
            val ids = json.decodeFromJsonElement<AppConfig>(document).enabledLocalGames.toMutableSet()
            val changed = if (enabled) ids.add(id) else ids.remove(id)
            if (!changed) return@withLock false
            val updated = JsonObject(document + ("enabledLocalGames" to JsonArray(ids.map(::JsonPrimitive))))
            val target = configFile.absoluteFile.toPath()
            Files.createDirectories(target.parent)
            val temporary = Files.createTempFile(target.parent, "local-games-", ".json")
            try {
                Files.writeString(temporary, json.encodeToString(JsonObject.serializer(), updated))
                try {
                    Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                } catch (_: AtomicMoveNotSupportedException) {
                    Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING)
                }
            } finally { Files.deleteIfExists(temporary) }
            true
        }
    }

    companion object {
        private val locks = ConcurrentHashMap<String, Mutex>()
    }
}
