package org.example.globalconsole.navigatorWeb.data.dto

import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.generalDomain.entititys.Platforms

data class NavWebObjDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null,
    val platform: Platforms = Platforms.NAVWEB,
    val navigatorType: NavigatorsWeb
)
