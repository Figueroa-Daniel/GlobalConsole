package org.example.globalconsole.presesentation.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.SaveEmulatorPathUseCase

/**
 * ViewModel del diálogo de configuración de rutas de emuladores.
 * Gestiona la carga de la ruta persistida y el guardado de nuevas rutas,
 * exponiendo el estado de la operación mediante [SettingsUiState].
 *
 * @param saveEmulatorPathUseCase UseCase para persistir la ruta configurada.
 * @param getEmulatorPathUseCase UseCase para recuperar la ruta persistida.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class SettingsViewModel(
    private val saveEmulatorPathUseCase: SaveEmulatorPathUseCase,
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)

    /**
     * Estado observable del diálogo. Emite uno de los estados de [SettingsUiState].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /**
     * Carga la ruta actualmente configurada para el emulador indicado.
     * Transiciona a [SettingsUiState.Loading] durante la lectura y a
     * [SettingsUiState.Success] o [SettingsUiState.Error] al finalizar.
     *
     * @param emulatorId Identificador del emulador (ej. "pcsx2").
     * @return Unit
     * @throws Exception cualquier error de lectura se captura y refleja en [SettingsUiState.Error].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    fun loadCurrentPath(emulatorId: String) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                val path = getEmulatorPathUseCase(emulatorId)
                _uiState.value = SettingsUiState.Success(path)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Error al cargar la ruta")
            }
        }
    }

    /**
     * Persiste la ruta indicada para el emulador especificado.
     * Transiciona a [SettingsUiState.Loading] durante la escritura y a
     * [SettingsUiState.Success] o [SettingsUiState.Error] al finalizar.
     *
     * @param emulatorId Identificador del emulador (ej. "pcsx2").
     * @param path Ruta absoluta del directorio de juegos.
     * @return Unit
     * @throws Exception cualquier error de escritura o validación se captura y refleja en [SettingsUiState.Error].
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    fun savePath(emulatorId: String, path: String) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                saveEmulatorPathUseCase(emulatorId, path)
                _uiState.value = SettingsUiState.Success(path)
            } catch (e: IllegalArgumentException) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Ruta no válida")
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Error al guardar la ruta")
            }
        }
    }
}
