package org.example.globalconsole.localGames.data.repositoryImpl

import org.example.globalconsole.config.LocalSelectionStore
import org.example.globalconsole.localGames.data.database.LocalGamesAccessAdapter
import org.example.globalconsole.localGames.data.mappers.toLocalGameObj
import org.example.globalconsole.localGames.data.repository.LocalGamesRepository
import org.example.globalconsole.localGames.domain.entitys.LocalGameObj
import java.io.File

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
    private val selection = LocalSelectionStore(configFile, "enabledLocalGames")

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
        val enabled = selection.read()
        return adapter.scan(enabled).map { it.toLocalGameObj() }
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
        if (adapter.scan(setOf(id)).isEmpty()) false else selection.update(id, true)

    /**
     * Retira el juego de la biblioteca sin borrar archivos del sistema.
     * @param id Identificador a deshabilitar.
     * @return True si cambia la selección.
     * @throws Exception Si no puede persistir las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun disable(id: String): Boolean = selection.update(id, false)

    /**
     * Consulta la preferencia almacenada.
     * @param id Identificador estable del juego.
     * @return True si está habilitado.
     * @throws Exception Si no puede leer las preferencias.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun isEnabled(id: String): Boolean = id in selection.read()

    /**
     * Resuelve de nuevo el juego para no ejecutar comandos obsoletos de la interfaz.
     * @param game Juego seleccionado en la biblioteca.
     * @return Resultado del proceso o false si dejó de estar instalado.
     * @throws Exception Si falla el lanzamiento.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun execute(game: LocalGameObj): Boolean {
        val current = adapter.scan(setOf(game.id)).firstOrNull { it.id == game.id } ?: return false
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

}
