package org.example.globalconsole.navigatorWeb.data.repository

import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj

/**
 * Contrato de acceso a los navegadores web. Distingue tres conjuntos:
 * los instalados en el sistema, los habilitados por el usuario (caché, los que llegan
 * a la interfaz) y, dentro de estos, los ocultos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
interface NavWebRepository {

    /** Tipos de navegador de la caché (habilitados por el usuario). */
    suspend fun isNavigatorInstalled(): List<NavigatorsWeb>

    /** Navegadores realmente instalados en el sistema, para la pantalla de configuración. */
    suspend fun getInstalledNavigators(): List<NavigatorsWeb>
    suspend fun hideNavigator(navigator: NavigatorsWeb): Boolean
    suspend fun showNavigator(navigator: NavigatorsWeb): Boolean
    suspend fun executeNavigator(navWebObj: NavWebObj): Boolean

    /** Navegadores habilitados y visibles (no ocultos) que se muestran en la biblioteca. */
    suspend fun getNavigatorsWeb(): List<NavWebObj>
    suspend fun closeNavigator(): Boolean
    suspend fun isEnabledNavigator(navigator: NavigatorsWeb): Boolean
    suspend fun saveEnabledNavigator(navigator: NavigatorsWeb): Boolean
    suspend fun deleteNavigator(navigator: NavigatorsWeb): Boolean
}