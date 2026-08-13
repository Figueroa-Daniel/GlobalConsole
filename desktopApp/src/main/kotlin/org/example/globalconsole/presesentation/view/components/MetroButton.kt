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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * @param modifier Modificador extra opcional (por defecto usa borde blanco normal y fondo negro/gris oscuro).
 * @param isPrimary Si es true, el botón se ve blanco por defecto (destacado). Si es false, se ve con borde blanco y fondo oscuro.
 * @param isFocused Si es true, fuerza el estado visual de foco (útil para navegación por gamepad en diálogos).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun MetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isFocused: Boolean = false,
    onHover: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isComposeFocused by interactionSource.collectIsFocusedAsState()
    val isActive = isHovered || isComposeFocused || isFocused

    androidx.compose.runtime.LaunchedEffect(isHovered) {
        if (isHovered && !isFocused && !isComposeFocused) {
            onHover()
        }
    }
    
    val bgColor = if (isActive) {
        if (isPrimary) Color(0xFFCCCCCC) else Color.White
    } else {
        if (isPrimary) Color.White else Color(0xFF161616)
    }
    
    val textColor = if (isActive) {
        Color.Black
    } else {
        if (isPrimary) Color.Black else Color.White
    }
    
    val borderColor = if (isActive) {
        Color.White
    } else {
        if (isPrimary) Color.White else Color(0xFF444444)
    }

    Box(
        modifier = modifier
            .background(bgColor)
            .border(1.dp, borderColor, RectangleShape)
            .hoverable(interactionSource)
            .focusable(interactionSource = interactionSource)
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
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}
