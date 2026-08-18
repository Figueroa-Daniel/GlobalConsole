package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Diálogo modal de recorte de imagen al estilo Instagram.
 *
 * Muestra una previsualización interactiva de la imagen donde el usuario puede:
 * - Arrastrar la imagen para desplazarla dentro del cuadro de recorte.
 * - Hacer zoom con la rueda del ratón o gesture de pinza para escalarla.
 *
 * Al confirmar, utiliza [ImageCropperUtils.cropAndSaveImage] para guardar la región
 * seleccionada como `cover_cropped.png` en el directorio del juego.
 *
 * @param imagePath Ruta absoluta a la imagen fuente que se va a recortar.
 * @param outputDir Directorio donde se guardará el archivo recortado resultante.
 * @param onDismiss Acción ejecutada al cancelar o cerrar el diálogo sin guardar.
 * @param onCropSaved Acción ejecutada tras guardar el recorte exitosamente, con la ruta del archivo.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
fun ImageCropperDialog(
    imagePath: String,
    outputDir: String,
    onDismiss: () -> Unit,
    onCropSaved: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Estado de transformación de la imagen (offset y escala)
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var previewSize by remember { mutableStateOf(IntSize.Zero) }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000)) // Fondo oscuro semitransparente
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Título estilo Metro
                Text(
                    text = "SELECCIONA EL ÁREA DE CARÁTULA",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "ARRASTRA · RUEDA DEL RATÓN PARA ZOOM",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // --- Zona de previsualización interactiva ---
                Box(
                    modifier = Modifier
                        .size(480.dp)
                        .background(Color(0xFF111111))
                        .onSizeChanged { previewSize = it }
                        // Detectar drag para desplazar la imagen
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        // Detectar gestos de pinza para zoom
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoomChange, _ ->
                                scale = (scale * zoomChange).coerceIn(0.5f, 5f)
                            }
                        }
                ) {
                    // Imagen con transformación aplicada en tiempo real
                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = "Imagen a recortar",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            }
                    )

                    // Máscara de recorte: oscurece todo excepto el cuadro central
                    CropMask()
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botones de acción estilo Metro
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Botón cancelar
                    MetroButton(
                        text = "CANCELAR",
                        backgroundColor = Color(0xFF222222),
                        textColor = Color(0xFF888888),
                        onClick = onDismiss
                    )

                    // Botón confirmar
                    MetroButton(
                        text = if (isSaving) "GUARDANDO..." else "CONFIRMAR",
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        onClick = {
                            if (!isSaving) {
                                isSaving = true
                                scope.launch {
                                    val success = withContext(Dispatchers.IO) {
                                        ImageCropperUtils.cropAndSaveImage(
                                            sourcePath = imagePath,
                                            offsetX = offsetX,
                                            offsetY = offsetY,
                                            scale = scale,
                                            previewSizePx = previewSize.width.takeIf { it > 0 } ?: 480,
                                            outputDir = outputDir
                                        )
                                    }
                                    isSaving = false
                                    if (success) {
                                        val croppedPath = File(outputDir, "cover_cropped.png").absolutePath
                                        onCropSaved(croppedPath)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Máscara semitransparente que oscurece los bordes del área de previsualización,
 * dejando visible únicamente el cuadrado central de recorte.
 *
 * Se renderiza dentro de un [BoxScope] para poder utilizar `.align()`.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
private fun BoxScope.CropMask() {
    // Borde superior
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .background(Color(0xAA000000))
            .align(Alignment.TopCenter)
    )
    // Borde inferior
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .background(Color(0xAA000000))
            .align(Alignment.BottomCenter)
    )
    // Borde izquierdo
    Box(
        modifier = Modifier
            .fillMaxWidth(0.1f)
            .fillMaxHeight()
            .background(Color(0xAA000000))
            .align(Alignment.CenterStart)
    )
    // Borde derecho
    Box(
        modifier = Modifier
            .fillMaxWidth(0.1f)
            .fillMaxHeight()
            .background(Color(0xAA000000))
            .align(Alignment.CenterEnd)
    )

    // Marco del área de recorte (línea blanca en las esquinas)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.8f)
            .align(Alignment.Center)
    ) {
        CropCorner(Alignment.TopStart)
        CropCorner(Alignment.TopEnd)
        CropCorner(Alignment.BottomStart)
        CropCorner(Alignment.BottomEnd)
    }
}

/**
 * Renderiza una esquina del recuadro de recorte en la posición indicada.
 *
 * @param alignment Posición de la esquina dentro del Box padre.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
private fun BoxScope.CropCorner(alignment: Alignment) {
    val isTop = alignment == Alignment.TopStart || alignment == Alignment.TopEnd
    val isStart = alignment == Alignment.TopStart || alignment == Alignment.BottomStart
    Box(
        modifier = Modifier
            .size(24.dp, 3.dp)
            .align(alignment)
            .background(Color.White)
    )
    Box(
        modifier = Modifier
            .size(3.dp, 24.dp)
            .align(alignment)
            .background(Color.White)
    )
}

/**
 * Botón de acción en estilo Metro UI: rectangulares, sin bordes redondeados,
 * con tipografía monoespaciada en negrita y un tono de color sólido.
 *
 * @param text Texto a mostrar en el botón.
 * @param backgroundColor Color de fondo del botón.
 * @param textColor Color del texto del botón.
 * @param onClick Acción ejecutada al pulsar el botón.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
private fun MetroButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RectangleShape)
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Modificador auxiliar que aplica un click sin efecto ripple.
 *
 * @param onClick Acción ejecutada al pulsar.
 * @return [Modifier] con el click configurado.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.pointerInput(onClick) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull()
            if (change != null && change.pressed && !change.previousPressed) {
                change.consume()
                onClick()
            }
        }
    }
}
