package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository

/**
 * Caso de uso de dominio. Obtiene los navegadores instalados en el sistema para ofrecerlos en la configuración.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class GetInstalledNavsUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Obtiene los navegadores instalados en el sistema para ofrecerlos en la configuración.
     *
     * @return Lista de navegadores instalados.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(): List<NavigatorsWeb> = withContext(Dispatchers.IO) {
        repository.getInstalledNavigators()
    }
}
