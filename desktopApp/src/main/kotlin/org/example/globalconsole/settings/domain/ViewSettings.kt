package org.example.globalconsole.settings.domain

/**
 * Configuración de vista de la biblioteca persistida por el usuario.
 * Se almacena como nombres de enum (String) para que el dominio no dependa de la capa de presentación.
 *
 * @property groupingMode Nombre del modo de agrupación (ej. "NONE", "PLATFORM", "CONSOLE_SERIES").
 * @property launcherPosition Nombre de la posición de launchers (ej. "TOP", "INLINE").
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
data class ViewSettings(
    val groupingMode: String = "NONE",
    val launcherPosition: String = "INLINE"
)
