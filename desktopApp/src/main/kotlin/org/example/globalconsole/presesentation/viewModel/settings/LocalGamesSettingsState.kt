package org.example.globalconsole.presesentation.viewModel.settings

import org.example.globalconsole.localGames.domain.entitys.LocalGameObj

/**
 * Fila de juego instalado con su preferencia de biblioteca.
 * @property game Metadatos del juego.
 * @property enabled Indica si aparece en la biblioteca.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
data class LocalGameSettingItem(val game: LocalGameObj, val enabled: Boolean)

/**
 * Estado único para impedir que una carga sobrescriba un cambio pendiente.
 * @property items Juegos detectados y preferencias persistidas.
 * @property busy Hay una detección o escritura pendiente.
 * @property error Mensaje del último fallo, si existe.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
data class LocalGamesSettingsState(
    val items: List<LocalGameSettingItem> = emptyList(),
    val busy: Boolean = false,
    val error: String? = null
)
