package org.example.globalconsole.PS3Launcher.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Entidad de dominio que representa el launcher de PS3 (RPCS3).
 * Extiende la clase base [Game] asignando la plataforma específica [Platforms.PS3].
 *
 * @param id Identificador único del launcher.
 * @param name Nombre del launcher a mostrar.
 * @param urlGameExecute Comando nativo o identificador de ejecución.
 * @param image Ruta a la imagen de portada.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
class PS3Launcher(
    id: String,
    name: String,
    urlGameExecute: String,
    image: String? = null
) : Game(
    id = id,
    name = name,
    urlGameExecute = urlGameExecute,
    image = image,
    platform = Platforms.PS3
)
