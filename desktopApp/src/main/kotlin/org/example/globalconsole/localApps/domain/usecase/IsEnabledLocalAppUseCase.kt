package org.example.globalconsole.localApps.domain.usecase

import org.example.globalconsole.localApps.domain.repository.LocalAppsRepository
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Coordina isEnabled mediante el repositorio de aplicaciones locales.
 * @param repository Contrato de datos y ejecución.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class IsEnabledLocalAppUseCase(private val repository: LocalAppsRepository) {
/**
 * Solicita isEnabled al repositorio.
 * @param id Valor de la operación.
 * @return Boolean.
 * @throws Exception Si el repositorio no puede completar la operación.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
    suspend operator fun invoke(id: String): Boolean =
        repository.isEnabled(id)
}
