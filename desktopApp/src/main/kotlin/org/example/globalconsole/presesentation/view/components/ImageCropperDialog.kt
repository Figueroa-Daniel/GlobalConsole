package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.globalconsole.presesentation.input.GamepadEvent
import org.example.globalconsole.presesentation.input.GamepadManager
import java.io.File
import kotlin.math.abs

/**
 * Diálogo modal de recorte de imagen controlado 100% por gamepad.
 *
 * Permite al usuario seleccionar visualmente qué región de una imagen se usará
 * como carátula del juego, de manera similar a Instagram:
 * - **Stick izquierdo:** Desplaza la imagen dentro del cuadro de recorte.
 * - **L2 (Trigger izquierdo):** Reduce el zoom (aleja la imagen).
 * - **R2 (Trigger derecho):** Aumenta el zoom (acerca la imagen).
 * - **A (Cruz/Confirmar):** Aplica el recorte y guarda el resultado.
 * - **B (Círculo/Cancelar):** Cierra el diálogo sin guardar cambios.
 *
 * Al confirmar, delega el recorte real a [ImageCropperUtils.cropAndSaveImage],
 * que genera un archivo `cover_cropped.png` en el directorio del juego.
 *
 * @param imagePath Ruta absoluta a la imagen fuente a recortar.
 * @param outputDir Directorio donde se guardará el archivo recortado resultante.
 * @param gamepadManager Gestor del gamepad para leer eventos de botones y ejes en tiempo real.
 * @param onDismiss Acción ejecutada al cancelar el diálogo (botón B).
 * @param onCropSaved Acción ejecutada tras guardar el recorte, con la ruta del nuevo archivo.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
fun ImageCropperDialog(
    imagePath: String,
    outputDir: String,
    gamepadManager: GamepadManager?,
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

    // Botón seleccionado actualmente: 0 = Cancelar, 1 = Confirmar
    var selectedButton by remember { mutableStateOf(1) }

    // Leer el stick izquierdo en tiempo real para mover la imagen (~60Hz)
    LaunchedEffect(gamepadManager) {
        if (gamepadManager == null) return@LaunchedEffect
        while (true) {
            val stickX = gamepadManager.leftStickX.value
            val stickY = gamepadManager.leftStickY.value
            val deadZone = 0.15f
            val speed = 6f

            if (abs(stickX) > deadZone) offsetX += stickX * speed
            if (abs(stickY) > deadZone) offsetY += stickY * speed

            // L2 aleja (zoom -), R2 acerca (zoom +)
            // Los triggers GLFW van de -1.0 (suelto) a +1.0 (pulsado a fondo)
            val l2 = (gamepadManager.leftTrigger.value + 1f) / 2f  // normalizar a [0..1]
            val r2 = (gamepadManager.rightTrigger.value + 1f) / 2f
            val zoomSpeed = 0.01f
            if (r2 > 0.1f) scale = (scale + r2 * zoomSpeed).coerceIn(0.5f, 5f)
            if (l2 > 0.1f) scale = (scale - l2 * zoomSpeed).coerceIn(0.5f, 5f)

            delay(16L) // ~60Hz
        }
    }

    // Escuchar botones del mando: D-Pad izquierdo/derecho para cambiar botón, A para confirmar, B para cancelar
    LaunchedEffect(gamepadManager) {
        if (gamepadManager == null) return@LaunchedEffect
        gamepadManager.events.collectLatest { event ->
            when (event) {
                is GamepadEvent.ButtonPressed -> when (event.button) {
                    GamepadEvent.Button.BACK -> onDismiss()
                    GamepadEvent.Button.CONFIRM -> {
                        if (!isSaving) {
                            if (selectedButton == 0) {
                                // Botón cancelar seleccionado
                                onDismiss()
                            } else {
                                // Botón confirmar seleccionado → recortar y guardar
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
                                        onCropSaved(File(outputDir, "cover_cropped.png").absolutePath)
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
                is GamepadEvent.DirectionPressed -> when (event.direction) {
                    // D-Pad izquierdo/derecho cambia el botón de acción enfocado
                    GamepadEvent.Direction.LEFT -> selectedButton = 0
                    GamepadEvent.Direction.RIGHT -> selectedButton = 1
                    else -> {}
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
                .background(Color(0xE6000000))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Título estilo Metro
                Text(
                    text = "AJUSTAR CARÁTULA",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Instrucciones de control del mando
                Text(
                    text = "STICK IZQ. MUEVE · R2 ACERCA · L2 ALEJA",
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // --- Área de previsualización interactiva ---
                Box(
                    modifier = Modifier
                        .size(480.dp)
                        .background(Color(0xFF111111))
                        .onSizeChanged { previewSize = it }
                ) {
                    // Imagen con transformación aplicada en tiempo real por gamepad
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

                    // Máscara semitransparente con recuadro cuadrado de recorte
                    CropMask()
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Indicador de zoom actual
                Text(
                    text = "ZOOM: ${"%.1f".format(scale)}x",
                    color = Color(0xFF555555),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Botones de acción estilo Metro con foco de gamepad
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    GamepadFocusButton(
                        text = "B  CANCELAR",
                        isFocused = selectedButton == 0,
                        backgroundColor = if (selectedButton == 0) Color.White else Color(0xFF222222),
                        textColor = if (selectedButton == 0) Color.Black else Color(0xFF888888),
                        onClick = onDismiss
                    )
                    GamepadFocusButton(
                        text = if (isSaving) "GUARDANDO..." else "A  CONFIRMAR",
                        isFocused = selectedButton == 1,
                        backgroundColor = if (selectedButton == 1) Color.White else Color(0xFF222222),
                        textColor = if (selectedButton == 1) Color.Black else Color(0xFF888888),
                        onClick = {}
                    )
                }
            }
        }
    }
}

/**
 * Máscara semitransparente que oscurece los bordes del área de previsualización,
 * dejando visible únicamente el cuadrado central que define la zona de recorte.
 * Incluye esquinas blancas estilo Metro para indicar visualmente el área seleccionada.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
private fun BoxScope.CropMask() {
    // Bordes oscuros que ocultan lo que quedará fuera del recorte
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f).background(Color(0xBB000000)).align(Alignment.TopCenter))
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f).background(Color(0xBB000000)).align(Alignment.BottomCenter))
    Box(modifier = Modifier.fillMaxWidth(0.1f).fillMaxHeight().background(Color(0xBB000000)).align(Alignment.CenterStart))
    Box(modifier = Modifier.fillMaxWidth(0.1f).fillMaxHeight().background(Color(0xBB000000)).align(Alignment.CenterEnd))

    // Recuadro central: esquinas blancas estilo Metro
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
 * Renderiza una esquina del recuadro de recorte con dos líneas blancas perpendiculares.
 *
 * @param alignment Posición de la esquina dentro del Box padre.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
private fun BoxScope.CropCorner(alignment: Alignment) {
    Box(modifier = Modifier.size(24.dp, 3.dp).align(alignment).background(Color.White))
    Box(modifier = Modifier.size(3.dp, 24.dp).align(alignment).background(Color.White))
}

/**
 * Botón de acción estilo Metro con soporte de foco visual del gamepad.
 * Cuando está enfocado se resalta con fondo blanco y texto negro.
 *
 * @param text Texto a mostrar en el botón.
 * @param isFocused Indica si el botón está actualmente enfocado por el gamepad.
 * @param backgroundColor Color de fondo del botón.
 * @param textColor Color del texto del botón.
 * @param onClick Acción ejecutada al pulsar el botón (uso con ratón, no necesario para gamepad).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
@Composable
private fun GamepadFocusButton(
    text: String,
    isFocused: Boolean,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RectangleShape)
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
