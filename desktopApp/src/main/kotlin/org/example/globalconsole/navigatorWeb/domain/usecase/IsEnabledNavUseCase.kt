package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository

/**
 * Caso de uso de dominio. Consulta si un navegador está habilitado por el usuario.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class IsEnabledNavUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Consulta si un navegador está habilitado por el usuario.
     *
     * @param navigator Navegador a consultar.
     * @return True si está habilitado.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(navigator: NavigatorsWeb): Boolean = withContext(Dispatchers.IO) {
        repository.isEnabledNavigator(navigator)
    }
}
