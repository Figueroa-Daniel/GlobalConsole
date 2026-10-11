package org.example.globalconsole.localApps.domain.repository

import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Contrato de detección, preferencias y ejecución de aplicaciones locales.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
interface LocalAppsRepository {

/**
 * Gestiona getInstalled para aplicaciones locales.
 * @return List<LocalAppObj>.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun getInstalled(): List<LocalAppObj>

/**
 * Gestiona getEnabled para aplicaciones locales.
 * @return List<LocalAppObj>.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun getEnabled(): List<LocalAppObj>

/**
 * Gestiona enable para aplicaciones locales.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun enable(id: String): Boolean

/**
 * Gestiona disable para aplicaciones locales.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun disable(id: String): Boolean

/**
 * Gestiona isEnabled para aplicaciones locales.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun isEnabled(id: String): Boolean

/**
 * Gestiona execute para aplicaciones locales.
 * @param game Valor de la operación.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun execute(game: LocalAppObj): Boolean

/**
 * Gestiona close para aplicaciones locales.
 * @return Boolean.
 * @throws Exception Si falla el acceso a datos o al proceso.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend fun close(): Boolean
}
