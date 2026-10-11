package org.example.globalconsole.localGames.data.dto

/**
 * Metadatos del acceso directo obtenidos del sistema operativo.
 * @property id Identificador estable del acceso.
 * @property name Nombre legible.
 * @property arguments Ejecutable y argumentos separados.
 * @property iconPath Archivo de icono resuelto.
 * @property workingDirectory Directorio de trabajo opcional.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
data class LocalGameDto(
    val id: String,
    val name: String,
    val arguments: List<String>,
    val iconPath: String? = null,
    val workingDirectory: String? = null
)
