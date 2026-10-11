package org.example.globalconsole.navigatorWeb.domain.usecase.fakes

import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj

/**
 * Implementación fake de [NavWebRepository] para pruebas unitarias.
 * Reproduce en memoria las reglas del repositorio real: solo se habilitan navegadores
 * instalados, los ocultos siguen habilitados pero no son visibles.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class FakeNavWebRepository(
    var installed: List<NavigatorsWeb> = emptyList()
) : NavWebRepository {

    private val enabled = linkedSetOf<NavigatorsWeb>()
    private val hidden = mutableSetOf<NavigatorsWeb>()

    /** Navegador recibido en la última ejecución. */
    var lastExecuted: NavWebObj? = null

    /** Número de veces que se ha cerrado el navegador. */
    var closeCalls = 0

    /** Resultado devuelto por [executeNavigator] y [closeNavigator]. */
    var processResult = true

    override suspend fun isNavigatorInstalled(): List<NavigatorsWeb> = enabled.toList()

    override suspend fun getInstalledNavigators(): List<NavigatorsWeb> = installed

    override suspend fun hideNavigator(navigator: NavigatorsWeb): Boolean = hidden.add(navigator)

    override suspend fun showNavigator(navigator: NavigatorsWeb): Boolean = hidden.remove(navigator)

    override suspend fun executeNavigator(navWebObj: NavWebObj): Boolean {
        lastExecuted = navWebObj
        return processResult
    }

    override suspend fun getNavigatorsWeb(): List<NavWebObj> =
        enabled.map {
            NavWebObj(id = it.name, name = it.name, urlGameExecute = "run ${it.name}", navigatorType = it)
        }

    override suspend fun closeNavigator(): Boolean {
        closeCalls++
        return processResult
    }

    override suspend fun isEnabledNavigator(navigator: NavigatorsWeb): Boolean = navigator in enabled

    override suspend fun saveEnabledNavigator(navigator: NavigatorsWeb): Boolean =
        navigator in installed && enabled.add(navigator)

    override suspend fun deleteNavigator(navigator: NavigatorsWeb): Boolean {
        return enabled.remove(navigator)
    }
}
