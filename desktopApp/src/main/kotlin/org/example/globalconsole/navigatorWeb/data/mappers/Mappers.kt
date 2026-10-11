package org.example.globalconsole.navigatorWeb.data.mappers

import org.example.globalconsole.navigatorWeb.data.dto.NavWebObjDto
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj

fun NavWebObjDto.toNavWebObj() = NavWebObj (
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = this.platform,
        navigatorType = this.navigatorType
    )
fun NavWebObj.toNavWebObjDto() = NavWebObjDto (
        id = this.id,
        name = this.name,
        urlGameExecute = this.urlGameExecute,
        image = this.image,
        platform = this.platform,
        navigatorType = this.navigatorType
    )