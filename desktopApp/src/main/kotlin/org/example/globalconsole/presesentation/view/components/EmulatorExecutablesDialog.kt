package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import globalconsole.shared.generated.resources.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import org.example.globalconsole.settings.domain.EmulatorTarget
import org.jetbrains.compose.resources.stringResource
import java.io.File

/**
 * Edita ejecutables con selección de archivos y guardado explícito, aislando el modal hijo.
 * @param viewModel Acceso a las preferencias mediante casos de uso.
 * @param gamepadManager Entrada nativa del mando.
 * @param onDismiss Cierra los ajustes.
 * @return Unit.
 * @throws Nothing Los errores de persistencia se muestran en el diálogo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
@Composable
fun EmulatorExecutablesDialog(viewModel: SettingsViewModel, gamepadManager: GamepadManager, onDismiss: () -> Unit) {
    val targets = EmulatorTarget.entries
    val labels = listOf("PCSX2", "melonDS", "Dolphin", "Azahar", "DuckStation", "RPCS3")
    val palette = LocalMetroPalette.current
    val inputMode by gamepadManager.inputMode.collectAsState()
    var paths by remember { mutableStateOf<Map<EmulatorTarget, String>>(emptyMap()) }
    var picker by remember { mutableStateOf<EmulatorTarget?>(null) }
    var focused by remember { mutableStateOf(0) }
    var busy by remember { mutableStateOf(true) }
    var loaded by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val requesters = remember { List(targets.size + 2) { BringIntoViewRequester() } }
    val close by rememberUpdatedState<() -> Unit> { if (!busy) onDismiss() }
    val save by rememberUpdatedState<() -> Unit> {
        if (!busy && loaded) {
            busy = true
            failed = false
            scope.launch {
                try {
                    viewModel.saveExecutables(paths)
                    onDismiss()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    failed = true
                } finally {
                    busy = false
                }
            }
        }
    }
    LaunchedEffect(viewModel) {
        try {
            paths = viewModel.loadExecutables()
            loaded = true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            failed = true
        } finally {
            busy = false
        }
    }
    LaunchedEffect(focused) { requesters[focused].bringIntoView() }
    LaunchedEffect(gamepadManager, picker, busy, loaded) {
        if (picker != null || busy) return@LaunchedEffect
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> when (event.direction) {
                    GamepadEvent.Direction.UP -> focused = (focused - 1).coerceAtLeast(0)
                    GamepadEvent.Direction.DOWN -> focused = (focused + 1).coerceAtMost(targets.size + 1)
                    GamepadEvent.Direction.LEFT -> if (focused >= targets.size) focused = targets.size
                    GamepadEvent.Direction.RIGHT -> if (focused >= targets.size) focused = targets.size + 1
                }
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.OPTIONS -> focused = targets.size + 1
                    GamepadEvent.Button.BACK -> close()
                    GamepadEvent.Button.DELETE -> if (loaded && focused in targets.indices) paths = paths + (targets[focused] to "")
                    GamepadEvent.Button.CONFIRM -> when (focused) {
                        targets.size -> close()
                        targets.size + 1 -> save()
                        else -> if (loaded) picker = targets[focused]
                    }
                    else -> Unit
                }
            }
        }
    }
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(palette.background.copy(alpha = .92f)), contentAlignment = Alignment.Center) {
            Column(Modifier.widthIn(max = 900.dp).fillMaxWidth(.94f).fillMaxHeight(.94f).metroPanel(palette).padding(24.dp)) {
                Text(stringResource(Res.string.executable_settings_title), color = palette.text, fontSize = 28.sp)
                Text(stringResource(Res.string.executable_settings_description), color = palette.muted, fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    targets.forEachIndexed { index, target ->
                        Column(Modifier.fillMaxWidth().bringIntoViewRequester(requesters[index]).background(palette.surface).padding(14.dp)) {
                            Text(labels[index], color = palette.text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(paths[target]?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.executable_automatic),
                                color = palette.muted, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(vertical = 8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetroButton(stringResource(Res.string.action_browse), {
                                    if (!busy && loaded) picker = target
                                }, inputMode = inputMode, isFocused = focused == index, onHover = { focused = index })
                                MetroButton(stringResource(Res.string.executable_automatic), {
                                    if (!busy && loaded) paths = paths + (target to "")
                                }, inputMode = inputMode, onHover = { focused = index })
                            }
                        }
                    }
                }
                Text(stringResource(if (failed) Res.string.executable_settings_error else if (busy) Res.string.executable_settings_busy else Res.string.executable_settings_controls),
                    color = if (failed) palette.accent else palette.muted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)) {
                    MetroButton(stringResource(Res.string.action_close), close,
                        Modifier.bringIntoViewRequester(requesters[targets.size]), inputMode = inputMode,
                        isFocused = focused == targets.size, onHover = { focused = targets.size })
                    MetroButton(stringResource(Res.string.action_save), save,
                        Modifier.bringIntoViewRequester(requesters[targets.size + 1]), inputMode = inputMode,
                        isPrimary = true, isFocused = focused == targets.size + 1, onHover = { focused = targets.size + 1 })
                }
            }
        }
    }
    picker?.let { target ->
        val current = paths[target].orEmpty()
        val initial = File(current).takeIf { current.isNotBlank() && it.isFile }?.parent ?: System.getProperty("user.home")
        GamepadFolderPickerDialog(gamepadManager, initialPath = initial, selectFile = true,
            onDirectorySelected = { paths = paths + (target to it); picker = null }, onDismiss = { picker = null })
    }
}
