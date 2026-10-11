package org.example.globalconsole.systemApps.data

/**
 * Acceso de escritorio neutro utilizado antes de clasificarlo como juego o aplicación.
 * @property id Identificador desktop con precedencia XDG resuelta.
 * @property name Nombre localizado.
 * @property arguments Comando separado en argumentos.
 * @property iconPath Imagen local compatible.
 * @property workingDirectory Directorio de ejecución opcional.
 * @property categories Categorías declaradas por el acceso.
 * @property applicationId Identidad Flatpak, Snap o desktop.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal data class DesktopApplicationEntry(
    val id: String,
    val name: String,
    val arguments: List<String>,
    val iconPath: String?,
    val workingDirectory: String?,
    val categories: Set<String>,
    val applicationId: String
)
