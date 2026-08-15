package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import java.io.File

/**
 * Identificadores de los botones navegables del diálogo para la gestión del foco de gamepad.
 */
private enum class DialogButton { 
    SENSITIVITY_SLIDER, 
    HEROIC_TOGGLE, 
    MELONDS_TOGGLE, 
    PCSX2_BROWSE, 
    MELONDS_GAMES_BROWSE, 
    CANCEL, 
    CONFIRM 
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPathDialog(
    settingsViewModel: SettingsViewModel,
    gamepadManager: GamepadManager,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val heroicEnabled by settingsViewModel.heroicEnabled.collectAsState()
    val melonDSEnabled by settingsViewModel.melonDSEnabled.collectAsState()
    val mouseSensitivity by settingsViewModel.mouseSensitivity.collectAsState()
    val pcsx2PathState by settingsViewModel.pcsx2Path.collectAsState()
    val melonDSGamesPathState by settingsViewModel.melonDSGamesPath.collectAsState()

    var pathTextPcsx2 by remember(pcsx2PathState) { mutableStateOf(pcsx2PathState) }
    var pathTextMelonGames by remember(melonDSGamesPathState) { mutableStateOf(melonDSGamesPathState) }
    
    var errorMessage by remember { mutableStateOf("") }
    var focusedButton by remember { mutableStateOf(DialogButton.CONFIRM) }
    var showFolderPickerFor by remember { mutableStateOf<DialogButton?>(null) }

    LaunchedEffect(Unit) {
        settingsViewModel.loadAllPaths()
        settingsViewModel.loadHeroicEnabled()
        settingsViewModel.loadMelonDSEnabled()
        settingsViewModel.loadMouseSensitivity()
    }

    LaunchedEffect(gamepadManager, showFolderPickerFor) {
        if (showFolderPickerFor != null) return@LaunchedEffect
        
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> {
                    focusedButton = when (event.direction) {
                        GamepadEvent.Direction.UP -> when (focusedButton) {
                            DialogButton.HEROIC_TOGGLE -> DialogButton.SENSITIVITY_SLIDER
                            DialogButton.MELONDS_TOGGLE -> DialogButton.HEROIC_TOGGLE
                            DialogButton.PCSX2_BROWSE -> DialogButton.MELONDS_TOGGLE
                            DialogButton.MELONDS_GAMES_BROWSE -> DialogButton.PCSX2_BROWSE
                            DialogButton.CONFIRM, DialogButton.CANCEL -> DialogButton.MELONDS_GAMES_BROWSE
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.DOWN -> when (focusedButton) {
                            DialogButton.SENSITIVITY_SLIDER -> DialogButton.HEROIC_TOGGLE
                            DialogButton.HEROIC_TOGGLE -> DialogButton.MELONDS_TOGGLE
                            DialogButton.MELONDS_TOGGLE -> DialogButton.PCSX2_BROWSE
                            DialogButton.PCSX2_BROWSE -> DialogButton.MELONDS_GAMES_BROWSE
                            DialogButton.MELONDS_GAMES_BROWSE -> DialogButton.CONFIRM
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.LEFT -> when (focusedButton) {
                            DialogButton.CONFIRM -> DialogButton.CANCEL
                            DialogButton.SENSITIVITY_SLIDER -> {
                                val newVal = (mouseSensitivity - 2f).coerceAtLeast(1f)
                                settingsViewModel.setMouseSensitivity(newVal)
                                focusedButton
                            }
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.RIGHT -> when (focusedButton) {
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
                            DialogButton.HEROIC_TOGGLE -> settingsViewModel.setHeroicEnabled(!heroicEnabled)
                            DialogButton.MELONDS_TOGGLE -> settingsViewModel.setMelonDSEnabled(!melonDSEnabled)
                            DialogButton.SENSITIVITY_SLIDER -> {}
                            DialogButton.PCSX2_BROWSE -> showFolderPickerFor = DialogButton.PCSX2_BROWSE
                            DialogButton.MELONDS_GAMES_BROWSE -> showFolderPickerFor = DialogButton.MELONDS_GAMES_BROWSE
                            DialogButton.CANCEL -> onDismiss()
                            DialogButton.CONFIRM -> {
                                // Validaciones básicas
                                settingsViewModel.savePath("pcsx2", pathTextPcsx2)
                                settingsViewModel.savePath("melonds", pathTextMelonGames)
                                onConfirm()
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
                    .width(550.dp)
                    .fillMaxHeight(0.9f)
                    .background(Color(0xFF0F0F0F), RectangleShape)
                    .border(2.dp, Color.White, RectangleShape)
                    .padding(24.dp)
            ) {
                Text(
                    text = "CONFIGURACIÓN",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(end = 8.dp)
                ) {
                    // Control Gamepad
                    val sensitivityBorderColor = if (focusedButton == DialogButton.SENSITIVITY_SLIDER) Color(0xFF00FFCC) else Color(0xFF333333)
                    Column(
                        modifier = Modifier.fillMaxWidth().border(1.dp, sensitivityBorderColor, RectangleShape).padding(12.dp)
                    ) {
                        Text("CONTROL DEL GAMEPAD", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Sensibilidad del ratón al usar el stick derecho.", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(String.format("%.0f", mouseSensitivity), color = Color(0xFF00FFCC), fontSize = 14.sp, modifier = Modifier.width(30.dp))
                            Slider(
                                value = mouseSensitivity, onValueChange = { settingsViewModel.setMouseSensitivity(it) },
                                valueRange = 1f..50f, steps = 49, modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00FFCC), inactiveTrackColor = Color(0xFF333333))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Heroic Games
                    val heroicSectionBorderColor = if (focusedButton == DialogButton.HEROIC_TOGGLE) Color(0xFF00FFCC) else Color(0xFF333333)
                    Column(
                        modifier = Modifier.fillMaxWidth().border(1.dp, heroicSectionBorderColor, RectangleShape).padding(12.dp)
                    ) {
                        Text("HEROIC GAMES LAUNCHER", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Mostrar Heroic Games Launcher en la biblioteca.", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (heroicEnabled) "HABILITADO" else "DESHABILITADO", color = if (heroicEnabled) Color(0xFF00FFCC) else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Switch(
                                checked = heroicEnabled, onCheckedChange = { settingsViewModel.setHeroicEnabled(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFF00FFCC), uncheckedThumbColor = Color.Gray, uncheckedTrackColor = Color(0xFF333333))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Melon DS Launcher Toggle
                    val melonToggleBorderColor = if (focusedButton == DialogButton.MELONDS_TOGGLE) Color(0xFF00FFCC) else Color(0xFF333333)
                    Column(
                        modifier = Modifier.fillMaxWidth().border(1.dp, melonToggleBorderColor, RectangleShape).padding(12.dp)
                    ) {
                        Text("MELON DS LAUNCHER", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Mostrar Melon DS Launcher en la biblioteca.", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (melonDSEnabled) "HABILITADO" else "DESHABILITADO", color = if (melonDSEnabled) Color(0xFF00FFCC) else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Switch(
                                checked = melonDSEnabled, onCheckedChange = { settingsViewModel.setMelonDSEnabled(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFF00FFCC), uncheckedThumbColor = Color.Gray, uncheckedTrackColor = Color(0xFF333333))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // PCSX2 Path
                    Text("RUTA DE JUEGOS (PCSX2)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = pathTextPcsx2, onValueChange = { pathTextPcsx2 = it }, modifier = Modifier.weight(1f).border(1.dp, Color.Gray, RectangleShape),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF161616), unfocusedContainerColor = Color(0xFF161616), disabledContainerColor = Color(0xFF161616), focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        MetroButton(text = "EXAMINAR", isPrimary = focusedButton == DialogButton.PCSX2_BROWSE, isFocused = focusedButton == DialogButton.PCSX2_BROWSE, onClick = { showFolderPickerFor = DialogButton.PCSX2_BROWSE })
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Melon DS Games Path
                    Text("RUTA DE JUEGOS (MELON DS)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = pathTextMelonGames, onValueChange = { pathTextMelonGames = it }, modifier = Modifier.weight(1f).border(1.dp, Color.Gray, RectangleShape),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF161616), unfocusedContainerColor = Color(0xFF161616), disabledContainerColor = Color(0xFF161616), focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        MetroButton(text = "EXAMINAR", isPrimary = focusedButton == DialogButton.MELONDS_GAMES_BROWSE, isFocused = focusedButton == DialogButton.MELONDS_GAMES_BROWSE, onClick = { showFolderPickerFor = DialogButton.MELONDS_GAMES_BROWSE })
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    MetroButton(text = "CANCELAR", isFocused = focusedButton == DialogButton.CANCEL, onClick = onDismiss)
                    Spacer(modifier = Modifier.width(16.dp))
                    MetroButton(
                        text = "GUARDAR", isPrimary = true, isFocused = focusedButton == DialogButton.CONFIRM,
                        onClick = {
                            settingsViewModel.savePath("pcsx2", pathTextPcsx2)
                            settingsViewModel.savePath("melonds", pathTextMelonGames)
                            onConfirm()
                        }
                    )
                }
            }
        }
    }

    if (showFolderPickerFor != null) {
        val initialPath = when (showFolderPickerFor) {
            DialogButton.PCSX2_BROWSE -> pathTextPcsx2
            DialogButton.MELONDS_GAMES_BROWSE -> pathTextMelonGames
            else -> ""
        }
        val safeInitial = if (initialPath.isNotBlank() && File(initialPath).exists()) initialPath else System.getProperty("user.home")
        
        GamepadFolderPickerDialog(
            gamepadManager = gamepadManager,
            initialPath = safeInitial,
            onDirectorySelected = { selectedPath ->
                when (showFolderPickerFor) {
                    DialogButton.PCSX2_BROWSE -> pathTextPcsx2 = selectedPath
                    DialogButton.MELONDS_GAMES_BROWSE -> pathTextMelonGames = selectedPath
                    else -> {}
                }
                errorMessage = ""
                showFolderPickerFor = null
            },
            onDismiss = {
                showFolderPickerFor = null
            }
        )
    }
}
