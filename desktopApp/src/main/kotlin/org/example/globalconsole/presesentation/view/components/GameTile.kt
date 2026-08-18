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
import org.example.globalconsole.generalDomain.entititys.Platforms
import coil3.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import globalconsole.shared.generated.resources.Res
import globalconsole.shared.generated.resources.*
import java.io.File

/**
 * Representa una tarjeta (Tile) estilo Metro de un juego.
 * Muestra el nombre del juego, plataforma y reacciona de forma dinámica con efectos visuales premium
 * (glow, color e incrementos de escala) tanto al posicionar el ratón encima (hover) como al
 * enfocarlo mediante teclado o gamepad.
 *
 * @param game Datos del juego a renderizar.
 * @param focusRequester Requester para control de foco externo (gamepad).
 * @param inputMode Modo de entrada activo (gamepad o ratón).
 * @param onClick Acción ejecutada al seleccionar el juego (click primario o botón A del mando).
 * @param onFocus Acción ejecutada al recibir el foco del teclado o gamepad (modo GAMEPAD).
 * @param onHover Acción ejecutada cuando el cursor del ratón entra en el tile (modo MOUSE).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun GameTile(
    game: Game,
    focusRequester: FocusRequester = remember { FocusRequester() },
    inputMode: org.example.globalconsole.presesentation.input.InputMode = org.example.globalconsole.presesentation.input.InputMode.GAMEPAD,
    onClick: () -> Unit,
    onFocus: () -> Unit = {},
    onHover: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    // Solo aplicar el brillo de isFocused si el modo de entrada es GAMEPAD.
    // Si es MOUSE, ignoramos el isFocused virtual y usamos solo el hover real del ratón.
    val isVirtualFocused = isFocused && inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD
    val isActive = isHovered || isVirtualFocused

    // Notificar al componente madre cuando este juego obtenga el foco por gamepad
    LaunchedEffect(isFocused) {
        if (isFocused) onFocus()
    }

    // Notificar al componente madre cuando el cursor del ratón entra en este tile (modo MOUSE)
    LaunchedEffect(isHovered) {
        if (isHovered) onHover()
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
        // Imagen del juego o por defecto
        val imagePath = game.image
        if (imagePath != null && File(imagePath).exists()) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = "Carátula de ${game.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val defaultImage = when (game.platform) {
                Platforms.PCSX2 -> Res.drawable.play2Logo
                Platforms.HEORIC_GAMES_LAUCHER -> Res.drawable.heroicLogo
                Platforms.MELONDS -> {
                    if (game.id == "melonds-launcher") Res.drawable.melonDSLogo else Res.drawable.dsLogo
                }
                Platforms.DOLPHIN -> {
                    if (game.id == "dolphin-launcher-id") Res.drawable.dolphinLogo else Res.drawable.wiiLogo
                }
                else -> null
            }
            if (defaultImage != null) {
                Image(
                    painter = painterResource(defaultImage),
                    contentDescription = "Logo por defecto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

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
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = game.platform.name,
                color = Color.Black,
                fontSize = 12.sp,
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
                text = if (isActive) "EJECUTAR >" else "${game.platform.name} SYSTEM",
                color = if (isActive) Color.White else Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

