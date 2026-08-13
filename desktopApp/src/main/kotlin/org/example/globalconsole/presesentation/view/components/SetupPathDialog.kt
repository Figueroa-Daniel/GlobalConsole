package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.viewModel.settings.SettingsUiState
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import java.io.File
import javax.swing.JFileChooser

/**
 * Identificadores de los botones navegables del diálogo para la gestión del foco de gamepad.
 * Incluye los tres botones de acción y el toggle de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
private enum class DialogButton { SENSITIVITY_SLIDER, HEROIC_TOGGLE, BROWSE, CANCEL, CONFIRM }

/**
 * Diálogo modal de configuración de la aplicación GlobalConsole.
 * Permite configurar la ruta de juegos de PCSX2 y habilitar/deshabilitar
 * Heroic Games Launcher en la biblioteca principal.
 *
 * Diseñado con estética oscura Metro (bordes blancos nítidos, sin esquinas redondeadas, negro puro).
 * 100% navegable por gamepad: D-Pad para moverse entre elementos, botón A para confirmar.
 *
 * Navegación por D-Pad:
 * - UP/DOWN: alterna entre secciones (Sensibilidad, Heroic, PCSX2).
 * - LEFT/RIGHT: navega entre botones o ajusta el valor del slider.
 *
 * @param settingsViewModel ViewModel que gestiona la carga y guardado de rutas y preferencias.
 * @param gamepadManager Gestor de eventos de gamepad para la navegación entre elementos.
 * @param onDismiss Llamado al cerrar el diálogo.
 * @param onConfirm Llamado tras guardar con éxito la nueva ruta.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPathDialog(
    settingsViewModel: SettingsViewModel,
    gamepadManager: GamepadManager,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val heroicEnabled by settingsViewModel.heroicEnabled.collectAsState()
    val mouseSensitivity by settingsViewModel.mouseSensitivity.collectAsState()

    var pathText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var focusedButton by remember { mutableStateOf(DialogButton.CONFIRM) }

    // Precarga la ruta guardada, la preferencia de Heroic y la sensibilidad
    LaunchedEffect(Unit) {
        settingsViewModel.loadCurrentPath("pcsx2")
        settingsViewModel.loadHeroicEnabled()
        settingsViewModel.loadMouseSensitivity()
    }

    // Sincroniza el campo de texto cuando el estado carga la ruta persistida
    LaunchedEffect(uiState) {
        if (uiState is SettingsUiState.Success) {
            val loadedPath = (uiState as SettingsUiState.Success).path
            if (loadedPath != null && pathText.isBlank()) {
                pathText = loadedPath
            }
        }
    }

    // Navegación por gamepad entre todos los elementos del diálogo
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> {
                    focusedButton = when (event.direction) {
                        GamepadEvent.Direction.UP -> when (focusedButton) {
                            DialogButton.HEROIC_TOGGLE -> DialogButton.SENSITIVITY_SLIDER
                            DialogButton.CONFIRM, DialogButton.CANCEL, DialogButton.BROWSE -> DialogButton.HEROIC_TOGGLE
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.DOWN -> when (focusedButton) {
                            DialogButton.SENSITIVITY_SLIDER -> DialogButton.HEROIC_TOGGLE
                            DialogButton.HEROIC_TOGGLE -> DialogButton.CONFIRM
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.LEFT -> when (focusedButton) {
                            DialogButton.CONFIRM -> DialogButton.CANCEL
                            DialogButton.CANCEL -> DialogButton.BROWSE
                            DialogButton.SENSITIVITY_SLIDER -> {
                                val newVal = (mouseSensitivity - 2f).coerceAtLeast(1f)
                                settingsViewModel.setMouseSensitivity(newVal)
                                focusedButton
                            }
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.RIGHT -> when (focusedButton) {
                            DialogButton.BROWSE -> DialogButton.CANCEL
                            DialogButton.CANCEL -> DialogButton.CONFIRM
                            DialogButton.SENSITIVITY_SLIDER -> {
                                val newVal = (mouseSensitivity + 2f).coerceAtMost(50f)
                                settingsViewModel.setMouseSensitivity(newVal)
                                focusedButton
                            }
                            else -> focusedButton
                        }
                    }
                }
                is GamepadEvent.ButtonPressed -> {
                    if (event.button == GamepadEvent.Button.CONFIRM) {
                        when (focusedButton) {
                            // Alternar el toggle de Heroic con el botón de confirmación
                            DialogButton.HEROIC_TOGGLE -> {
                                settingsViewModel.setHeroicEnabled(!heroicEnabled)
                            }
                            DialogButton.SENSITIVITY_SLIDER -> {
                                // No hace falta confirmar nada con el botón A en el slider,
                                // se ajusta directamente con las direcciones LEFT/RIGHT.
                            }
                            DialogButton.BROWSE -> {
                                val chooser = JFileChooser().apply {
                                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                    dialogTitle = "Selecciona la carpeta de Juegos"
                                }
                                val result = chooser.showOpenDialog(null)
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    pathText = chooser.selectedFile.absolutePath
                                    errorMessage = ""
                                }
                            }
                            DialogButton.CANCEL -> onDismiss()
                            DialogButton.CONFIRM -> {
                                if (pathText.isBlank()) {
                                    errorMessage = "La ruta no puede estar vacía"
                                } else {
                                    val file = File(pathText)
                                    if (file.exists() && file.isDirectory) {
                                        settingsViewModel.savePath("pcsx2", pathText)
                                        onConfirm()
                                    } else {
                                        errorMessage = "La ruta no es un directorio válido"
                                    }
                                }
                            }
                        }
                    }
                    if (event.button == GamepadEvent.Button.BACK) {
                        onDismiss()
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(500.dp)
                    .background(Color(0xFF0F0F0F), RectangleShape)
                    .border(2.dp, Color.White, RectangleShape)
                    .padding(24.dp)
            ) {
                // ── Cabecera ──────────────────────────────────────────────────
                Text(
                    text = "CONFIGURACIÓN",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // ── Sección: Control del Gamepad ──────────────────────────────
                val sensitivityBorderColor = if (focusedButton == DialogButton.SENSITIVITY_SLIDER) {
                    Color(0xFF00FFCC)
                } else {
                    Color(0xFF333333)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, sensitivityBorderColor, RectangleShape)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "CONTROL DEL GAMEPAD",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sensibilidad del ratón al usar el stick derecho.",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%.0f", mouseSensitivity),
                            color = Color(0xFF00FFCC),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(30.dp)
                        )

                        Slider(
                            value = mouseSensitivity,
                            onValueChange = { settingsViewModel.setMouseSensitivity(it) },
                            valueRange = 1f..50f,
                            steps = 49,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color(0xFF00FFCC),
                                inactiveTrackColor = Color(0xFF333333)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Sección: Heroic Games Launcher ────────────────────────────
                val heroicSectionBorderColor = if (focusedButton == DialogButton.HEROIC_TOGGLE) {
                    Color(0xFF00FFCC)
                } else {
                    Color(0xFF333333)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, heroicSectionBorderColor, RectangleShape)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "HEROIC GAMES LAUNCHER",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Mostrar Heroic Games Launcher en la biblioteca principal.",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (heroicEnabled) "HABILITADO" else "DESHABILITADO",
                            color = if (heroicEnabled) Color(0xFF00FFCC) else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Switch(
                            checked = heroicEnabled,
                            onCheckedChange = { settingsViewModel.setHeroicEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color(0xFF00FFCC),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF333333)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Sección: Ruta de juegos PCSX2 ─────────────────────────────
                Text(
                    text = "RUTA DE JUEGOS (PCSX2)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Selecciona la carpeta donde guardas tus juegos / ISOs para PCSX2.",
                    color = Color(0xFFCCCCCC),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = pathText,
                        onValueChange = {
                            pathText = it
                            errorMessage = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, RectangleShape),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF161616),
                            unfocusedContainerColor = Color(0xFF161616),
                            disabledContainerColor = Color(0xFF161616),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    MetroButton(
                        text = "EXAMINAR",
                        isPrimary = focusedButton == DialogButton.BROWSE,
                        isFocused = focusedButton == DialogButton.BROWSE,
                        onClick = {
                            val chooser = JFileChooser().apply {
                                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                dialogTitle = "Selecciona la carpeta de Juegos"
                            }
                            val result = chooser.showOpenDialog(null)
                            if (result == JFileChooser.APPROVE_OPTION) {
                                pathText = chooser.selectedFile.absolutePath
                                errorMessage = ""
                            }
                        }
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Botones de acción ─────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MetroButton(
                        text = "CANCELAR",
                        isFocused = focusedButton == DialogButton.CANCEL,
                        onClick = onDismiss
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    MetroButton(
                        text = "GUARDAR",
                        isPrimary = true,
                        isFocused = focusedButton == DialogButton.CONFIRM,
                        onClick = {
                            if (pathText.isBlank()) {
                                errorMessage = "La ruta no puede estar vacía"
                            } else {
                                val file = File(pathText)
                                if (file.exists() && file.isDirectory) {
                                    settingsViewModel.savePath("pcsx2", pathText)
                                    onConfirm()
                                } else {
                                    errorMessage = "La ruta no es un directorio válido"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
