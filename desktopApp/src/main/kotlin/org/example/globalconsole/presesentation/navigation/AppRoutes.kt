package org.example.globalconsole.presesentation.navigation

import kotlinx.serialization.Serializable

/**
 * Define los destinos de navegación de GlobalConsole como objetos serializable de Kotlin.
 * El uso de clases/objetos @Serializable permite la navegación type-safe con la librería
 * Compose Multiplatform Navigation, eliminando el uso de cadenas de texto como rutas.
 *
 * Cada nuevo módulo o pantalla debe añadir su propio objeto de ruta aquí siguiendo
 * el mismo patrón, con sus parámetros como propiedades del data class si son necesarios.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
object AppRoutes {

    /**
     * Ruta hacia la pantalla principal de biblioteca de juegos.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-09
     */
    @Serializable
    data object Home

    // TODO: Añadir nuevas rutas aquí cuando se creen nuevas pantallas.
    // Ejemplo de ruta con parámetros:
    // @Serializable
    // data class GameDetail(val gameId: String)

    // @Serializable
    // data object Settings
}
