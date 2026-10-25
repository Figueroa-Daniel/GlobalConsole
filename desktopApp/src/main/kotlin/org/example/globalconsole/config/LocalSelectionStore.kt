package org.example.globalconsole.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap

/**
 * Persiste selecciones locales con un bloqueo compartido por archivo y reemplazo atómico.
 * @param configFile Documento compartido de configuración.
 * @param field Campo que contiene la selección de juegos o aplicaciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal class LocalSelectionStore(private val configFile: File, private val field: String) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val mutex = locks.computeIfAbsent(configFile.absoluteFile.normalize().path) { Mutex() }

    /**
     * Recupera los identificadores bajo el bloqueo de escritura.
     * @return Selección persistida.
     * @throws Exception Si el documento es ilegible o inválido.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun read(): Set<String> = withContext(Dispatchers.IO) {
        mutex.withLock { identifiers(document()) }
    }

    /**
     * Valida el documento completo antes de aceptar su contenido.
     * @return Objeto JSON conservando campos desconocidos.
     * @throws Exception Si la configuración no se puede leer o validar.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun document(): JsonObject {
        val result = if (configFile.exists()) json.parseToJsonElement(configFile.readText()).jsonObject else JsonObject(emptyMap())
        json.decodeFromJsonElement<AppConfig>(result)
        return result
    }

    /**
     * Extrae únicamente el campo gestionado por esta selección.
     * @param document Configuración previamente validada.
     * @return Identificadores sin duplicados.
     * @throws Exception Si el campo no es una lista de cadenas.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun identifiers(document: JsonObject): Set<String> =
        document[field]?.jsonArray?.map { it.jsonPrimitive.content }?.toSet().orEmpty()

    /**
     * Cambia una preferencia sin perder actualizaciones del otro módulo local.
     * @param id Identificador de la aplicación o juego.
     * @param enabled Nueva preferencia.
     * @return True si cambió el documento.
     * @throws Exception Si falla la lectura o el reemplazo del archivo.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    suspend fun update(id: String, enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        mutex.withLock {
            val document = document()
            val ids = identifiers(document).toMutableSet()
            if (!(if (enabled) ids.add(id) else ids.remove(id))) return@withLock false
            val updated = JsonObject(document + (field to JsonArray(ids.map(::JsonPrimitive))))
            val target = configFile.absoluteFile.toPath()
            Files.createDirectories(target.parent)
            val temporary = Files.createTempFile(target.parent, "local-selection-", ".json")
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
