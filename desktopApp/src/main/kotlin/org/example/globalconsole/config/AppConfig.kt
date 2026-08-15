package org.example.globalconsole.config

import kotlinx.serialization.Serializable

/**
 * Modelo unificado de configuración de la aplicación GlobalConsole.
 * Representa el contenido completo del archivo `config.json` en el directorio
 * de trabajo de la aplicación.
 *
 * Todos los repositorios que persisten datos en `config.json` deben leer este
 * objeto completo antes de escribir, modificar únicamente su propio campo y
 * reescribir el objeto entero, para evitar que una escritura parcial destruya
 * los datos de otro módulo.
 *
 * **Campos gestionados por módulo:**
 * - `emulatorPaths` → `SettingsRepositoryImpl` (módulo `settings`)
 * - `heroicEnabled` → `HGLauncherRepositoryImpl` (módulo `HeroicGames`)
 * - `melonDSEnabled` → `MelonDSRepositoryImpl` (módulo `melonDS`)
 *
 * @param emulatorPaths Mapa de identificadores de emulador (ej. "pcsx2") a rutas absolutas.
 * @param heroicEnabled True si Heroic Games Launcher debe aparecer en la biblioteca principal.
 * @param melonDSEnabled True si Melon DS Launcher debe aparecer en la biblioteca principal.
 * @param mouseSensitivity Velocidad de movimiento del ratón con el gamepad (por defecto 14f).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
@Serializable
data class AppConfig(
    val emulatorPaths: Map<String, String> = emptyMap(),
    val heroicEnabled: Boolean = false,
    val melonDSEnabled: Boolean = false,
    val mouseSensitivity: Float = 14f
)
