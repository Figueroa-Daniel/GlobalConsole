package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import org.example.globalconsole.presesentation.input.InputMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón genérico estilo Metro para la interfaz de GlobalConsole.
 * Reacciona al hover del ratón (stick derecho) y al foco (cruceta) invirtiendo sus colores
 * para dar feedback visual claro al usuario en la televisión.
 *
 * @param text Texto del botón.
 * @param onClick Acción a ejecutar.
 * @param modifier Tamaño y posición del botón.
 * @param isPrimary Destaca una opción seleccionada sin confundirla con el foco de navegación.
 * @param isFocused Si es true, fuerza el estado visual de foco (útil para navegación por gamepad en diálogos).
 *
 * @param inputMode Modo activo para distinguir foco lógico y hover.
 * @param onHover Sincroniza la selección al entrar el cursor.
 * @return Unit.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun MetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isFocused: Boolean? = null,
    inputMode: InputMode = InputMode.GAMEPAD,
    onHover: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(isHovered, inputMode) { if (isHovered && inputMode == InputMode.MOUSE) onHover() }
    val isComposeFocused by interactionSource.collectIsFocusedAsState()
    
    val palette = LocalMetroPalette.current
    val isVirtualFocused = (isFocused ?: isComposeFocused) && inputMode == InputMode.GAMEPAD
    val isActive = if (inputMode == InputMode.MOUSE) isHovered else isVirtualFocused
    val bgColor = if (isActive) palette.selection else if (isPrimary) palette.raised else palette.surface
    val textColor = if (isActive) palette.onSelection else if (isPrimary) palette.accent else palette.text

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = MetroLayout.controlHeight)
            .background(bgColor)
            .metroFocus(isActive, palette)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}
