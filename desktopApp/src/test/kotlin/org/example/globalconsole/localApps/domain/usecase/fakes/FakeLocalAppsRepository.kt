package org.example.globalconsole.localApps.domain.usecase.fakes

import org.example.globalconsole.localApps.domain.repository.LocalAppsRepository
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Repositorio en memoria para verificar contratos de aplicaciones locales.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class FakeLocalAppsRepository : LocalAppsRepository {
    val installed = mutableListOf(LocalAppObj("app:test", "Aplicación", listOf("/games/test")))
    val enabled = linkedSetOf<String>()
    var executed: LocalAppObj? = null
    var closeCalls = 0
    var result = true

/**
 * Implementa getInstalled en memoria.
 * @return List<LocalAppObj>.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun getInstalled(): List<LocalAppObj> = installed.toList()

/**
 * Implementa getEnabled en memoria.
 * @return List<LocalAppObj>.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun getEnabled(): List<LocalAppObj> = installed.filter { it.id in enabled }

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
    override suspend fun execute(game: LocalAppObj): Boolean = run { executed = game; result }

/**
 * Implementa close en memoria.
 * @return Boolean.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    override suspend fun close(): Boolean = run { closeCalls++; result }
}
