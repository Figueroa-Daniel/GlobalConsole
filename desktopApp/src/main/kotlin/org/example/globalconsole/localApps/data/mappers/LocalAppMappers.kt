package org.example.globalconsole.localApps.data.mappers

import org.example.globalconsole.localApps.data.dto.LocalAppDto
import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Transforma metadatos nativos en una entidad de biblioteca.
 * @receiver Datos detectados en el sistema.
 * @return Aplicación de la plataforma LOCALAPP.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
fun LocalAppDto.toLocalAppObj(): LocalAppObj = LocalAppObj(id, name, arguments, iconPath, workingDirectory)

/**
 * Recupera los datos de ejecución de una entidad.
 * @receiver Aplicación de dominio.
 * @return Datos utilizados por el adaptador.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
fun LocalAppObj.toLocalAppDto(): LocalAppDto = LocalAppDto(id, name, arguments, image, workingDirectory)
