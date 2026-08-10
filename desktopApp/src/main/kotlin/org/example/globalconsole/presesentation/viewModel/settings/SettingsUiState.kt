package org.example.globalconsole.presesentation.viewModel.settings

/**
 * Define los posibles estados de la interfaz de usuario del diálogo de configuración de rutas.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
sealed interface SettingsUiState {

    /** Estado inicial antes de cargar ningún dato. */
    data object Idle : SettingsUiState

    /** Estado de carga mientras se lee o escribe el archivo de configuración. */
    data object Loading : SettingsUiState

    /**
     * Estado de éxito con la ruta cargada o guardada correctamente.
     *
     * @param path La ruta configurada actualmente, o null si no hay ninguna.
     */
    data class Success(val path: String?) : SettingsUiState

    /**
     * Estado de error con un mensaje descriptivo.
     *
     * @param message Descripción del error ocurrido.
     */
    data class Error(val message: String) : SettingsUiState
}
