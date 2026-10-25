package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository

// Este metodo es para agregar un nuevo navegador web a la lista de navegadores
/**
 * Caso de uso de dominio. Habilita un navegador instalado para que pase a ser componente de la biblioteca.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class AddNavUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Habilita un navegador instalado para que pase a ser componente de la biblioteca.
     *
     * @param navigator Navegador a habilitar.
     * @return True si se habilitó, false si no está instalado o ya estaba habilitado.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(navigator: NavigatorsWeb): Boolean = withContext(Dispatchers.IO) {
        repository.saveEnabledNavigator(navigator)
    }
}
