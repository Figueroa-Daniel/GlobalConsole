package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository

/**
 * Caso de uso de dominio. Cierra el navegador en ejecución.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class CloseNavUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Cierra el navegador en ejecución.
     *
     * @return True si se cerró correctamente.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        repository.closeNavigator()
    }
}
