package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj

/**
 * Caso de uso de dominio. Obtiene los navegadores habilitados y visibles que se muestran en la biblioteca.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class GetNavigatorsWebUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Obtiene los navegadores habilitados y visibles que se muestran en la biblioteca.
     *
     * @return Lista de [NavWebObj] visibles.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(): List<NavWebObj> = withContext(Dispatchers.IO) {
        repository.getNavigatorsWeb()
    }
}
