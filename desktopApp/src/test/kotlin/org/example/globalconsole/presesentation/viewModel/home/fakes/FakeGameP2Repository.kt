package org.example.globalconsole.presesentation.viewModel.home.fakes

import org.example.globalconsole.juegosPcsx2.data.repository.GameP2Repository
import org.example.globalconsole.juegosPcsx2.domain.entitys.GameP2

/**
 * Implementación fake de [GameP2Repository] para uso exclusivo en pruebas unitarias.
 * Actúa como dependencia mínima requerida por [GetGamesP2UseCase] sin recurrir a
 * la capa de datos real.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
class FakeGameP2Repository : GameP2Repository {

    /**
     * No implementado en el fake; la lógica de games se controla desde [FakeGetGamesP2UseCase].
     *
     * @param id Identificador único del juego.
     * @return Siempre null.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    override suspend fun getGameP2ById(id: String): GameP2? = null

    /**
     * No implementado en el fake.
     *
     * @param id Identificador único del juego.
     * @return Siempre false.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    override suspend fun deleteGameP2(id: String): Boolean = false

    /**
     * No implementado en el fake.
     *
     * @param id Identificador único del juego.
     * @return Siempre false.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    override suspend fun executeGameP2(id: String): Boolean = false

    /**
     * No implementado en el fake; devuelve lista vacía.
     *
     * @return Lista vacía de [GameP2].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    override suspend fun getAllGamesP2(): List<GameP2> = emptyList()

    /**
     * No implementado en el fake; devuelve lista vacía.
     *
     * @param name Texto de búsqueda.
     * @return Lista vacía de [GameP2].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    override suspend fun getGamesByName(name: String): List<GameP2> = emptyList()
}
