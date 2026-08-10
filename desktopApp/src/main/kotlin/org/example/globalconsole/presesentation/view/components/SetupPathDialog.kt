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
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-10
 */
private enum class DialogButton { BROWSE, CANCEL, CONFIRM }

/**
 * Diálogo modal para la selección y persistencia de la ruta de juegos de PCSX2.
 * Diseñado con estética oscura Metro (bordes blancos nítidos, sin esquinas redondeadas, negro puro).
 * 100% navegable por gamepad: D-Pad para moverse entre botones, botón A para confirmar.
 *
 * @param settingsViewModel ViewModel que gestiona la carga y guardado de la ruta.
 * @param gamepadManager Gestor de eventos de gamepad para la navegación entre botones.
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

    var pathText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var focusedButton by remember { mutableStateOf(DialogButton.CONFIRM) }

    // Precarga la ruta guardada al abrirse el diálogo
    LaunchedEffect(Unit) {
        settingsViewModel.loadCurrentPath("pcsx2")
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

    // Navegación por gamepad entre los botones del diálogo
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> {
                    focusedButton = when (event.direction) {
                        GamepadEvent.Direction.LEFT -> when (focusedButton) {
                            DialogButton.CONFIRM -> DialogButton.CANCEL
                            DialogButton.CANCEL -> DialogButton.BROWSE
                            DialogButton.BROWSE -> DialogButton.BROWSE
                        }
                        GamepadEvent.Direction.RIGHT -> when (focusedButton) {
                            DialogButton.BROWSE -> DialogButton.CANCEL
                            DialogButton.CANCEL -> DialogButton.CONFIRM
                            DialogButton.CONFIRM -> DialogButton.CONFIRM
                        }
                        else -> focusedButton
                    }
                }
                is GamepadEvent.ButtonPressed -> {
                    if (event.button == GamepadEvent.Button.CONFIRM) {
                        when (focusedButton) {
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
                Text(
                    text = "CONFIGURACIÓN DE RUTA DE JUEGOS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Selecciona la carpeta donde guardas tus juegos / ISOs para PCSX2.",
                    color = Color(0xFFCCCCCC),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 20.dp)
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
