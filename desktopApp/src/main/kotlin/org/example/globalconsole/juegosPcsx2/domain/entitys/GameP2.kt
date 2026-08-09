package org.example.globalconsole.juegosPcsx2.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Representación de un juego específico para el emulador PCSX2.
 * Hereda de la entidad base [Game] y encapsula las propiedades necesarias para PS2.
 *
 * @param id Identificador único del juego de PS2.
 * @param name Nombre visible del juego.
 * @param urlGameExecute Ruta local del archivo ISO del juego de PS2.
 * @param image Ruta local o URL de la carátula del juego.
 * @param platform Plataforma asociada, típicamente [Platforms.PCSX2].
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
data class GameP2(
    override val id: String,
    override val name: String,
    override val urlGameExecute: String,
    override val image: String?,
    override val platform: Platforms
) : Game(id, name, urlGameExecute, image, platform)
