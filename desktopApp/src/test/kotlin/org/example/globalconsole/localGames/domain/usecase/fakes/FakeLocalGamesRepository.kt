package org.example.globalconsole.localGames.domain.usecase.fakes

import org.example.globalconsole.localGames.data.repository.LocalGamesRepository
import org.example.globalconsole.localGames.domain.entitys.LocalGameObj

/**
 * Repositorio en memoria para verificar contratos de juegos locales.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class FakeLocalGamesRepository : LocalGamesRepository {
    val installed = mutableListOf(LocalGameObj("local:test", "Juego", listOf("/games/test")))
    val enabled = linkedSetOf<String>()
    var executed: LocalGameObj? = null
    var closeCalls = 0
    var result = true

/**
 * Implementa getInstalled en memoria.
 * @return List<LocalGameObj>.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun getInstalled(): List<LocalGameObj> = installed.toList()

/**
 * Implementa getEnabled en memoria.
 * @return List<LocalGameObj>.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun getEnabled(): List<LocalGameObj> = installed.filter { it.id in enabled }

/**
 * Implementa enable en memoria.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun enable(id: String): Boolean = installed.any { it.id == id } && enabled.add(id)

/**
 * Implementa disable en memoria.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun disable(id: String): Boolean = enabled.remove(id)

/**
 * Implementa isEnabled en memoria.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun isEnabled(id: String): Boolean = id in enabled

/**
 * Implementa execute en memoria.
 * @param game Valor de la operación.
 * @return Boolean.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun execute(game: LocalGameObj): Boolean = run { executed = game; result }

/**
 * Implementa close en memoria.
 * @return Boolean.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun close(): Boolean = run { closeCalls++; result }
}
