package org.example.globalconsole.juegosPcsx2.data.repository

import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Contrato de repositorio encargado de gestionar las operaciones relacionadas con los juegos de PS2.
 * Define la lógica de obtención, eliminación y ejecución de archivos de emulación de PS2.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
interface GameP2Repository {

    /**
     * Obtiene un juego de PS2 a partir de su identificador único.
     *
     * @param id Identificador único del juego.
     * @return El juego de tipo [GameP2] si existe, o null en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun getGameP2ById(id: String): GameP2?

    /**
     * Elimina un juego de PS2 físicamente del almacenamiento.
     *
     * @param id Identificador único del juego.
     * @return True si el juego fue eliminado con éxito, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun deleteGameP2(id: String): Boolean

    /**
     * Lanza la ejecución de un juego de PS2 en el emulador correspondiente.
     *
     * @param id Identificador único del juego.
     * @return True si la ejecución del proceso se inició correctamente, false en caso contrario.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun executeGameP2(id: String): Boolean

    /**
     * Obtiene la lista completa de juegos de PS2 disponibles en el sistema.
     *
     * @return Una lista de entidades [GameP2].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun getAllGamesP2(): List<GameP2>

    /**
     * Busca juegos de PS2 que contengan una cadena de texto específica en su nombre.
     *
     * @param name Texto o subcadena de búsqueda.
     * @return Lista filtrada de juegos [GameP2].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-02
     */
    suspend fun getGamesByName(name: String): List<GameP2>
}