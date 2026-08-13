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
    LaunchedEffect(gamepadManager, folders) {
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
                                val selected = folders[focusedIndex]
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
                                val selected = folders[focusedIndex]
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
                    .width(600.dp)
                    .fillMaxHeight(0.8f)
                    .background(Color(0xFF0F0F0F), RectangleShape)
                    .border(2.dp, Color.White, RectangleShape)
                    .padding(24.dp)
            ) {
                Text(
                    text = "SELECCIONAR CARPETA",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Ruta actual: ${currentDir.absolutePath}",
                    color = Color(0xFF00FFCC),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Lista de carpetas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF333333), RectangleShape)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        itemsIndexed(folders) { index, folder ->
                            val isFocused = index == focusedIndex
                            val bgColor = if (isFocused) Color(0xFF2A2A2A) else Color.Transparent
                            val textColor = if (isFocused) Color(0xFF00FFCC) else Color.White

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(bgColor)
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (folder.name == "..") "📁 .." else "📁 ${folder.name}",
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controles de ayuda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A/Derecha: Entrar | B/Izq: Volver",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Row {
                        MetroButton(
                            text = "CANCELAR",
                            isFocused = false,
                            onClick = onDismiss
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        MetroButton(
                            text = "START: SELECCIONAR AQUÍ",
                            isPrimary = true,
                            isFocused = true,
                            onClick = { onDirectorySelected(currentDir.absolutePath) }
                        )
                    }
                }
            }
        }
    }
}
