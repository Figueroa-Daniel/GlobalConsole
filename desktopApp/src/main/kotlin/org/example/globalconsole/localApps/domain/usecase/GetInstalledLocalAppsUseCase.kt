package org.example.globalconsole.localApps.domain.usecase

import org.example.globalconsole.localApps.domain.repository.LocalAppsRepository
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Coordina getInstalled mediante el repositorio de aplicaciones locales.
 * @param repository Contrato de datos y ejecución.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class GetInstalledLocalAppsUseCase(private val repository: LocalAppsRepository) {
/**
 * Solicita getInstalled al repositorio.
 * @return List<LocalAppObj>.
 * @throws Exception Si el repositorio no puede completar la operación.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend operator fun invoke(): List<LocalAppObj> =
        repository.getInstalled()
}
