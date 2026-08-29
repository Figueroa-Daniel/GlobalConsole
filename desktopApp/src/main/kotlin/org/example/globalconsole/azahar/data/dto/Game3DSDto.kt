package org.example.globalconsole.azahar.data.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object que representa un juego de Nintendo 3DS en la capa de datos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-26
 */
@Serializable
data class Game3DSDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null,
)
