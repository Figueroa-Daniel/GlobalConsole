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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.globalconsole.input.domain.usecase.KeyboardAction
import org.example.globalconsole.input.domain.usecase.KeyboardInputUseCase
import org.example.globalconsole.input.domain.usecase.KeyboardState
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager

/**
 * Teclado español con búsqueda reactiva, números y foco consistente entre capas.
 * @param gamepadManager Entrada nativa del mando.
 * @param initialText Texto inicial de búsqueda.
 * @param onTextChanged Publica cada cambio de texto.
 * @param onConfirm Confirma con Intro o START.
 * @param onDismiss Cierra conservando la búsqueda reactiva.
 * @return Unit.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@Composable
fun GamepadOSK(
    gamepadManager: GamepadManager,
    initialText: String = "",
    onTextChanged: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val useCase = remember { KeyboardInputUseCase() }
    var state by remember { mutableStateOf(KeyboardState(text = initialText, row = 1)) }
    val inputMode by gamepadManager.inputMode.collectAsState()
    val palette = LocalMetroPalette.current
    val requesters = remember { List(5) { BringIntoViewRequester() } }
    val confirm by rememberUpdatedState(onConfirm)
    val changed by rememberUpdatedState(onTextChanged)
    val dismiss by rememberUpdatedState(onDismiss)
    val act: (KeyboardAction) -> Unit = { action ->
        val previous = state.text
        state = useCase(state, action)
        if (previous != state.text) changed(state.text)
        if (state.confirmed) confirm(state.text)
    }
    val currentAct by rememberUpdatedState(act)
    LaunchedEffect(state.row) { requesters[state.row].bringIntoView() }
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> currentAct(when (event.direction) {
                    GamepadEvent.Direction.UP -> KeyboardAction.UP
                    GamepadEvent.Direction.DOWN -> KeyboardAction.DOWN
                    GamepadEvent.Direction.LEFT -> KeyboardAction.LEFT
                    GamepadEvent.Direction.RIGHT -> KeyboardAction.RIGHT
                })
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.CONFIRM -> currentAct(KeyboardAction.PRESS)
                    GamepadEvent.Button.DELETE -> currentAct(KeyboardAction.DELETE)
                    GamepadEvent.Button.OPTIONS -> currentAct(KeyboardAction.SHIFT)
                    GamepadEvent.Button.MENU -> currentAct(KeyboardAction.CONFIRM)
                    GamepadEvent.Button.BACK -> dismiss()
                    else -> Unit
                }
            }
        }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(palette.background.copy(alpha = .8f)).padding(16.dp), contentAlignment = Alignment.BottomCenter) {
            Column(Modifier.widthIn(max = 1100.dp).fillMaxWidth().heightIn(max = 520.dp).fillMaxHeight(.92f)
                .metroPanel(palette).padding(20.dp)) {
                Text("BUSCAR EN TU BIBLIOTECA", color = palette.accent, fontSize = 14.sp, letterSpacing = 2.sp)
                Text(if (state.text.isEmpty()) "Escribe para buscar…" else state.text + "│",
                    color = if (state.text.isEmpty()) palette.muted else palette.text,
                    fontSize = 24.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp).background(palette.raised).padding(12.dp))
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    useCase.layout(state).forEachIndexed { rowIndex, row ->
                        Row(Modifier.fillMaxWidth().bringIntoViewRequester(requesters[rowIndex]), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEachIndexed { colIndex, key ->
                                MetroButton(
                                    text = if (key == "↵") "BUSCAR ↵" else key,
                                    onClick = {
                                        state = state.copy(row = rowIndex, column = colIndex,
                                            preferredColumn = if (rowIndex == 4) colIndex * 4 + 1 else colIndex)
                                        act(KeyboardAction.PRESS)
                                    },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                    isFocused = state.row == rowIndex && state.column == colIndex,
                                    isPrimary = key == "⇧" && state.upperCase,
                                    inputMode = inputMode,
                                    onHover = { state = state.copy(row = rowIndex, column = colIndex, preferredColumn = if (rowIndex == 4) colIndex * 4 + 1 else colIndex) }
                                )
                            }
                        }
                    }
                }
                Text("A Escribir   ·   X Borrar   ·   Y Mayúsculas   ·   START Buscar   ·   B Cerrar",
                    color = palette.muted, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
