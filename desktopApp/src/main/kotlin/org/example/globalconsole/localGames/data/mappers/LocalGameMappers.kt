package org.example.globalconsole.localGames.data.mappers

import org.example.globalconsole.localGames.data.dto.LocalGameDto
import org.example.globalconsole.localGames.domain.entitys.LocalGameObj

/**
 * Transforma metadatos nativos en una entidad de biblioteca.
 * @receiver Datos detectados en el sistema.
 * @return Juego de la plataforma LOCALGAME.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
fun LocalGameDto.toLocalGameObj(): LocalGameObj = LocalGameObj(id, name, arguments, iconPath, workingDirectory)

/**
 * Recupera los datos de ejecución de una entidad.
 * @receiver Juego de dominio.
 * @return Datos utilizados por el adaptador.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
fun LocalGameObj.toLocalGameDto(): LocalGameDto = LocalGameDto(id, name, arguments, image, workingDirectory)
