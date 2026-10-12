package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clipToBounds
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
import org.jetbrains.compose.resources.stringResource
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import globalconsole.shared.generated.resources.*
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
 * @param outputName Nombre del recorte asociado al juego.
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
    onCropSaved: (String) -> Unit,
    outputName: String = "cover_cropped.png"
) {
    val scope = rememberCoroutineScope()

    // Estado de transformación de la imagen (offset y escala)
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var previewSize by remember { mutableStateOf(IntSize.Zero) }
    var isSaving by remember { mutableStateOf(false) }

    val inputMode by gamepadManager?.inputMode?.collectAsState()
        ?: remember { mutableStateOf(org.example.globalconsole.presesentation.input.InputMode.GAMEPAD) }
    var saveError by remember { mutableStateOf(false) }
    val save: () -> Unit = {
        if (!isSaving && previewSize.width > 0) {
            isSaving = true
            saveError = false
            val capturedX = offsetX
            val capturedY = offsetY
            val capturedScale = scale
            val capturedSize = previewSize.width
            scope.launch {
                try {
                    val success = withContext(Dispatchers.IO) {
                        ImageCropperUtils.cropAndSaveImage(imagePath, capturedX, capturedY, capturedScale, capturedSize, outputDir, outputName)
                    }
                    if (success) onCropSaved(File(outputDir, File(outputName).name).absolutePath)
                    else saveError = true
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (e: Exception) {
                    saveError = true
                } finally {
                    isSaving = false
                }
            }
        }
    }
    val currentSave by rememberUpdatedState(save)

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
                    GamepadEvent.Button.OPTIONS -> selectedButton = 1
                    GamepadEvent.Button.BACK -> if (!isSaving) onDismiss()
                    GamepadEvent.Button.CONFIRM -> {
                        if (!isSaving) {
                            if (selectedButton == 0) {
                                // Botón cancelar seleccionado
                                onDismiss()
                            } else {
                                currentSave()
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
        onDismissRequest = { if (!isSaving) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
        ) {
            val previewSide = minOf(480.dp, maxWidth - 48.dp, (maxHeight - 230.dp).coerceAtLeast(100.dp))
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Título estilo Metro
                Text(
                    text = stringResource(Res.string.crop_title),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Instrucciones de control del mando
                Text(
                    text = stringResource(Res.string.crop_controls),
                    color = LocalMetroPalette.current.muted,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // --- Área de previsualización interactiva ---
                Box(
                    modifier = Modifier
                        .size(previewSide)
                        .clipToBounds()
                        .background(LocalMetroPalette.current.surface)
                        .onSizeChanged { previewSize = it }
                ) {
                    // Imagen con transformación aplicada en tiempo real por gamepad
                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = stringResource(Res.string.crop_image_description),
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
                    text = if (saveError) stringResource(Res.string.crop_save_error)
                    else stringResource(Res.string.crop_zoom, scale),
                    color = LocalMetroPalette.current.muted,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Botones de acción estilo Metro con foco de gamepad
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetroButton(
                        text = stringResource(Res.string.crop_cancel),
                        isFocused = selectedButton == 0,
                        onHover = { selectedButton = 0 },
                        inputMode = inputMode,
                        onClick = { if (!isSaving) onDismiss() }
                    )
                    MetroButton(
                        text = if (isSaving) stringResource(Res.string.crop_saving) else stringResource(Res.string.crop_confirm),
                        isFocused = selectedButton == 1,
                        onHover = { selectedButton = 1 },
                        inputMode = inputMode,
                        onClick = save
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
    Box(modifier = Modifier.size(24.dp, 3.dp).align(alignment).background(LocalMetroPalette.current.accent))
    Box(modifier = Modifier.size(3.dp, 24.dp).align(alignment).background(LocalMetroPalette.current.accent))
}
