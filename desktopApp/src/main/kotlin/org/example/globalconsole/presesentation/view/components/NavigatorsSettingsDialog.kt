package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import globalconsole.shared.generated.resources.*
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import org.example.globalconsole.presesentation.input.InputMode
import org.example.globalconsole.presesentation.viewModel.settings.SettingsViewModel
import org.jetbrains.compose.resources.stringResource

/**
 * Diálogo de configuración de navegadores web. Lista los navegadores instalados en el
 * sistema; el usuario elige cuáles pasan a ser componentes de la biblioteca
 * (habilitado = visible en la biblioteca).
 *
 * Aislado del diálogo base: tiene su propio colector de eventos del mando
 * (arriba/abajo navega, A alterna, B cierra).
 *
 * @param settingsViewModel Estado y acciones de los navegadores.
 * @param gamepadManager Entrada nativa del mando.
 * @param onDismiss Cierra el diálogo.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
@Composable
fun NavigatorsSettingsDialog(
    settingsViewModel: SettingsViewModel,
    gamepadManager: GamepadManager,
    onDismiss: () -> Unit
) {
    val navigators by settingsViewModel.navigators.collectAsState()
    val inputMode by gamepadManager.inputMode.collectAsState()
    val palette = LocalMetroPalette.current

    var focusedRow by remember { mutableStateOf(0) }
    val closeRow = navigators.size
    val requesters = remember(navigators.size) { List(navigators.size + 1) { BringIntoViewRequester() } }

    LaunchedEffect(Unit) { settingsViewModel.loadNavigators() }
    LaunchedEffect(focusedRow, requesters) { requesters.getOrNull(focusedRow)?.bringIntoView() }

    val currentNavigators by rememberUpdatedState(navigators)
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> when (event.direction) {
                    GamepadEvent.Direction.UP -> focusedRow = (focusedRow - 1).coerceAtLeast(0)
                    GamepadEvent.Direction.DOWN -> focusedRow = (focusedRow + 1).coerceAtMost(currentNavigators.size)
                    GamepadEvent.Direction.LEFT, GamepadEvent.Direction.RIGHT -> {}
                }
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.CONFIRM -> {
                        val item = currentNavigators.getOrNull(focusedRow)
                        if (item == null) onDismiss()
                        else settingsViewModel.setNavigatorEnabled(item.navigator, !item.enabled)
                    }
                    GamepadEvent.Button.OPTIONS -> focusedRow = currentNavigators.size
                    GamepadEvent.Button.BACK -> onDismiss()
                    else -> {}
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
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp).fillMaxWidth(.94f)
                    .fillMaxHeight(0.8f)
                    .metroPanel(palette)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.setup_navigators_title),
                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = stringResource(Res.string.setup_navigators_description),
                    color = palette.muted, fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (navigators.isEmpty()) {
                        Text(stringResource(Res.string.setup_navigators_empty), color = palette.muted, fontSize = 14.sp)
                    }
                    navigators.forEachIndexed { index, item ->
                        val rowFocused = inputMode == InputMode.GAMEPAD && focusedRow == index
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .bringIntoViewRequester(requesters[index])
                                .border(1.dp, if (rowFocused) palette.accent else palette.border, RectangleShape)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = item.navigator.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            MetroButton(
                                inputMode = inputMode,
                                text = stringResource(if (item.enabled) Res.string.status_enabled else Res.string.status_disabled),
                                isPrimary = item.enabled,
                                isFocused = focusedRow == index,
                                onHover = { focusedRow = index },
                                onClick = { settingsViewModel.setNavigatorEnabled(item.navigator, !item.enabled) }
                            )
                        }
                    }
                }

                Text(stringResource(Res.string.settings_close_hint), color = palette.muted, fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().bringIntoViewRequester(requesters[closeRow]),
                    horizontalArrangement = Arrangement.End
                ) {
                    MetroButton(
                        inputMode = inputMode,
                        text = stringResource(Res.string.action_close),
                        isPrimary = true,
                        isFocused = focusedRow == closeRow,
                        onHover = { focusedRow = closeRow },
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}
