package org.example.globalconsole.presesentation.viewModel.home

import org.example.globalconsole.generalDomain.entititys.Game

/**
 * Modos de agrupación para la biblioteca de juegos.
 */
enum class GroupingMode {
    NONE,
    PLATFORM,
    CONSOLE_SERIES
}

/**
 * Posición de los launchers (emuladores) en la vista.
 */
enum class LauncherPosition {
    TOP,     // Todos los emuladores se agrupan en la parte superior
    INLINE   // Los emuladores se muestran junto a sus juegos correspondientes
}

/**
 * Representa un elemento renderizable en la cuadrícula de la pantalla principal.
 * Puede ser un encabezado de grupo o un juego/emulador.
 */
sealed class HomeListItem {
    data class Header(val title: String) : HomeListItem()
    data class GameItem(val game: Game) : HomeListItem()
}
