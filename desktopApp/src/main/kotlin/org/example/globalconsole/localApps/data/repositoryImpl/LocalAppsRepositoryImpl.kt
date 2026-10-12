package org.example.globalconsole.localApps.data.repositoryImpl

import org.example.globalconsole.config.LocalSelectionStore
import org.example.globalconsole.localApps.data.database.LocalAppsAccessAdapter
import org.example.globalconsole.localApps.data.mappers.toLocalAppObj
import org.example.globalconsole.localApps.domain.repository.LocalAppsRepository
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj
import java.io.File

/**
 * Combina detección nativa y preferencias atómicas preservando campos ajenos.
 * @param adapter Acceso al sistema y ejecución de procesos.
 * @param configFile Configuración compartida o archivo aislado de pruebas.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class LocalAppsRepositoryImpl(
    private val adapter: LocalAppsAccessAdapter,
    private val configFile: File = File("config.json")
) : LocalAppsRepository {
    private val selection = LocalSelectionStore(configFile, "enabledLocalApps")

    /**
     * Actualiza la lista de aplicaciones instaladas.
     * @return Metadatos actuales del sistema.
     * @throws Exception Si falla la detección nativa.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun getInstalled(): List<LocalAppObj> = adapter.scan().map { it.toLocalAppObj() }

    /**
     * Cruza las preferencias con aplicaciones que siguen instaladas.
     * @return Aplicaciones habilitadas disponibles.
     * @throws Exception Si falla la detección o la lectura de preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun getEnabled(): List<LocalAppObj> {
        val enabled = selection.read()
        return adapter.scan(enabled).map { it.toLocalAppObj() }
    }

    /**
     * Habilita una aplicación instalada sin duplicar su identificador.
     * @param id Identificador estable de la aplicación.
     * @return True si cambia la selección; false si no existe o ya está habilitada.
     * @throws Exception Si no puede leer o persistir las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun enable(id: String): Boolean =
        if (adapter.scan(setOf(id)).isEmpty()) false else selection.update(id, true)

    /**
     * Retira la aplicación de la biblioteca sin borrar archivos del sistema.
     * @param id Identificador a deshabilitar.
     * @return True si cambia la selección.
     * @throws Exception Si no puede persistir las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun disable(id: String): Boolean = selection.update(id, false)

    /**
     * Consulta la preferencia almacenada.
     * @param id Identificador estable de la aplicación.
     * @return True si está habilitada.
     * @throws Exception Si no puede leer las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun isEnabled(id: String): Boolean = id in selection.read()

    /**
     * Resuelve de nuevo la aplicación para no ejecutar comandos obsoletos de la interfaz.
     * @param game Aplicación seleccionada en la biblioteca.
     * @return Resultado del proceso o false si dejó de estar instalada.
     * @throws Exception Si falla el lanzamiento.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun execute(game: LocalAppObj): Boolean {
        val current = adapter.scan(setOf(game.id)).firstOrNull { it.id == game.id } ?: return false
        return adapter.execute(current)
    }

    /**
     * Solicita el cierre del árbol de procesos activo.
     * @return True cuando la aplicación está cerrada.
     * @throws Exception Si falla el cierre.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun close(): Boolean = adapter.close()

}
