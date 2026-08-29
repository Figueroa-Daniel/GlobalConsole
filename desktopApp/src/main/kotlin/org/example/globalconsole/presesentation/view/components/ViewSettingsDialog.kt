package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.collectLatest
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.input.InputMode
import org.example.globalconsole.presesentation.viewModel.home.GroupingMode
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.viewModel.home.LauncherPosition

enum class ViewSettingsFocus {
    GROUPING_NONE,
    GROUPING_PLATFORM,
    GROUPING_SERIES,
    LAUNCHER_INLINE,
    LAUNCHER_TOP,
    CLOSE
}

@Composable
fun ViewSettingsDialog(
    homeViewModel: HomeViewModel,
    gamepadManager: GamepadManager,
    onDismiss: () -> Unit
) {
    val currentGrouping by homeViewModel.groupingMode.collectAsState()
    val currentLauncherPos by homeViewModel.launcherPosition.collectAsState()
    val inputMode by gamepadManager.inputMode.collectAsState()

    var focusedElement by remember { mutableStateOf(ViewSettingsFocus.GROUPING_NONE) }

    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collectLatest { event ->
            if (event is GamepadEvent.DirectionPressed) {
                focusedElement = when (focusedElement) {
                    ViewSettingsFocus.GROUPING_NONE -> {
                        if (event.direction == GamepadEvent.Direction.DOWN) ViewSettingsFocus.GROUPING_PLATFORM
                        else if (event.direction == GamepadEvent.Direction.RIGHT) ViewSettingsFocus.LAUNCHER_INLINE
                        else focusedElement
                    }
                    ViewSettingsFocus.GROUPING_PLATFORM -> {
                        if (event.direction == GamepadEvent.Direction.UP) ViewSettingsFocus.GROUPING_NONE
                        else if (event.direction == GamepadEvent.Direction.DOWN) ViewSettingsFocus.GROUPING_SERIES
                        else if (event.direction == GamepadEvent.Direction.RIGHT) ViewSettingsFocus.LAUNCHER_INLINE
                        else focusedElement
                    }
                    ViewSettingsFocus.GROUPING_SERIES -> {
                        if (event.direction == GamepadEvent.Direction.UP) ViewSettingsFocus.GROUPING_PLATFORM
                        else if (event.direction == GamepadEvent.Direction.DOWN) ViewSettingsFocus.CLOSE
                        else if (event.direction == GamepadEvent.Direction.RIGHT) ViewSettingsFocus.LAUNCHER_TOP
                        else focusedElement
                    }
                    ViewSettingsFocus.LAUNCHER_INLINE -> {
                        if (event.direction == GamepadEvent.Direction.DOWN) ViewSettingsFocus.LAUNCHER_TOP
                        else if (event.direction == GamepadEvent.Direction.LEFT) ViewSettingsFocus.GROUPING_NONE
                        else focusedElement
                    }
                    ViewSettingsFocus.LAUNCHER_TOP -> {
                        if (event.direction == GamepadEvent.Direction.UP) ViewSettingsFocus.LAUNCHER_INLINE
                        else if (event.direction == GamepadEvent.Direction.DOWN) ViewSettingsFocus.CLOSE
                        else if (event.direction == GamepadEvent.Direction.LEFT) ViewSettingsFocus.GROUPING_SERIES
                        else focusedElement
                    }
                    ViewSettingsFocus.CLOSE -> {
                        if (event.direction == GamepadEvent.Direction.UP) ViewSettingsFocus.GROUPING_SERIES
                        else focusedElement
                    }
                }
            } else if (event is GamepadEvent.ButtonPressed) {
                if (event.button == GamepadEvent.Button.CONFIRM) {
                    when (focusedElement) {
                        ViewSettingsFocus.GROUPING_NONE -> homeViewModel.setGroupingMode(GroupingMode.NONE)
                        ViewSettingsFocus.GROUPING_PLATFORM -> homeViewModel.setGroupingMode(GroupingMode.PLATFORM)
                        ViewSettingsFocus.GROUPING_SERIES -> homeViewModel.setGroupingMode(GroupingMode.CONSOLE_SERIES)
                        ViewSettingsFocus.LAUNCHER_INLINE -> homeViewModel.setLauncherPosition(LauncherPosition.INLINE)
                        ViewSettingsFocus.LAUNCHER_TOP -> homeViewModel.setLauncherPosition(LauncherPosition.TOP)
                        ViewSettingsFocus.CLOSE -> onDismiss()
                    }
                } else if (event.button == GamepadEvent.Button.BACK) {
                    onDismiss()
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
                    .width(600.dp)
                    .background(Color(0xFF161616))
                    .border(2.dp, Color(0xFF333333), RectangleShape)
                    .padding(32.dp)
            ) {
                Text(
                    text = "OPCIONES DE VISTA",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AGRUPACIÓN", color = Color(0xFF00FFCC), fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        MetroButton(
                            text = "SIN AGRUPAR",
                            isFocused = focusedElement == ViewSettingsFocus.GROUPING_NONE,
                            inputMode = inputMode,
                            onClick = { homeViewModel.setGroupingMode(GroupingMode.NONE) },
                            isPrimary = currentGrouping == GroupingMode.NONE
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MetroButton(
                            text = "POR PLATAFORMA",
                            isFocused = focusedElement == ViewSettingsFocus.GROUPING_PLATFORM,
                            inputMode = inputMode,
                            onClick = { homeViewModel.setGroupingMode(GroupingMode.PLATFORM) },
                            isPrimary = currentGrouping == GroupingMode.PLATFORM
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MetroButton(
                            text = "POR FAMILIA",
                            isFocused = focusedElement == ViewSettingsFocus.GROUPING_SERIES,
                            inputMode = inputMode,
                            onClick = { homeViewModel.setGroupingMode(GroupingMode.CONSOLE_SERIES) },
                            isPrimary = currentGrouping == GroupingMode.CONSOLE_SERIES
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("EMULADORES", color = Color(0xFF00FFCC), fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        MetroButton(
                            text = "JUNTO A JUEGOS",
                            isFocused = focusedElement == ViewSettingsFocus.LAUNCHER_INLINE,
                            inputMode = inputMode,
                            onClick = { homeViewModel.setLauncherPosition(LauncherPosition.INLINE) },
                            isPrimary = currentLauncherPos == LauncherPosition.INLINE
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MetroButton(
                            text = "ARRIBA DEL TODO",
                            isFocused = focusedElement == ViewSettingsFocus.LAUNCHER_TOP,
                            inputMode = inputMode,
                            onClick = { homeViewModel.setLauncherPosition(LauncherPosition.TOP) },
                            isPrimary = currentLauncherPos == LauncherPosition.TOP
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    MetroButton(
                        text = "CERRAR",
                        isFocused = focusedElement == ViewSettingsFocus.CLOSE,
                        inputMode = inputMode,
                        onClick = onDismiss,
                        isPrimary = true
                    )
                }
            }
        }
    }
}
