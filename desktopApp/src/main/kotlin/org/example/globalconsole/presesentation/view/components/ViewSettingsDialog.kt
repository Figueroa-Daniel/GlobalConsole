package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.viewModel.home.GroupingMode
import org.example.globalconsole.presesentation.viewModel.home.HomeViewModel
import org.example.globalconsole.presesentation.viewModel.home.LauncherPosition
import org.example.globalconsole.settings.domain.AppearanceSettings

/**
 * Editor de vista y apariencia con filas navegables y vista previa en toda la interfaz.
 * @param homeViewModel Estado y persistencia de preferencias.
 * @param gamepadManager Entrada nativa del mando.
 * @param onDismiss Cierra el diálogo.
 * @return Unit.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@Composable
fun ViewSettingsDialog(homeViewModel: HomeViewModel, gamepadManager: GamepadManager, onDismiss: () -> Unit) {
    val grouping by homeViewModel.groupingMode.collectAsState()
    val position by homeViewModel.launcherPosition.collectAsState()
    val appearance by homeViewModel.appearance.collectAsState()
    val error by homeViewModel.appearanceError.collectAsState()
    val inputMode by gamepadManager.inputMode.collectAsState()
    val palette = LocalMetroPalette.current
    var focusedRow by remember { mutableStateOf(0) }
    val requesters = remember { List(5) { BringIntoViewRequester() } }
    val colorNames = listOf("Cian", "Azul", "Violeta", "Rosa", "Ámbar", "Verde")
    val labels = listOf("Agrupación", "Emuladores", "Color del brillo", "Intensidad del brillo")
    val values = listOf(
        when (grouping) { GroupingMode.NONE -> "Sin agrupar"; GroupingMode.PLATFORM -> "Por plataforma"; GroupingMode.CONSOLE_SERIES -> "Por familia" },
        if (position == LauncherPosition.TOP) "Arriba del todo" else "Junto a los juegos",
        colorNames[AppearanceSettings.COLORS.indexOf(appearance.accentColor).coerceAtLeast(0)],
        if (appearance.glowIntensity == 0) "Sin halo · foco visible" else "${appearance.glowIntensity}%"
    )
    val change by rememberUpdatedState<(Int, Int) -> Unit> { row, delta ->
        when (row) {
            0 -> homeViewModel.setGroupingMode(GroupingMode.entries[(grouping.ordinal + delta + GroupingMode.entries.size) % GroupingMode.entries.size])
            1 -> homeViewModel.setLauncherPosition(if (position == LauncherPosition.TOP) LauncherPosition.INLINE else LauncherPosition.TOP)
            2 -> {
                val colors = AppearanceSettings.COLORS
                homeViewModel.setAppearance(appearance.copy(accentColor = colors[(colors.indexOf(appearance.accentColor) + delta + colors.size) % colors.size]))
            }
            3 -> homeViewModel.setAppearance(appearance.copy(glowIntensity = (appearance.glowIntensity + delta * 5).coerceIn(0, 100)))
        }
    }
    val dismiss by rememberUpdatedState(onDismiss)
    LaunchedEffect(focusedRow) { requesters[focusedRow].bringIntoView() }
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> when (event.direction) {
                    GamepadEvent.Direction.UP -> focusedRow = (focusedRow - 1).coerceAtLeast(0)
                    GamepadEvent.Direction.DOWN -> focusedRow = (focusedRow + 1).coerceAtMost(4)
                    GamepadEvent.Direction.LEFT -> change(focusedRow, -1)
                    GamepadEvent.Direction.RIGHT -> change(focusedRow, 1)
                }
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.BACK -> dismiss()
                    GamepadEvent.Button.CONFIRM -> if (focusedRow == 4) dismiss() else change(focusedRow, 1)
                    else -> Unit
                }
            }
        }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(palette.background.copy(alpha = .92f)), contentAlignment = Alignment.Center) {
            Column(Modifier.widthIn(max = 760.dp).fillMaxWidth(.94f).fillMaxHeight(.92f)
                .background(palette.surface).border(1.dp, palette.accent.copy(alpha = .6f)).padding(24.dp)) {
                Text("TU BIBLIOTECA, A TU ESTILO", color = palette.accent, fontSize = 13.sp, letterSpacing = 2.sp)
                Text("Vista y apariencia", color = palette.text, fontSize = 28.sp, fontWeight = FontWeight.Light)
                Text("Fondo oscuro · cambios guardados automáticamente", color = palette.muted, fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    labels.forEachIndexed { index, label ->
                        Column(Modifier.fillMaxWidth().bringIntoViewRequester(requesters[index])
                            .background(palette.raised).metroFocus(focusedRow == index && inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD, palette).padding(14.dp)) {
                            Text(label.uppercase(), color = palette.muted, fontSize = 12.sp, letterSpacing = 1.sp)
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetroButton("‹", { focusedRow = index; change(index, -1) }, inputMode = inputMode)
                                Text(values[index], color = palette.text, fontSize = 18.sp, modifier = Modifier.weight(1f))
                                MetroButton("›", { focusedRow = index; change(index, 1) }, inputMode = inputMode)
                            }
                        }
                    }
                }
                Text(error ?: "↑ ↓ Elegir   ·   ← → Ajustar   ·   A Siguiente valor   ·   B Cerrar",
                    color = if (error != null) androidx.compose.ui.graphics.Color(0xFFFF9A9A) else palette.muted,
                    fontSize = 13.sp, modifier = Modifier.padding(vertical = 16.dp))
                MetroButton("LISTO", onDismiss, Modifier.fillMaxWidth().bringIntoViewRequester(requesters[4]),
                    isFocused = focusedRow == 4, inputMode = inputMode, isPrimary = true)
            }
        }
    }
}
