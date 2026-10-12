package org.example.globalconsole.localGames.data.repository

import org.example.globalconsole.localGames.domain.entitys.LocalGameObj

/**
 * Contrato de detección, preferencias y ejecución de juegos locales.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
interface LocalGamesRepository {

/**
 * Gestiona getInstalled para juegos locales.
 * @return List<LocalGameObj>.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun getInstalled(): List<LocalGameObj>

/**
 * Gestiona getEnabled para juegos locales.
 * @return List<LocalGameObj>.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun getEnabled(): List<LocalGameObj>

/**
 * Gestiona enable para juegos locales.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun enable(id: String): Boolean

/**
 * Gestiona disable para juegos locales.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun disable(id: String): Boolean

/**
 * Gestiona isEnabled para juegos locales.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun isEnabled(id: String): Boolean

/**
 * Gestiona execute para juegos locales.
 * @param game Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun execute(game: LocalGameObj): Boolean

/**
 * Gestiona close para juegos locales.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun close(): Boolean
}
