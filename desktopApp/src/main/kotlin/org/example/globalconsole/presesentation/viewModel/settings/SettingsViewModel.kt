package org.example.globalconsole.presesentation.viewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.globalconsole.HeroicGames.domain.usecase.EnableHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.FindHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.HideHGLauncherUseCase
import org.example.globalconsole.settings.domain.usecase.GetEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.SaveEmulatorPathUseCase
import org.example.globalconsole.settings.domain.usecase.GetMouseSensitivityUseCase
import org.example.globalconsole.settings.domain.usecase.SaveMouseSensitivityUseCase
import org.example.globalconsole.melonDS.domain.usecase.FindMelonDSLauncherUseCase
import org.example.globalconsole.melonDS.domain.usecase.EnableMelonDSLauncherUseCase
import org.example.globalconsole.melonDS.domain.usecase.HideMelonDSLauncherUseCase

/**
 * ViewModel del diálogo de configuración de rutas de emuladores y preferencias de launchers.
 * Gestiona la carga de la ruta persistida, el guardado de nuevas rutas y el toggle de
 * visibilidad de Heroic Games Launcher en la biblioteca principal.
 *
 * La lógica de visibilidad de Heroic Games Launcher se delega a los use cases del
 * módulo HeroicGames, respetando la separación de responsabilidades de Clean Architecture.
 *
 * @param saveEmulatorPathUseCase UseCase para persistir la ruta configurada.
 * @param getEmulatorPathUseCase UseCase para recuperar la ruta persistida.
 * @param findHGLauncherUseCase UseCase para consultar si Heroic debe mostrarse en la biblioteca.
 * @param enableHGLauncherUseCase UseCase para activar la visibilidad de Heroic en la biblioteca.
 * @param hideHGLauncherUseCase UseCase para desactivar la visibilidad de Heroic en la biblioteca.
 * @param getMouseSensitivityUseCase UseCase para recuperar la sensibilidad del ratón.
 * @param saveMouseSensitivityUseCase UseCase para persistir la sensibilidad del ratón.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
class SettingsViewModel(
    private val saveEmulatorPathUseCase: SaveEmulatorPathUseCase,
    private val getEmulatorPathUseCase: GetEmulatorPathUseCase,
    private val findHGLauncherUseCase: FindHGLauncherUseCase,
    private val enableHGLauncherUseCase: EnableHGLauncherUseCase,
    private val hideHGLauncherUseCase: HideHGLauncherUseCase,
    private val getMouseSensitivityUseCase: GetMouseSensitivityUseCase,
    private val saveMouseSensitivityUseCase: SaveMouseSensitivityUseCase,
    private val findMelonDSLauncherUseCase: FindMelonDSLauncherUseCase? = null,
    private val enableMelonDSLauncherUseCase: EnableMelonDSLauncherUseCase? = null,
    private val hideMelonDSLauncherUseCase: HideMelonDSLauncherUseCase? = null,
    private val findDolphinLauncherUseCase: org.example.globalconsole.dolphin.domain.usecase.FindDolphinLauncherUseCase? = null,
    private val enableDolphinLauncherUseCase: org.example.globalconsole.dolphin.domain.usecase.EnableDolphinLauncherUseCase? = null,
    private val hideDolphinLauncherUseCase: org.example.globalconsole.dolphin.domain.usecase.HideDolphinLauncherUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)

    /**
     * Estado observable del diálogo. Emite uno de los estados de [SettingsUiState].
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-10
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _pcsx2Path = MutableStateFlow("")
    val pcsx2Path: StateFlow<String> = _pcsx2Path.asStateFlow()

    private val _melonDSGamesPath = MutableStateFlow("")
    val melonDSGamesPath: StateFlow<String> = _melonDSGamesPath.asStateFlow()

    private val _dolphinGamesPath = MutableStateFlow("")
    val dolphinGamesPath: StateFlow<String> = _dolphinGamesPath.asStateFlow()

    private val _heroicEnabled = MutableStateFlow(false)

    /**
     * Estado observable del toggle de Heroic Games Launcher.
     * True indica que el launcher debe mostrarse en la biblioteca principal.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    val heroicEnabled: StateFlow<Boolean> = _heroicEnabled.asStateFlow()

    private val _melonDSEnabled = MutableStateFlow(false)

    /**
     * Estado observable del toggle de Melon DS Launcher.
     * True indica que el launcher debe mostrarse en la biblioteca principal.
     */
    val melonDSEnabled: StateFlow<Boolean> = _melonDSEnabled.asStateFlow()

    private val _dolphinEnabled = MutableStateFlow(false)
    val dolphinEnabled: StateFlow<Boolean> = _dolphinEnabled.asStateFlow()

    private val _mouseSensitivity = MutableStateFlow(14f)

    /**
     * Estado observable de la sensibilidad del ratón con el gamepad.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    val mouseSensitivity: StateFlow<Float> = _mouseSensitivity.asStateFlow()

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
                when (emulatorId) {
                    "pcsx2" -> _pcsx2Path.value = path ?: ""
                    "melonds" -> _melonDSGamesPath.value = path ?: ""
                    "dolphinGames" -> _dolphinGamesPath.value = path ?: ""
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Error al cargar la ruta")
            }
        }
    }

    /**
     * Carga todas las rutas conocidas.
     */
    fun loadAllPaths() {
        loadCurrentPath("pcsx2")
        loadCurrentPath("melonds")
        loadCurrentPath("dolphinGames")
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
                when (emulatorId) {
                    "pcsx2" -> _pcsx2Path.value = path
                    "melonds" -> _melonDSGamesPath.value = path
                    "dolphinGames" -> _dolphinGamesPath.value = path
                }
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
            _heroicEnabled.value = findHGLauncherUseCase()
        }
    }

    /**
     * Persiste la nueva preferencia de visibilidad de Heroic Games Launcher
     * y actualiza inmediatamente el estado observable [heroicEnabled].
     * Delega la activación a [EnableHGLauncherUseCase] y la desactivación a [HideHGLauncherUseCase].
     *
     * @param enabled True para mostrar Heroic en la biblioteca, false para ocultarlo.
     * @return Unit
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    fun setHeroicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                enableHGLauncherUseCase()
            } else {
                hideHGLauncherUseCase()
            }
            _heroicEnabled.value = enabled
        }
    }

    /**
     * Carga la sensibilidad del ratón desde la persistencia y actualiza el estado.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    fun loadMouseSensitivity() {
        viewModelScope.launch {
            _mouseSensitivity.value = getMouseSensitivityUseCase()
        }
    }

    /**
     * Persiste la sensibilidad del ratón y actualiza el estado.
     *
     * @param speed Nueva sensibilidad (ej. 1f a 50f).
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    fun setMouseSensitivity(speed: Float) {
        viewModelScope.launch {
            saveMouseSensitivityUseCase(speed)
            _mouseSensitivity.value = speed
        }
    }

    /**
     * Carga la preferencia de visibilidad de Melon DS Launcher desde la persistencia
     * y actualiza el estado observable [melonDSEnabled].
     */
    fun loadMelonDSEnabled() {
        viewModelScope.launch {
            _melonDSEnabled.value = findMelonDSLauncherUseCase?.invoke() ?: false
        }
    }

    /**
     * Persiste la nueva preferencia de visibilidad de Melon DS Launcher
     * y actualiza inmediatamente el estado observable [melonDSEnabled].
     */
    fun setMelonDSEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                enableMelonDSLauncherUseCase?.invoke()
            } else {
                hideMelonDSLauncherUseCase?.invoke()
            }
            _melonDSEnabled.value = enabled
        }
    }

    fun loadDolphinEnabled() {
        viewModelScope.launch {
            _dolphinEnabled.value = findDolphinLauncherUseCase?.invoke() ?: false
        }
    }

    fun setDolphinEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                enableDolphinLauncherUseCase?.invoke()
            } else {
                hideDolphinLauncherUseCase?.invoke()
            }
            _dolphinEnabled.value = enabled
        }
    }
}
