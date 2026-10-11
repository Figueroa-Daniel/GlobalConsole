package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import org.jetbrains.compose.resources.stringResource
import globalconsole.shared.generated.resources.*
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import java.io.File

private enum class DialogButton {
    SENSITIVITY_SLIDER,
    HEROIC_TOGGLE,
    MELONDS_TOGGLE,
    DOLPHIN_TOGGLE,
    PS3_TOGGLE,
    AZAHAR_TOGGLE,
    DUCKSTATION_TOGGLE,
    NAVIGATORS_OPEN,
    PCSX2_BROWSE,
    MELONDS_GAMES_BROWSE,
    DOLPHIN_GAMES_BROWSE,
    AZAHAR_GAMES_BROWSE,
    DUCKSTATION_GAMES_BROWSE,
    CANCEL,
    CONFIRM
}

/**
 * Configura rutas y launchers con foco por mando y aislamiento del selector de carpetas.
 * @param settingsViewModel Estado y guardado de los ajustes.
 * @param gamepadManager Entrada nativa del mando.
 * @param onDismiss Cierra el diálogo.
 * @param onConfirm Notifica la confirmación de las rutas.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val dolphinEnabled by settingsViewModel.dolphinEnabled.collectAsState()
    val ps3Enabled by settingsViewModel.ps3Enabled.collectAsState()
    val dolphinGamesPathState by settingsViewModel.dolphinGamesPath.collectAsState()
    val azaharEnabled by settingsViewModel.azaharEnabled.collectAsState()
    val azaharGamesPathState by settingsViewModel.azaharGamesPath.collectAsState()
    val duckStationEnabled by settingsViewModel.duckStationEnabled.collectAsState()
    val duckStationGamesPathState by settingsViewModel.duckstationGamesPath.collectAsState()

    var pathTextPcsx2 by remember(pcsx2PathState) { mutableStateOf(pcsx2PathState) }
    var pathTextMelonGames by remember(melonDSGamesPathState) { mutableStateOf(melonDSGamesPathState) }
    var pathTextDolphinGames by remember(dolphinGamesPathState) { mutableStateOf(dolphinGamesPathState) }
    var pathTextAzaharGames by remember(azaharGamesPathState) { mutableStateOf(azaharGamesPathState) }
    var pathTextDuckStationGames by remember(duckStationGamesPathState) { mutableStateOf(duckStationGamesPathState) }

    val inputMode by gamepadManager.inputMode.collectAsState()
    var errorMessage by remember { mutableStateOf("") }
    var focusedButton by remember { mutableStateOf(DialogButton.CONFIRM) }
    var showFolderPickerFor by remember { mutableStateOf<DialogButton?>(null) }
    var showNavigators by remember { mutableStateOf(false) }

    // Requesters para auto-scroll del gamepad
    val requesters = remember { DialogButton.values().associateWith { BringIntoViewRequester() } }

    LaunchedEffect(focusedButton) {
        requesters[focusedButton]?.bringIntoView()
    }

    LaunchedEffect(Unit) {
        settingsViewModel.loadAllPaths()
        settingsViewModel.loadHeroicEnabled()
        settingsViewModel.loadMelonDSEnabled()
        settingsViewModel.loadDolphinEnabled()
        settingsViewModel.loadPs3Enabled()
        settingsViewModel.loadAzaharEnabled()
        settingsViewModel.loadDuckStationEnabled()
        settingsViewModel.loadMouseSensitivity()
    }

    LaunchedEffect(gamepadManager, showFolderPickerFor, showNavigators) {
        if (showFolderPickerFor != null || showNavigators) return@LaunchedEffect

        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> {
                    focusedButton = when (event.direction) {
                        GamepadEvent.Direction.UP -> when (focusedButton) {
                            DialogButton.HEROIC_TOGGLE -> DialogButton.SENSITIVITY_SLIDER
                            DialogButton.MELONDS_TOGGLE -> DialogButton.HEROIC_TOGGLE
                            DialogButton.DOLPHIN_TOGGLE -> DialogButton.MELONDS_TOGGLE
                            DialogButton.PS3_TOGGLE -> DialogButton.DOLPHIN_TOGGLE
                            DialogButton.AZAHAR_TOGGLE -> DialogButton.PS3_TOGGLE
                            DialogButton.DUCKSTATION_TOGGLE -> DialogButton.AZAHAR_TOGGLE

                            DialogButton.MELONDS_GAMES_BROWSE -> DialogButton.PCSX2_BROWSE
                            DialogButton.DOLPHIN_GAMES_BROWSE -> DialogButton.MELONDS_GAMES_BROWSE
                            DialogButton.AZAHAR_GAMES_BROWSE -> DialogButton.DOLPHIN_GAMES_BROWSE
                            DialogButton.DUCKSTATION_GAMES_BROWSE -> DialogButton.AZAHAR_GAMES_BROWSE

                            DialogButton.NAVIGATORS_OPEN -> DialogButton.DUCKSTATION_TOGGLE
                            DialogButton.CANCEL -> DialogButton.NAVIGATORS_OPEN
                            DialogButton.CONFIRM -> DialogButton.DUCKSTATION_GAMES_BROWSE
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.DOWN -> when (focusedButton) {
                            DialogButton.SENSITIVITY_SLIDER -> DialogButton.HEROIC_TOGGLE
                            DialogButton.HEROIC_TOGGLE -> DialogButton.MELONDS_TOGGLE
                            DialogButton.MELONDS_TOGGLE -> DialogButton.DOLPHIN_TOGGLE
                            DialogButton.DOLPHIN_TOGGLE -> DialogButton.PS3_TOGGLE
                            DialogButton.PS3_TOGGLE -> DialogButton.AZAHAR_TOGGLE
                            DialogButton.AZAHAR_TOGGLE -> DialogButton.DUCKSTATION_TOGGLE
                            DialogButton.DUCKSTATION_TOGGLE -> DialogButton.NAVIGATORS_OPEN
                            DialogButton.NAVIGATORS_OPEN -> DialogButton.CANCEL

                            DialogButton.PCSX2_BROWSE -> DialogButton.MELONDS_GAMES_BROWSE
                            DialogButton.MELONDS_GAMES_BROWSE -> DialogButton.DOLPHIN_GAMES_BROWSE
                            DialogButton.DOLPHIN_GAMES_BROWSE -> DialogButton.AZAHAR_GAMES_BROWSE
                            DialogButton.AZAHAR_GAMES_BROWSE -> DialogButton.DUCKSTATION_GAMES_BROWSE
                            DialogButton.DUCKSTATION_GAMES_BROWSE -> DialogButton.CONFIRM
                            else -> focusedButton
                        }
                        GamepadEvent.Direction.LEFT -> when (focusedButton) {
                            DialogButton.CONFIRM -> DialogButton.CANCEL
                            DialogButton.PCSX2_BROWSE -> DialogButton.HEROIC_TOGGLE
                            DialogButton.MELONDS_GAMES_BROWSE -> DialogButton.MELONDS_TOGGLE
                            DialogButton.DOLPHIN_GAMES_BROWSE -> DialogButton.DOLPHIN_TOGGLE
                            DialogButton.AZAHAR_GAMES_BROWSE -> DialogButton.AZAHAR_TOGGLE
                            DialogButton.DUCKSTATION_GAMES_BROWSE -> DialogButton.DUCKSTATION_TOGGLE
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
                            DialogButton.HEROIC_TOGGLE -> DialogButton.PCSX2_BROWSE
                            DialogButton.MELONDS_TOGGLE -> DialogButton.MELONDS_GAMES_BROWSE
                            DialogButton.DOLPHIN_TOGGLE -> DialogButton.DOLPHIN_GAMES_BROWSE
                            DialogButton.PS3_TOGGLE -> DialogButton.AZAHAR_GAMES_BROWSE // PS3 has no path browse, map to Azahar
                            DialogButton.AZAHAR_TOGGLE -> DialogButton.AZAHAR_GAMES_BROWSE
                            DialogButton.DUCKSTATION_TOGGLE -> DialogButton.DUCKSTATION_GAMES_BROWSE
                            else -> focusedButton
                        }
                    }
                }
                is GamepadEvent.ButtonPressed -> {
                    if (event.button == GamepadEvent.Button.CONFIRM) {
                        when (focusedButton) {
                            DialogButton.HEROIC_TOGGLE -> settingsViewModel.setHeroicEnabled(!heroicEnabled)
                            DialogButton.MELONDS_TOGGLE -> settingsViewModel.setMelonDSEnabled(!melonDSEnabled)
                            DialogButton.DOLPHIN_TOGGLE -> settingsViewModel.setDolphinEnabled(!dolphinEnabled)
                            DialogButton.PS3_TOGGLE -> settingsViewModel.setPs3Enabled(!ps3Enabled)
                            DialogButton.AZAHAR_TOGGLE -> settingsViewModel.setAzaharEnabled(!azaharEnabled)
                            DialogButton.DUCKSTATION_TOGGLE -> settingsViewModel.setDuckStationEnabled(!duckStationEnabled)
                            DialogButton.NAVIGATORS_OPEN -> showNavigators = true
                            DialogButton.SENSITIVITY_SLIDER -> {}
                            DialogButton.PCSX2_BROWSE -> showFolderPickerFor = DialogButton.PCSX2_BROWSE
                            DialogButton.MELONDS_GAMES_BROWSE -> showFolderPickerFor = DialogButton.MELONDS_GAMES_BROWSE
                            DialogButton.DOLPHIN_GAMES_BROWSE -> showFolderPickerFor = DialogButton.DOLPHIN_GAMES_BROWSE
                            DialogButton.AZAHAR_GAMES_BROWSE -> showFolderPickerFor = DialogButton.AZAHAR_GAMES_BROWSE
                            DialogButton.DUCKSTATION_GAMES_BROWSE -> showFolderPickerFor = DialogButton.DUCKSTATION_GAMES_BROWSE
                            DialogButton.CANCEL -> onDismiss()
                            DialogButton.CONFIRM -> {
                                settingsViewModel.savePath("pcsx2", pathTextPcsx2)
                                settingsViewModel.savePath("melonds", pathTextMelonGames)
                                settingsViewModel.savePath("dolphinGames", pathTextDolphinGames)
                                settingsViewModel.savePath("azahar", pathTextAzaharGames)
                                settingsViewModel.savePath("duckstation", pathTextDuckStationGames)
                                onConfirm()
                            }
                        }
                    }
                    if (event.button == GamepadEvent.Button.BACK) {
                        onDismiss()
                    }
                }
                else -> {}
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
                    .widthIn(max = 980.dp).fillMaxWidth(.94f) // Ampliado para dos columnas
                    .fillMaxHeight(0.9f)
                    .metroPanel(LocalMetroPalette.current)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.setup_title),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                val scrollState = rememberScrollState()

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // COLUMNA IZQUIERDA (Toggles y Sensibilidad)
                    Column(modifier = Modifier.weight(1f)) {

                        // Control Gamepad
                        val sensitivityBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.SENSITIVITY_SLIDER) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.SENSITIVITY_SLIDER]!!).border(1.dp, sensitivityBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_gamepad_controls), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_mouse_sensitivity), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(String.format("%.0f", mouseSensitivity), color = LocalMetroPalette.current.accent, fontSize = 14.sp, modifier = Modifier.width(30.dp))
                                Slider(
                                    value = mouseSensitivity, onValueChange = { settingsViewModel.setMouseSensitivity(it) },
                                    valueRange = 1f..50f, steps = 49, modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = LocalMetroPalette.current.accent, inactiveTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // Heroic Games
                        val heroicSectionBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.HEROIC_TOGGLE) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.HEROIC_TOGGLE]!!).border(1.dp, heroicSectionBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_heroic_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_heroic_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stringResource(if (heroicEnabled) Res.string.status_enabled else Res.string.status_disabled), color = if (heroicEnabled) LocalMetroPalette.current.accent else LocalMetroPalette.current.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = heroicEnabled, onCheckedChange = { settingsViewModel.setHeroicEnabled(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LocalMetroPalette.current.accent, uncheckedThumbColor = LocalMetroPalette.current.muted, uncheckedTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // Melon DS
                        val melonToggleBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.MELONDS_TOGGLE) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.MELONDS_TOGGLE]!!).border(1.dp, melonToggleBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_melonds_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_melonds_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stringResource(if (melonDSEnabled) Res.string.status_enabled else Res.string.status_disabled), color = if (melonDSEnabled) LocalMetroPalette.current.accent else LocalMetroPalette.current.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = melonDSEnabled, onCheckedChange = { settingsViewModel.setMelonDSEnabled(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LocalMetroPalette.current.accent, uncheckedThumbColor = LocalMetroPalette.current.muted, uncheckedTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // Dolphin
                        val dolphinToggleBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.DOLPHIN_TOGGLE) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.DOLPHIN_TOGGLE]!!).border(1.dp, dolphinToggleBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_dolphin_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_dolphin_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stringResource(if (dolphinEnabled) Res.string.status_enabled else Res.string.status_disabled), color = if (dolphinEnabled) LocalMetroPalette.current.accent else LocalMetroPalette.current.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = dolphinEnabled, onCheckedChange = { settingsViewModel.setDolphinEnabled(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LocalMetroPalette.current.accent, uncheckedThumbColor = LocalMetroPalette.current.muted, uncheckedTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // PS3
                        val ps3ToggleBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.PS3_TOGGLE) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.PS3_TOGGLE]!!).border(1.dp, ps3ToggleBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_ps3_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_ps3_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stringResource(if (ps3Enabled) Res.string.status_enabled else Res.string.status_disabled), color = if (ps3Enabled) LocalMetroPalette.current.accent else LocalMetroPalette.current.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = ps3Enabled, onCheckedChange = { settingsViewModel.setPs3Enabled(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LocalMetroPalette.current.accent, uncheckedThumbColor = LocalMetroPalette.current.muted, uncheckedTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // Azahar
                        val azaharToggleBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.AZAHAR_TOGGLE) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.AZAHAR_TOGGLE]!!).border(1.dp, azaharToggleBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_azahar_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_azahar_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stringResource(if (azaharEnabled) Res.string.status_enabled else Res.string.status_disabled), color = if (azaharEnabled) LocalMetroPalette.current.accent else LocalMetroPalette.current.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = azaharEnabled, onCheckedChange = { settingsViewModel.setAzaharEnabled(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LocalMetroPalette.current.accent, uncheckedThumbColor = LocalMetroPalette.current.muted, uncheckedTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // DuckStation
                        val duckStationToggleBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.DUCKSTATION_TOGGLE) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.DUCKSTATION_TOGGLE]!!).border(1.dp, duckStationToggleBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_duckstation_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_duckstation_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stringResource(if (duckStationEnabled) Res.string.status_enabled else Res.string.status_disabled), color = if (duckStationEnabled) LocalMetroPalette.current.accent else LocalMetroPalette.current.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = duckStationEnabled, onCheckedChange = { settingsViewModel.setDuckStationEnabled(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LocalMetroPalette.current.accent, uncheckedThumbColor = LocalMetroPalette.current.muted, uncheckedTrackColor = LocalMetroPalette.current.border)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        // Navegadores web
                        val navigatorsBorderColor = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD && focusedButton == DialogButton.NAVIGATORS_OPEN) LocalMetroPalette.current.accent else LocalMetroPalette.current.border
                        Column(
                            modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.NAVIGATORS_OPEN]!!).border(1.dp, navigatorsBorderColor, RectangleShape).padding(16.dp)
                        ) {
                            Text(stringResource(Res.string.setup_navigators_title), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(Res.string.setup_navigators_description), color = LocalMetroPalette.current.muted, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            MetroButton(inputMode = inputMode, text = stringResource(Res.string.setup_navigators_manage), isPrimary = focusedButton == DialogButton.NAVIGATORS_OPEN, isFocused = focusedButton == DialogButton.NAVIGATORS_OPEN, onClick = { showNavigators = true })
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // COLUMNA DERECHA (Rutas)
                    Column(modifier = Modifier.weight(1f)) {

                        // PCSX2 Path
                        Column(modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.PCSX2_BROWSE]!!)) {
                            Text(stringResource(Res.string.setup_path_pcsx2), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextField(
                                    value = pathTextPcsx2, onValueChange = { pathTextPcsx2 = it }, modifier = Modifier.weight(1f).border(1.dp, LocalMetroPalette.current.muted, RectangleShape),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = LocalMetroPalette.current.surface, unfocusedContainerColor = LocalMetroPalette.current.surface, disabledContainerColor = LocalMetroPalette.current.surface, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                MetroButton(inputMode = inputMode, text = stringResource(Res.string.action_browse), isPrimary = focusedButton == DialogButton.PCSX2_BROWSE, isFocused = focusedButton == DialogButton.PCSX2_BROWSE, onClick = { showFolderPickerFor = DialogButton.PCSX2_BROWSE })
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Melon DS Path
                        Column(modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.MELONDS_GAMES_BROWSE]!!)) {
                            Text(stringResource(Res.string.setup_path_melonds), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextField(
                                    value = pathTextMelonGames, onValueChange = { pathTextMelonGames = it }, modifier = Modifier.weight(1f).border(1.dp, LocalMetroPalette.current.muted, RectangleShape),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = LocalMetroPalette.current.surface, unfocusedContainerColor = LocalMetroPalette.current.surface, disabledContainerColor = LocalMetroPalette.current.surface, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                MetroButton(inputMode = inputMode, text = stringResource(Res.string.action_browse), isPrimary = focusedButton == DialogButton.MELONDS_GAMES_BROWSE, isFocused = focusedButton == DialogButton.MELONDS_GAMES_BROWSE, onClick = { showFolderPickerFor = DialogButton.MELONDS_GAMES_BROWSE })
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Dolphin Path
                        Column(modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.DOLPHIN_GAMES_BROWSE]!!)) {
                            Text(stringResource(Res.string.setup_path_dolphin), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextField(
                                    value = pathTextDolphinGames, onValueChange = { pathTextDolphinGames = it }, modifier = Modifier.weight(1f).border(1.dp, LocalMetroPalette.current.muted, RectangleShape),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = LocalMetroPalette.current.surface, unfocusedContainerColor = LocalMetroPalette.current.surface, disabledContainerColor = LocalMetroPalette.current.surface, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                MetroButton(inputMode = inputMode, text = stringResource(Res.string.action_browse), isPrimary = focusedButton == DialogButton.DOLPHIN_GAMES_BROWSE, isFocused = focusedButton == DialogButton.DOLPHIN_GAMES_BROWSE, onClick = { showFolderPickerFor = DialogButton.DOLPHIN_GAMES_BROWSE })
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Azahar Path
                        Column(modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.AZAHAR_GAMES_BROWSE]!!)) {
                            Text(stringResource(Res.string.setup_path_azahar), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextField(
                                    value = pathTextAzaharGames, onValueChange = { pathTextAzaharGames = it }, modifier = Modifier.weight(1f).border(1.dp, LocalMetroPalette.current.muted, RectangleShape),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = LocalMetroPalette.current.surface, unfocusedContainerColor = LocalMetroPalette.current.surface, disabledContainerColor = LocalMetroPalette.current.surface, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                MetroButton(inputMode = inputMode, text = stringResource(Res.string.action_browse), isPrimary = focusedButton == DialogButton.AZAHAR_GAMES_BROWSE, isFocused = focusedButton == DialogButton.AZAHAR_GAMES_BROWSE, onClick = { showFolderPickerFor = DialogButton.AZAHAR_GAMES_BROWSE })
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // DuckStation Path
                        Column(modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[DialogButton.DUCKSTATION_GAMES_BROWSE]!!)) {
                            Text(stringResource(Res.string.setup_path_duckstation), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextField(
                                    value = pathTextDuckStationGames, onValueChange = { pathTextDuckStationGames = it }, modifier = Modifier.weight(1f).border(1.dp, LocalMetroPalette.current.muted, RectangleShape),
                                    colors = TextFieldDefaults.colors(focusedContainerColor = LocalMetroPalette.current.surface, unfocusedContainerColor = LocalMetroPalette.current.surface, disabledContainerColor = LocalMetroPalette.current.surface, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true, textStyle = TextStyle(fontSize = 14.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                MetroButton(inputMode = inputMode, text = stringResource(Res.string.action_browse), isPrimary = focusedButton == DialogButton.DUCKSTATION_GAMES_BROWSE, isFocused = focusedButton == DialogButton.DUCKSTATION_GAMES_BROWSE, onClick = { showFolderPickerFor = DialogButton.DUCKSTATION_GAMES_BROWSE })
                            }
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(requesters[DialogButton.CANCEL]!!)
                        .bringIntoViewRequester(requesters[DialogButton.CONFIRM]!!),
                    horizontalArrangement = Arrangement.End
                ) {
                    MetroButton(inputMode = inputMode, text = stringResource(Res.string.action_cancel), isFocused = focusedButton == DialogButton.CANCEL, onClick = onDismiss)
                    Spacer(modifier = Modifier.width(16.dp))
                    MetroButton(inputMode = inputMode,
                        text = stringResource(Res.string.action_save), isPrimary = true, isFocused = focusedButton == DialogButton.CONFIRM,
                        onClick = {
                            settingsViewModel.savePath("pcsx2", pathTextPcsx2)
                            settingsViewModel.savePath("melonds", pathTextMelonGames)
                            settingsViewModel.savePath("dolphinGames", pathTextDolphinGames)
                            settingsViewModel.savePath("azahar", pathTextAzaharGames)
                            settingsViewModel.savePath("duckstation", pathTextDuckStationGames)
                            onConfirm()
                        }
                    )
                }
            }
        }
    }

    if (showNavigators) {
        NavigatorsSettingsDialog(
            settingsViewModel = settingsViewModel,
            gamepadManager = gamepadManager,
            onDismiss = { showNavigators = false }
        )
    }

    if (showFolderPickerFor != null) {
        val initialPath = when (showFolderPickerFor) {
            DialogButton.PCSX2_BROWSE -> pathTextPcsx2
            DialogButton.MELONDS_GAMES_BROWSE -> pathTextMelonGames
            DialogButton.DOLPHIN_GAMES_BROWSE -> pathTextDolphinGames
            DialogButton.AZAHAR_GAMES_BROWSE -> pathTextAzaharGames
            DialogButton.DUCKSTATION_GAMES_BROWSE -> pathTextDuckStationGames
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
                    DialogButton.DOLPHIN_GAMES_BROWSE -> pathTextDolphinGames = selectedPath
                    DialogButton.AZAHAR_GAMES_BROWSE -> pathTextAzaharGames = selectedPath
                    DialogButton.DUCKSTATION_GAMES_BROWSE -> pathTextDuckStationGames = selectedPath
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
