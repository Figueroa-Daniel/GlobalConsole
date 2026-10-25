package org.example.globalconsole.localApps.domain.usecase

import org.example.globalconsole.localApps.domain.repository.LocalAppsRepository
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Coordina getEnabled mediante el repositorio de aplicaciones locales.
 * @param repository Contrato de datos y ejecución.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class GetLocalAppsUseCase(private val repository: LocalAppsRepository) {
/**
 * Solicita getEnabled al repositorio.
 * @return List<LocalAppObj>.
 * @throws Exception Si el repositorio no puede completar la operación.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend operator fun invoke(): List<LocalAppObj> =
        repository.getEnabled()
}
