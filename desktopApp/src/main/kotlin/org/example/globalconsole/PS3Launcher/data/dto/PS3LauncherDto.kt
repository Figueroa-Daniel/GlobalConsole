package org.example.globalconsole.PS3Launcher.data.dto

import kotlinx.serialization.Serializable

/**
 * Modelo de datos (DTO) que representa los datos estáticos del launcher de PS3.
 *
 * @param id Identificador único del launcher.
 * @param name Nombre a mostrar en la interfaz de usuario.
 * @param urlGameExecute Comando o ID de ejecución principal.
 * @param image Ruta de la imagen de portada (opcional).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-22
 */
@Serializable
data class PS3LauncherDto(
    val id: String,
    val name: String,
    val urlGameExecute: String,
    val image: String? = null
)
