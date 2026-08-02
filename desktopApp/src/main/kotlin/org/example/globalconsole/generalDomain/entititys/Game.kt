package org.example.globalconsole.generalDomain.entititys

/**
 * Entidad base que representa un juego en el frontend de GlobalConsole.
 * Define las propiedades comunes necesarias para mostrar e iniciar cualquier videojuego.
 *
 * @property id Identificador único del juego.
 * @property name Nombre visible del juego.
 * @property urlGameExecute Ruta o comando necesario para ejecutar el juego.
 * @property image Ruta local o URL de la carátula del juego.
 * @property platform Plataforma o emulador al que pertenece el juego.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-02
 */
open class Game(
    open val id: String,
    open val name: String,
    open val urlGameExecute: String,
    open val image: String?,
    open val platform: Platforms
)