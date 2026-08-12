package org.example.globalconsole.presesentation.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.IsHeroicEnabledUseCase
import org.example.globalconsole.settings.domain.usecase.SaveEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.SaveHeroicEnabledUseCase

/**
 * ViewModel del diálogo de configuración de rutas de emuladores y preferencias de launchers.
 * Gestiona la carga de la ruta persistida, el guardado de nuevas rutas y el toggle de
 * visibilidad de Heroic Games Launcher en la biblioteca principal.
 *
 * @param saveEmulatorPathUseCase UseCase para persistir la ruta configurada.
 * @param getEmulatorPathUseCase UseCase para recuperar la ruta persistida.
 * @param isHeroicEnabledUseCase UseCase para consultar si Heroic debe mostrarse en la biblioteca.
 * @param saveHeroicEnabledUseCase UseCase para persistir la preferencia de Heroic.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class SettingsViewModel(
    private val saveEmulatorPathUseCase: SaveEmulatorPathUseCase,
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase,
    private val isHeroicEnabledUseCase: IsHeroicEnabledUseCase,
    private val saveHeroicEnabledUseCase: SaveHeroicEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)

    /**
     * Estado observable del diálogo. Emite uno de los estados de [SettingsUiState].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _heroicEnabled = MutableStateFlow(false)

    /**
     * Estado observable del toggle de Heroic Games Launcher.
     * True indica que el launcher debe mostrarse en la biblioteca principal.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    val heroicEnabled: StateFlow<Boolean> = _heroicEnabled.asStateFlow()

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
     * @throws Exception cualquier error de escritura se captura y refleja en [SettingsUiState.Error].
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

    /**
     * Carga la preferencia de visibilidad de Heroic Games Launcher desde la persistencia
     * y actualiza el estado observable [heroicEnabled].
     *
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    fun loadHeroicEnabled() {
        viewModelScope.launch {
            _heroicEnabled.value = isHeroicEnabledUseCase()
        }
    }

    /**
     * Persiste la nueva preferencia de visibilidad de Heroic Games Launcher
     * y actualiza inmediatamente el estado observable [heroicEnabled].
     *
     * @param enabled True para mostrar Heroic en la biblioteca, false para ocultarlo.
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    fun setHeroicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            saveHeroicEnabledUseCase(enabled)
            _heroicEnabled.value = enabled
        }
    }
}
