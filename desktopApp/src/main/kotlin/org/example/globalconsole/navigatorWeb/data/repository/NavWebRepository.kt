package org.example.globalconsole.navigatorWeb.data.repository

import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj

interface NavWebRepository {

    suspend fun isNavigatorInstalled(): Pair<Boolean, List<NavigatorsWeb>>
    suspend fun hideNavigator(navigator: NavigatorsWeb): Boolean
    suspend fun showNavigator(navigator: NavigatorsWeb)
    suspend fun executeNavigator(navWebObj: NavWebObj): Boolean
    suspend fun getNavigatorsWeb(): List<NavWebObj>
    suspend fun closeLauncher(): Boolean
    suspend fun isEnabledLauncher(): Boolean
    suspend fun saveEnabledLauncher(enabled: Boolean, navigator: NavigatorsWeb): Boolean
}