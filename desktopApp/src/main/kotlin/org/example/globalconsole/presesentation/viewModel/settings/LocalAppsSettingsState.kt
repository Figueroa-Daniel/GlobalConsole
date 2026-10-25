package org.example.globalconsole.presesentation.viewModel.settings

import org.example.globalconsole.localApps.domain.entitys.LocalAppObj

/**
 * Fila de aplicación instalado con su preferencia de biblioteca.
 * @property app Metadatos de la aplicación.
 * @property enabled Indica si aparece en la biblioteca.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
data class LocalAppSettingItem(val app: LocalAppObj, val enabled: Boolean)

/**
 * Estado único para impedir que una carga sobrescriba un cambio pendiente.
 * @property items Aplicaciones detectados y preferencias persistidas.
 * @property busy Hay una detección o escritura pendiente.
 * @property error Mensaje del último fallo, si existe.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
data class LocalAppsSettingsState(
    val items: List<LocalAppSettingItem> = emptyList(),
    val busy: Boolean = false,
    val error: String? = null
)
