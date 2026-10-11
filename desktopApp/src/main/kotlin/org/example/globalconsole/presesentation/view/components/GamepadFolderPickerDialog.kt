package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.stringResource
import globalconsole.shared.generated.resources.*
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import java.io.File

/**
 * Diálogo nativo en Compose para seleccionar carpetas usando exclusivamente el Gamepad.
 *
 * Controles D-Pad:
 * - UP / DOWN: Navegar por la lista de carpetas.
 * - RIGHT / A: Entrar a la carpeta seleccionada.
 * - LEFT / B: Volver al directorio padre.
 * - START: Confirmar y seleccionar el directorio actual.
 *
 * @param gamepadManager Gestor de gamepad.
 * @param initialPath Ruta inicial (por defecto el Home del usuario).
 * @param onDirectorySelected Callback cuando el usuario confirma la carpeta actual.
 * @param onDismiss Callback para cancelar la operación.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
@Composable
fun GamepadFolderPickerDialog(
    gamepadManager: GamepadManager,
    initialPath: String = System.getProperty("user.home"),
    onDirectorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentDir by remember { mutableStateOf(File(initialPath).takeIf { it.exists() } ?: File("/")) }
    var folders by remember { mutableStateOf(emptyList<File>()) }
    var focusedIndex by remember { mutableStateOf(0) }
    val inputMode by gamepadManager.inputMode.collectAsState()
    val listState = rememberLazyListState()

    // Cargar carpetas cuando cambia el directorio actual
    LaunchedEffect(currentDir) {
        val listedFiles = currentDir.listFiles()?.filter { it.isDirectory && !it.isHidden }?.sortedBy { it.name.lowercase() } ?: emptyList()
        folders = if (currentDir.parentFile != null) {
            listOf(File("..")) + listedFiles
        } else {
            listedFiles
        }
        focusedIndex = 0
        listState.scrollToItem(0)
    }

    // Asegurar que el elemento enfocado sea visible
    LaunchedEffect(focusedIndex) {
        if (folders.isNotEmpty()) {
            listState.animateScrollToItem(focusedIndex)
        }
    }

    // Navegación por Gamepad
    LaunchedEffect(gamepadManager, folders, currentDir) {
        gamepadManager.events.collect { event ->
            when (event) {
                is GamepadEvent.DirectionPressed -> {
                    when (event.direction) {
                        GamepadEvent.Direction.UP -> {
                            if (focusedIndex > 0) focusedIndex--
                        }
                        GamepadEvent.Direction.DOWN -> {
                            if (focusedIndex < folders.size - 1) focusedIndex++
                        }
                        GamepadEvent.Direction.LEFT -> {
                            currentDir.parentFile?.let { currentDir = it }
                        }
                        GamepadEvent.Direction.RIGHT -> {
                            if (folders.isNotEmpty()) {
                                val selected = folders.getOrNull(focusedIndex) ?: return@collect
                                if (selected.name == "..") {
                                    currentDir.parentFile?.let { currentDir = it }
                                } else {
                                    currentDir = selected
                                }
                            }
                        }
                    }
                }
                is GamepadEvent.ButtonPressed -> {
                    when (event.button) {
                        GamepadEvent.Button.CONFIRM -> { // Entrar en la carpeta
                            if (folders.isNotEmpty()) {
                                val selected = folders.getOrNull(focusedIndex) ?: return@collect
                                if (selected.name == "..") {
                                    currentDir.parentFile?.let { currentDir = it }
                                } else {
                                    currentDir = selected
                                }
                            }
                        }
                        GamepadEvent.Button.BACK -> { // Subir de nivel o salir
                            if (currentDir.parentFile != null) {
                                currentDir = currentDir.parentFile!!
                            } else {
                                onDismiss()
                            }
                        }
                        GamepadEvent.Button.DELETE -> onDismiss()
                        GamepadEvent.Button.MENU -> { // Seleccionar la carpeta ACTUAL
                            onDirectorySelected(currentDir.absolutePath)
                        }
                        else -> {}
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
                    .widthIn(max = 860.dp).fillMaxWidth(.94f)
                    .fillMaxHeight(0.8f)
                    .metroPanel(LocalMetroPalette.current)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.folder_picker_title),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(Res.string.folder_current_path, currentDir.absolutePath),
                    color = LocalMetroPalette.current.accent,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Lista de carpetas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, LocalMetroPalette.current.border, RectangleShape)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        itemsIndexed(folders) { index, folder ->
                            MetroButton(
                                text = if (folder.name == "..") stringResource(Res.string.folder_parent) else "▸  ${folder.name}",
                                onClick = {
                                    focusedIndex = index
                                    if (folder.name == "..") currentDir.parentFile?.let { currentDir = it }
                                    else currentDir = folder
                                },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                isFocused = index == focusedIndex,
                                inputMode = inputMode,
                                onHover = { focusedIndex = index }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controles de ayuda
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.folder_controls),
                        color = LocalMetroPalette.current.muted,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Row {
                        MetroButton(
                            text = stringResource(Res.string.action_cancel),
                            inputMode = inputMode,
                            isFocused = false,
                            onClick = onDismiss
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        MetroButton(
                            text = stringResource(Res.string.folder_select_current),
                            inputMode = inputMode,
                            isPrimary = true,
                            isFocused = false,
                            onClick = { onDirectorySelected(currentDir.absolutePath) }
                        )
                    }
                }
            }
        }
    }
}
