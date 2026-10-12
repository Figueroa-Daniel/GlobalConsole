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
import org.jetbrains.compose.resources.stringResource
import globalconsole.shared.generated.resources.*
import org.example.globalconsole.input.domain.usecase.KeyboardAction
import org.example.globalconsole.input.domain.usecase.KeyboardInputUseCase
import org.example.globalconsole.input.domain.usecase.KeyboardState
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager

/**
 * Teclado español con búsqueda reactiva, números y foco consistente entre capas.
 * Envuelve [GamepadOSKContent] en un diálogo modal para la búsqueda en la biblioteca.
 *
 * @param gamepadManager Entrada nativa del mando.
 * @param initialText Texto inicial de búsqueda.
 * @param onTextChanged Publica cada cambio de texto.
 * @param onConfirm Confirma con Intro o START.
 * @param onDismiss Cierra conservando la búsqueda reactiva.
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
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        GamepadOSKContent(
            gamepadManager = gamepadManager,
            initialText = initialText,
            onTextChanged = onTextChanged,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

/**
 * Teclado en pantalla flotante (Overlay) diseñado para colocarse en una ventana superior
 * sobre navegadores web y procesos externos suspendidos.
 *
 * @param gamepadManager Entrada nativa del mando.
 * @param onConfirm Callback invocado al pulsar Intro o START para inyectar el texto en el navegador.
 * @param onDismiss Callback invocado para cerrar el overlay.
 * @param onExternalDelete Callback invocado al borrar con el búfer vacío, para borrar en el campo externo.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
@Composable
fun OskOverlay(
    gamepadManager: GamepadManager,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    onExternalDelete: () -> Unit = {}
) {
    GamepadOSKContent(
        gamepadManager = gamepadManager,
        useOverlayEvents = true,
        onExternalDelete = onExternalDelete,
        initialText = "",
        title = stringResource(Res.string.osk_overlay_title),
        placeholder = stringResource(Res.string.osk_overlay_placeholder),
        submitLabel = stringResource(Res.string.osk_overlay_submit),
        controlsLabel = stringResource(Res.string.osk_overlay_controls),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

/**
 * Contenido principal y lógica de interacción del teclado en pantalla (OSK).
 * Reutilizable tanto en el diálogo de búsqueda de biblioteca como en la ventana flotante del navegador.
 *
 * @param gamepadManager Entrada nativa del mando.
 * @param initialText Texto inicial.
 * @param title Título superior del teclado (opcional).
 * @param placeholder Texto cuando no hay nada escrito (opcional).
 * @param submitLabel Etiqueta de la tecla Intro/Confirmar (opcional).
 * @param controlsLabel Ayuda de controles en la parte inferior (opcional).
 * @param onTextChanged Callback reactivo ante cada cambio de texto.
 * @param onConfirm Callback invocado al confirmar con Intro o botón START.
 * @param onDismiss Callback invocado al cancelar o pulsar botón B.
 * @param useOverlayEvents Si es true escucha [GamepadManager.oskEvents] (ventana flotante) en lugar de los eventos generales.
 * @param onExternalDelete Callback invocado al borrar con el búfer vacío.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
@Composable
fun GamepadOSKContent(
    gamepadManager: GamepadManager,
    initialText: String = "",
    title: String? = null,
    placeholder: String? = null,
    submitLabel: String? = null,
    controlsLabel: String? = null,
    onTextChanged: (String) -> Unit = {},
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    useOverlayEvents: Boolean = false,
    onExternalDelete: () -> Unit = {}
) {
    val useCase = remember { KeyboardInputUseCase() }
    var state by remember { mutableStateOf(KeyboardState(text = initialText, row = 1)) }
    val inputMode by gamepadManager.inputMode.collectAsState()
    val palette = LocalMetroPalette.current
    val requesters = remember { List(5) { BringIntoViewRequester() } }
    val confirm by rememberUpdatedState(onConfirm)
    val changed by rememberUpdatedState(onTextChanged)
    val dismiss by rememberUpdatedState(onDismiss)
    val externalDelete by rememberUpdatedState(onExternalDelete)
    val act: (KeyboardAction) -> Unit = { action ->
        val previous = state.text
        val previousDeletes = state.externalDeletes
        state = useCase(state, action)
        if (previous != state.text) changed(state.text)
        if (state.externalDeletes != previousDeletes) externalDelete()
        if (state.confirmed) confirm(state.text)
    }
    val currentAct by rememberUpdatedState(act)
    LaunchedEffect(state.row) { requesters[state.row].bringIntoView() }
    LaunchedEffect(gamepadManager, useOverlayEvents) {
        val source = if (useOverlayEvents) gamepadManager.oskEvents else gamepadManager.events
        source.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> currentAct(when (event.direction) {
                    GamepadEvent.Direction.UP -> KeyboardAction.UP
                    GamepadEvent.Direction.DOWN -> KeyboardAction.DOWN
                    GamepadEvent.Direction.LEFT -> KeyboardAction.LEFT
                    GamepadEvent.Direction.RIGHT -> KeyboardAction.RIGHT
                })
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.TEXT_LEFT -> currentAct(KeyboardAction.TEXT_LEFT)
                    GamepadEvent.Button.TEXT_RIGHT -> currentAct(KeyboardAction.TEXT_RIGHT)
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
    Box(Modifier.fillMaxSize().background(palette.background.copy(alpha = .85f)).padding(16.dp), contentAlignment = Alignment.BottomCenter) {
        Column(Modifier.widthIn(max = 1100.dp).fillMaxWidth().heightIn(max = 520.dp).fillMaxHeight(.92f)
            .metroPanel(palette).padding(20.dp)) {
            Text(title ?: stringResource(Res.string.osk_title), color = palette.accent, fontSize = 14.sp, letterSpacing = 2.sp)
            Text(
                if (state.text.isEmpty()) placeholder ?: stringResource(Res.string.osk_placeholder)
                else state.text.substring(0, state.textCursor) + "│" + state.text.substring(state.textCursor),
                color = if (state.text.isEmpty()) palette.muted else palette.text,
                fontSize = 24.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp).background(palette.raised).padding(12.dp))
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                useCase.layout(state).forEachIndexed { rowIndex, row ->
                    Row(Modifier.fillMaxWidth().bringIntoViewRequester(requesters[rowIndex]), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEachIndexed { colIndex, key ->
                            MetroButton(
                                text = if (key == "↵") (submitLabel ?: stringResource(Res.string.osk_submit)) else key,
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
            Text(controlsLabel ?: stringResource(Res.string.osk_controls),
                color = palette.muted, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

