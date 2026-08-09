package org.example.globalconsole.presesentation.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.globalconsole.generalDomain.entititys.Game

/**
 * Representa una tarjeta (Tile) estilo Metro de un juego.
 * Muestra el nombre del juego, plataforma y reacciona de forma dinámica con efectos visuales premium
 * (glow, color e incrementos de escala) tanto al posicionar el ratón encima (hover) como al
 * enfocarlo mediante teclado o gamepad.
 *
 * @param game Datos del juego a renderizar.
 * @param onClick Acción ejecutada al seleccionar el juego.
 * @param onFocus Acción ejecutada al recibir el foco del teclado o gamepad.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun GameTile(
    game: Game,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onClick: () -> Unit,
    onFocus: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    // Activo si está bajo el cursor o enfocado por gamepad/teclado
    val isActive = isHovered || isFocused

    // Notificar al componente madre cuando este juego obtenga el foco por cualquier medio
    LaunchedEffect(isFocused) {
        if (isFocused) {
            onFocus()
        }
    }

    // Sincronizar el ratón con el sistema de foco: al pasar por encima, solicitamos foco
    // de forma que si el usuario presiona "Confirmar" con el mando, lance este juego.
    LaunchedEffect(isHovered) {
        if (isHovered && !isFocused) {
            onFocus()
            focusRequester.requestFocus()
        }
    }

    // Animación de escala suave
    val scaleFactor by animateFloatAsState(
        targetValue = if (isActive) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 200)
    )

    // Animación de brillo/glow del borde blanco Metro
    val borderColor by animateColorAsState(
        targetValue = if (isActive) Color.White else Color(0xFF222222),
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = Modifier
            .scale(scaleFactor)
            .aspectRatio(1f) // Cuadrado estilo Metro
            .background(Color(0xFF111111))
            .border(1.dp, borderColor, RectangleShape)
            .hoverable(interactionSource)
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Elimina el ripple nativo para mantener estética minimalista
                onClick = onClick
            )
    ) {
        // Fondo con un sutil degradado oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Detalle de la Plataforma (Badge Metro en la esquina superior derecha)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.White, RectangleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = game.platform.name,
                color = Color.Black,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }

        // Nombre e info del juego en la parte inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = game.name.uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtexto / Acción interactiva
            Text(
                text = if (isActive) "EJECUTAR >" else "PCSX2 SYSTEM",
                color = if (isActive) Color.White else Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

