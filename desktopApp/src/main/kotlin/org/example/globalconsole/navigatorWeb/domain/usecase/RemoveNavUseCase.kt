package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository

/**
 * Caso de uso de dominio. Deshabilita un navegador y lo retira de la biblioteca.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class RemoveNavUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Deshabilita un navegador y lo retira de la biblioteca.
     *
     * @param navigator Navegador a deshabilitar.
     * @return True si estaba habilitado y se eliminó.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(navigator: NavigatorsWeb): Boolean = withContext(Dispatchers.IO) {
        repository.deleteNavigator(navigator)
    }
}
