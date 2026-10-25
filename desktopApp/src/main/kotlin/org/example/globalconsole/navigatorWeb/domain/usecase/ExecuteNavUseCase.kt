package org.example.globalconsole.navigatorWeb.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj

/**
 * Caso de uso de dominio. Ejecuta un navegador y espera a que se cierre.
 * Delega en [NavWebRepository] y es [open] para permitir Fakes en los tests sin mocking.
 *
 * @property repository Repositorio de navegadores web.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
open class ExecuteNavUseCase(
    private val repository: NavWebRepository
) {

    /**
     * Ejecuta un navegador y espera a que se cierre.
     *
     * @param navWebObj Navegador a ejecutar.
     * @return True si el proceso finalizó correctamente.
     * @throws Exception Si el repositorio falla de forma inesperada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    open suspend operator fun invoke(navWebObj: NavWebObj): Boolean = withContext(Dispatchers.IO) {
        repository.executeNavigator(navWebObj)
    }
}
