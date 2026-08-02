package org.example.globalconsole.juegosPcsx2.data.dto

import kotlinx.serialization.Serializable

/**
 * Objeto de transferencia de datos (DTO) que representa un juego para el emulador PCSX2.
 * Utilizado para la serialización y persistencia local de la información del juego.
 *
 * @property id Identificador del juego generado por el sistema.
 * @property name Nombre sin extensión extraído del archivo ISO.
 * @property urlGameExecute Ruta absoluta de almacenamiento del archivo ISO.
 * @property image Ruta local de la carátula o null si no se ha asignado una.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
@Serializable
data class GameP2Dto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null,
)