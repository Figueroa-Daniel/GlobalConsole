package org.example.globalconsole.localApps.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import globalconsole.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.view.components.*
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import java.io.File

/**
 * Gestiona aplicaciones instaladas con foco propio, scroll y aislamiento del diálogo base.
 * @param settingsViewModel Estado y operaciones persistentes.
 * @param gamepadManager Entrada nativa del mando.
 * @param onDismiss Regresa a la configuración después de terminar los guardados.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
@Composable
fun LocalAppsSettingsDialog(
    settingsViewModel: SettingsViewModel,
    gamepadManager: GamepadManager,
    onDismiss: () -> Unit
) {
    val state by settingsViewModel.localApps.collectAsState()
    val inputMode by gamepadManager.inputMode.collectAsState()
    val palette = LocalMetroPalette.current
    val scroll = rememberLazyListState()
    var focusedRow by remember { mutableStateOf(0) }
    val current by rememberUpdatedState(state)
    val dismiss by rememberUpdatedState(onDismiss)
    val close: () -> Unit = { if (!state.busy) onDismiss() }

    LaunchedEffect(settingsViewModel) { settingsViewModel.loadLocalApps() }
    LaunchedEffect(state.items.size) { focusedRow = focusedRow.coerceIn(0, state.items.size + 1) }
    LaunchedEffect(focusedRow, state.items.size) {
        if (focusedRow in state.items.indices) scroll.animateScrollToItem(focusedRow)
    }
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> when (event.direction) {
                    GamepadEvent.Direction.UP -> focusedRow = (focusedRow - 1).coerceAtLeast(0)
                    GamepadEvent.Direction.DOWN -> focusedRow = (focusedRow + 1).coerceAtMost(current.items.size + 1)
                    else -> Unit
                }
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.BACK -> if (!current.busy) dismiss()
                    GamepadEvent.Button.CONFIRM -> if (!current.busy) {
                        val item = current.items.getOrNull(focusedRow)
                        when {
                            item != null -> settingsViewModel.setLocalAppEnabled(item.app.id, !item.enabled)
                            focusedRow == current.items.size -> settingsViewModel.loadLocalApps()
                            else -> dismiss()
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(palette.background.copy(alpha = .9f)), contentAlignment = Alignment.Center) {
            Column(Modifier.widthIn(max = 960.dp).fillMaxWidth(.94f).fillMaxHeight(.86f).metroPanel(palette).padding(24.dp)) {
                Text(stringResource(Res.string.local_apps_title), color = palette.text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(Res.string.local_apps_description), color = palette.muted, modifier = Modifier.padding(vertical = 12.dp))
                if (state.busy) Text(stringResource(Res.string.local_apps_busy), color = palette.accent)
                state.error?.let { Text(it, color = palette.accent, modifier = Modifier.padding(bottom = 8.dp)) }
                if (!state.busy && state.items.isEmpty() && state.error == null) {
                    Text(stringResource(Res.string.local_apps_empty), color = palette.muted)
                }
                LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = scroll, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(state.items, key = { _, item -> item.app.id }) { index, item ->
                        Row(Modifier.fillMaxWidth().background(palette.raised).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(64.dp).background(palette.surface), contentAlignment = Alignment.Center) {
                                Text("APP", color = palette.accent, fontWeight = FontWeight.Bold)
                                item.app.image?.let {
                                    AsyncImage(model = File(it), contentDescription = item.app.name, modifier = Modifier.fillMaxSize())
                                }
                            }
                            Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                                Text(item.app.name, color = palette.text, fontSize = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                            MetroButton(
                                text = stringResource(if (item.enabled) Res.string.status_enabled else Res.string.status_disabled),
                                inputMode = inputMode, isFocused = focusedRow == index, isPrimary = item.enabled,
                                onHover = { focusedRow = index },
                                onClick = { if (!state.busy) settingsViewModel.setLocalAppEnabled(item.app.id, !item.enabled) }
                            )
                        }
                    }
                }
                Text(stringResource(Res.string.local_apps_controls), color = palette.muted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)) {
                    MetroButton(
                        text = stringResource(Res.string.local_apps_refresh), inputMode = inputMode,
                        isFocused = focusedRow == state.items.size, onHover = { focusedRow = state.items.size },
                        onClick = { settingsViewModel.loadLocalApps() }
                    )
                    MetroButton(
                        text = stringResource(Res.string.action_close), inputMode = inputMode,
                        isFocused = focusedRow == state.items.size + 1, onHover = { focusedRow = state.items.size + 1 }, onClick = close
                    )
                }
            }
        }
    }
}
