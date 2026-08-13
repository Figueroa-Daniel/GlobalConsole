package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager

/**
 * Disposición QWERTY del teclado virtual.
 * Cada sublista es una fila de teclas.
 * Las teclas especiales se representan con cadenas descriptivas.
 */
private val KEYBOARD_LOWERCASE: List<List<String>> = listOf(
    listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "⌫"),
    listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "↵"),
    listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m"),
    listOf("ESPACIO")
)

private val KEYBOARD_UPPERCASE: List<List<String>> = listOf(
    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "⌫"),
    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L", "↵"),
    listOf("SHIFT", "Z", "X", "C", "V", "B", "N", "M"),
    listOf("ESPACIO")
)

/**
 * Teclado en pantalla (OSK) navegable 100% por gamepad.
 *
 * Controles:
 * - D-Pad UP/DOWN/LEFT/RIGHT: Mover el foco entre teclas, con wrap-around en los bordes.
 * - CONFIRM (A): Escribir el carácter enfocado.
 * - DELETE (Cuadrado/X): Borrar la última letra del texto.
 * - BACK (B): Cerrar el teclado sin confirmar.
 * - MENU (Start): Confirmar el texto actual y cerrar el teclado.
 *
 * @param gamepadManager Gestor de eventos del gamepad físico.
 * @param initialText Texto inicial del campo de búsqueda.
 * @param onTextChanged Callback emitido en tiempo real cuando el texto cambia.
 * @param onConfirm Callback emitido al pulsar START con el texto final.
 * @param onDismiss Callback emitido al pulsar B para cerrar sin confirmar.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
@Composable
fun GamepadOSK(
    gamepadManager: GamepadManager,
    initialText: String = "",
    onTextChanged: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf(initialText) }
    var isUpperCase by remember { mutableStateOf(false) }
    val inputMode by gamepadManager.inputMode.collectAsState()
    var focusedRow by remember { mutableStateOf(0) }
    var focusedCol by remember { mutableStateOf(0) }

    val layout = if (isUpperCase) KEYBOARD_UPPERCASE else KEYBOARD_LOWERCASE

    // Asegurar que el foco no supera el tamaño de la fila actual al cambiar de fila
    LaunchedEffect(focusedRow) {
        val rowSize = layout[focusedRow].size
        if (focusedCol >= rowSize) focusedCol = rowSize - 1
    }

    // Navegación e input por gamepad
    LaunchedEffect(gamepadManager) {
        gamepadManager.events.collect { event ->
            val currentRowSize = layout[focusedRow].size
            when (event) {
                is GamepadEvent.DirectionPressed -> when (event.direction) {
                    GamepadEvent.Direction.UP -> {
                        focusedRow = if (focusedRow > 0) focusedRow - 1 else layout.size - 1
                    }
                    GamepadEvent.Direction.DOWN -> {
                        focusedRow = if (focusedRow < layout.size - 1) focusedRow + 1 else 0
                    }
                    GamepadEvent.Direction.LEFT -> {
                        focusedCol = if (focusedCol > 0) focusedCol - 1 else currentRowSize - 1
                    }
                    GamepadEvent.Direction.RIGHT -> {
                        focusedCol = if (focusedCol < currentRowSize - 1) focusedCol + 1 else 0
                    }
                }
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.CONFIRM -> {
                        val key = layout[focusedRow][focusedCol]
                        inputText = handleKeyPress(key, inputText) { isUpperCase = !isUpperCase }
                        onTextChanged(inputText)
                    }
                    GamepadEvent.Button.DELETE -> {
                        if (inputText.isNotEmpty()) {
                            inputText = inputText.dropLast(1)
                            onTextChanged(inputText)
                        }
                    }
                    GamepadEvent.Button.BACK -> onDismiss()
                    GamepadEvent.Button.MENU -> onConfirm(inputText)
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
                .background(Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F0F), RectangleShape)
                    .border(2.dp, Color.White, RectangleShape)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Campo de texto ─────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF00FFCC), RectangleShape)
                        .background(Color(0xFF161616))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (inputText.isEmpty()) "Escribe para buscar…" else "$inputText|",
                        color = if (inputText.isEmpty()) Color(0xFF555555) else Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Filas del teclado ──────────────────────────────────────────
                layout.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        row.forEachIndexed { colIndex, key ->
                            val isFocused = rowIndex == focusedRow && colIndex == focusedCol
                            OSKKey(
                                label = key,
                                isFocused = isFocused,
                                isSpecial = key in listOf("SHIFT", "↵", "⌫", "ESPACIO"),
                                inputMode = inputMode,
                                onClick = {
                                    if (key == "↵") {
                                        onConfirm(inputText)
                                    } else if (key == "⌫") {
                                        if (inputText.isNotEmpty()) {
                                            inputText = inputText.dropLast(1)
                                            onTextChanged(inputText)
                                        }
                                    } else {
                                        inputText = handleKeyPress(key, inputText) { isUpperCase = !isUpperCase }
                                        onTextChanged(inputText)
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Leyenda de controles ───────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem("A", "Escribir")
                    LegendItem("⬛", "Borrar")
                    LegendItem("B", "Cancelar")
                    LegendItem("START", "Buscar")
                }
            }
        }
    }
}

/**
 * Procesa la pulsación de una tecla y devuelve el texto resultante.
 * Gestiona las teclas especiales (Backspace, Intro, Shift, Espacio) y las regulares.
 *
 * @param key La tecla pulsada (cadena de texto).
 * @param currentText El texto actual del campo de búsqueda.
 * @param onShiftToggle Lambda para alternar la capa de mayúsculas.
 * @return El nuevo texto resultante de la pulsación.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
private fun handleKeyPress(key: String, currentText: String, onShiftToggle: () -> Unit): String {
    return when (key) {
        "⌫" -> if (currentText.isNotEmpty()) currentText.dropLast(1) else currentText
        "↵" -> currentText // El intro de confirmación lo gestiona MENU
        "SHIFT" -> { onShiftToggle(); currentText }
        "ESPACIO" -> "$currentText "
        else -> currentText + key
    }
}

/**
 * Composable que representa una tecla individual del teclado OSK.
 * El estilo cambia según si la tecla tiene el foco o si es una tecla especial.
 *
 * @param label Texto o símbolo que muestra la tecla.
 * @param isFocused True si esta tecla tiene el foco del gamepad.
 * @param isSpecial True si es una tecla especial (Backspace, Enter, Shift, Espacio).
 * @param onClick Acción a ejecutar si el usuario pulsa con el ratón.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
@Composable
private fun OSKKey(
    label: String,
    isFocused: Boolean,
    isSpecial: Boolean,
    inputMode: org.example.globalconsole.presesentation.input.InputMode,
    onClick: () -> Unit
) {
    val isVirtualFocused = isFocused && inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD
    val bgColor = when {
        isVirtualFocused -> Color(0xFF00FFCC)
        isSpecial -> Color(0xFF2A2A2A)
        else -> Color(0xFF1A1A1A)
    }
    val textColor = if (isVirtualFocused) Color.Black else Color.White
    val width = when (label) {
        "ESPACIO" -> 320.dp
        "SHIFT", "↵" -> 64.dp
        "⌫" -> 56.dp
        else -> 44.dp
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(44.dp)
            .padding(2.dp)
            .background(bgColor, RectangleShape)
            .border(1.dp, if (isVirtualFocused) Color(0xFF00FFCC) else Color(0xFF444444), RectangleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (label.length > 3) 10.sp else 14.sp,
            fontWeight = if (isVirtualFocused) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable auxiliar para la leyenda de controles del OSK.
 *
 * @param button Nombre del botón del mando.
 * @param action Descripción de la acción que ejecuta.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
@Composable
private fun LegendItem(button: String, action: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .background(Color(0xFF333333), RectangleShape)
                .border(1.dp, Color.Gray, RectangleShape)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = button,
                color = Color(0xFF00FFCC),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = action,
            color = Color.Gray,
            fontSize = 11.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}
