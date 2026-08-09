package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.globalconsole.settings.ROUTE_PCSX2_GAMES
import java.io.File
import javax.swing.JFileChooser

/**
 * Diálogo modal para la selección de la ruta de juegos de PCSX2.
 * Diseñado con estética oscura Metro (bordes blancos nítidos, sin esquinas redondeadas, negro puro).
 *
 * @param onDismiss Llamado al cerrar el diálogo.
 * @param onConfirm Llamado tras guardar con éxito la nueva ruta.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPathDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var pathText by remember { mutableStateOf(ROUTE_PCSX2_GAMES ?: "") }
    var errorMessage by remember { mutableStateOf("") }

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
                    .width(500.dp)
                    .background(Color(0xFF0F0F0F), RectangleShape)
                    .border(2.dp, Color.White, RectangleShape)
                    .padding(24.dp)
            ) {
                Text(
                    text = "CONFIGURACIÓN DE RUTA DE JUEGOS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Selecciona la carpeta donde guardas tus juegos / ISOs para PCSX2.",
                    color = Color(0xFFCCCCCC),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = pathText,
                        onValueChange = { pathText = it },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, RectangleShape),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF161616),
                            unfocusedContainerColor = Color(0xFF161616),
                            disabledContainerColor = Color(0xFF161616),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    MetroButton(
                        text = "EXAMINAR",
                        isPrimary = true,
                        onClick = {
                            val chooser = JFileChooser().apply {
                                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                dialogTitle = "Selecciona la carpeta de Juegos"
                            }
                            val result = chooser.showOpenDialog(null)
                            if (result == JFileChooser.APPROVE_OPTION) {
                                pathText = chooser.selectedFile.absolutePath
                            }
                        }
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MetroButton(
                        text = "CANCELAR",
                        onClick = onDismiss
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    MetroButton(
                        text = "GUARDAR",
                        isPrimary = true,
                        onClick = {
                            if (pathText.isBlank()) {
                                errorMessage = "La ruta no puede estar vacía"
                            } else {
                                val file = File(pathText)
                                if (file.exists() && file.isDirectory) {
                                    ROUTE_PCSX2_GAMES = pathText
                                    onConfirm()
                                } else {
                                    errorMessage = "La ruta no es un directorio válido"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
