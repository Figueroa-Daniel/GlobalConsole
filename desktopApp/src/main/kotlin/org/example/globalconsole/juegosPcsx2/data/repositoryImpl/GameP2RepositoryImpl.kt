package org.example.globalconsole.juegosPcsx2.data.repositoryImpl

import org.example.globalconsole.juegosPcsx2.data.database.GameP2FileSystemAdapter
import org.example.globalconsole.juegosPcsx2.data.database.GamePCSX2Adapter
import org.example.globalconsole.juegosPcsx2.data.dto.GameP2Dto
import org.example.globalconsole.juegosPcsx2.data.mappers.toDomain
import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Implementación concreta del repositorio [GameP2Repository] encargada de coordinar las operaciones
 * sobre juegos de PS2 utilizando una caché en memoria y delegando en los adaptadores correspondientes.
 *
 * @property dataSourceFile Adaptador para la lectura y gestión de archivos ISO en el almacenamiento.
 * @property dataSourcePcsx Adaptador para la invocación nativa del emulador PCSX2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
class GameP2RepositoryImpl(
    private val dataSourceFile: GameP2FileSystemAdapter,
    private val dataSourcePcsx: GamePCSX2Adapter
) : GameP2Repository {

    /**
     * Caché de juegos en memoria intermedia para evitar escaneos repetidos de disco.
     */
    private var gamesInMemoryCache = mutableListOf<GameP2Dto>()

    /**
     * Obtiene un juego de la caché en memoria a partir de su ID.
     *
     * @param id Identificador del juego.
     * @return Entidad [GameP2] si se encuentra en caché, o null en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    override suspend fun getGameP2ById(id: String): GameP2? {
        return gamesInMemoryCache.find { it.id == id }?.toDomain()
    }

    /**
     * Solicita la eliminación física de un juego al adaptador de archivos.
     *
     * @param id Identificador del juego.
     * @return True si la operación de borrado físico del archivo tuvo éxito, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    override suspend fun deleteGameP2(id: String): Boolean {
        val nameOfGameIso: String = getGameP2ById(id)?.name ?: return false
        return dataSourceFile.deleteGameInFile(nameOfGameIso)
    }

    /**
     * Busca la ruta de juego en caché y solicita su ejecución al adaptador del emulador.
     *
     * @param id Identificador del juego.
     * @return True si se inició la ejecución del emulador con éxito, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    override suspend fun executeGameP2(id: String): Boolean {
        val gameSelected: GameP2Dto? = gamesInMemoryCache.find { it.id == id }
        val executeUrl = gameSelected?.urlGameExecute
        return dataSourcePcsx.executeGame(executeUrl)
    }

    /**
     * Escanea el sistema de archivos mediante el adaptador de persistencia para refrescar la caché y retornar los juegos.
     *
     * @return Lista completa de entidades [GameP2] encontradas en el sistema.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    override suspend fun getAllGamesP2(): List<GameP2> {
        val gamesDto = dataSourceFile.getGamesInSystemFile()
        gamesInMemoryCache = gamesDto.toMutableList()
        return gamesDto.toDomain()
    }

    /**
     * Filtra la caché local de juegos en memoria por coincidencia parcial en el nombre.
     *
     * @param name Filtro de búsqueda de texto.
     * @return Lista filtrada de entidades de dominio [GameP2].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    override suspend fun getGamesByName(name: String): List<GameP2> {
        return gamesInMemoryCache.filter { game ->
            game.name.contains(name, ignoreCase = true)
        }.toDomain()
    }
}