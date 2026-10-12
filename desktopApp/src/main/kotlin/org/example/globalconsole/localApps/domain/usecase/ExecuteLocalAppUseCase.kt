package org.example.globalconsole.localApps.domain.usecase

import org.example.globalconsole.localApps.domain.repository.LocalAppsRepository
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Coordina execute mediante el repositorio de aplicaciones locales.
 * @param repository Contrato de datos y ejecución.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class ExecuteLocalAppUseCase(private val repository: LocalAppsRepository) {
/**
 * Solicita execute al repositorio.
 * @param game Valor de la operación.
 * @return Boolean.
 * @throws Exception Si el repositorio no puede completar la operación.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend operator fun invoke(game: LocalAppObj): Boolean =
        repository.execute(game)
}
