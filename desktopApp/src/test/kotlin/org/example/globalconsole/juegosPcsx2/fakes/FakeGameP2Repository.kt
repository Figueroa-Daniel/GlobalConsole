package org.example.globalconsole.juegosPcsx2.fakes

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Implementación de prueba (Fake) de [GameP2Repository] para simular operaciones
 * en la capa de datos sin acceder a archivos reales o lanzar procesos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-03
 */
class FakeGameP2Repository : GameP2Repository {

    /** Lista mutable para almacenar los juegos de PS2 simulados. */
    val games = mutableListOf<GameP2>()

    /** Variable para registrar si se ha ejecutado un juego y con qué ID. */
    var executedGameId: String? = null

    /** Bandera para forzar un fallo al ejecutar juegos si fuera necesario. */
    var shouldExecutionFail = false

    /** Bandera para forzar un fallo al eliminar juegos si fuera necesario. */
    var shouldDeletionFail = false

    /**
     * Obtiene un juego simulado a partir de su identificador.
     *
     * @param id Identificador único del juego.
     * @return El juego de tipo [GameP2] si existe, o null en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    override suspend fun getGameP2ById(id: String): GameP2? {
        return games.find { it.id == id }
    }

    /**
     * Simula la eliminación física de un juego del almacenamiento.
     *
     * @param id Identificador único del juego.
     * @return True si fue eliminado con éxito, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    override suspend fun deleteGameP2(id: String): Boolean {
        if (shouldDeletionFail) return false
        return games.removeIf { it.id == id }
    }

    /**
     * Simula la ejecución de un juego en el emulador.
     *
     * @param id Identificador único del juego.
     * @return True si la ejecución se inició correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    override suspend fun executeGameP2(id: String): Boolean {
        if (shouldExecutionFail) return false
        val gameExists = games.any { it.id == id }
        if (gameExists) {
            executedGameId = id
            return true
        }
        return false
    }

    /**
     * Obtiene la lista completa de juegos de PS2 simulados.
     *
     * @return Una lista de entidades [GameP2].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    override suspend fun getAllGamesP2(): List<GameP2> {
        return games.toList()
    }

    /**
     * Filtra la lista de juegos simulados por coincidencia parcial en el nombre.
     *
     * @param name Texto de búsqueda.
     * @return Lista filtrada de juegos [GameP2].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-03
     */
    override suspend fun getGamesByName(name: String): List<GameP2> {
        return games.filter { it.name.contains(name, ignoreCase = true) }
    }
}
