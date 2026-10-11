package org.example.globalconsole.presesentation.view.components

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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import globalconsole.shared.generated.resources.Res
import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms
import coil3.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import globalconsole.shared.generated.resources.*
import java.io.File

/**
 * Representa una tarjeta (Tile) estilo Metro de un juego.
 * Separa la carátula y el pie tipográfico para mantener la legibilidad, con
 * iluminación y una composición geométrica cuando no existe imagen personalizada.
 *
 * @param game Datos del juego a renderizar.
 * @param focusRequester Requester para control de foco externo (gamepad).
 * @param inputMode Modo de entrada activo (gamepad o ratón).
 * @param selected Selección lógica; null permite utilizar el foco nativo.
 * @param onClick Acción ejecutada al seleccionar el juego (click primario o botón A del mando).
 * @param onFocus Acción ejecutada al recibir el foco del teclado o gamepad (modo GAMEPAD).
 * @param onHover Acción ejecutada cuando el cursor del ratón entra en el tile (modo MOUSE).
 *
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun GameTile(
    game: Game,
    focusRequester: FocusRequester = remember { FocusRequester() },
    inputMode: org.example.globalconsole.presesentation.input.InputMode = org.example.globalconsole.presesentation.input.InputMode.GAMEPAD,
    onClick: () -> Unit,
    selected: Boolean? = null,
    onFocus: () -> Unit = {},
    onHover: () -> Unit = {}
) {
    val palette = LocalMetroPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    // Solo aplicar el brillo de isFocused si el modo de entrada es GAMEPAD.
    // Si es MOUSE, ignoramos el isFocused virtual y usamos solo el hover real del ratón.
    val isVirtualFocused = (selected ?: isFocused) && inputMode == org.example.globalconsole.presesentation.input.InputMode.GAMEPAD
    val isActive = if (inputMode == org.example.globalconsole.presesentation.input.InputMode.MOUSE) isHovered else isVirtualFocused

    // Notificar al componente madre cuando este juego obtenga el foco por gamepad
    LaunchedEffect(isFocused) {
        if (isFocused) onFocus()
    }

    // Notificar al componente madre cuando el cursor del ratón entra en este tile (modo MOUSE)
    LaunchedEffect(isHovered, inputMode) {
        if (isHovered && inputMode == org.example.globalconsole.presesentation.input.InputMode.MOUSE) onHover()
    }

    // Animación de escala suave
    val scaleFactor by animateFloatAsState(
        targetValue = if (isActive) 1.015f else 1.0f,
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        Modifier.scale(scaleFactor).aspectRatio(.82f)
            .background(palette.surface).metroFocus(isActive, palette)
            .hoverable(interactionSource).focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(Modifier.weight(1f).fillMaxWidth().clipToBounds()
            .background(Brush.linearGradient(listOf(palette.raised, palette.background)))) {
            val imagePath = game.image
            if (imagePath != null && File(imagePath).exists()) {
                AsyncImage(model = File(imagePath), contentDescription = stringResource(Res.string.game_cover_description, game.name),
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize().drawBehind {
                    drawRect(Brush.linearGradient(listOf(palette.selection.copy(alpha = .26f), Color.Transparent)))
                    for (line in 0..4) {
                        val x = size.width * (.15f + line * .27f)
                        drawLine(palette.accent.copy(alpha = .12f), Offset(x, 0f),
                            Offset(x - size.height * .65f, size.height), strokeWidth = 1.dp.toPx())
                    }
                    drawRect(palette.selection.copy(alpha = .12f),
                        topLeft = Offset(size.width * .72f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width * .28f, size.height))
                })
                val defaultImage = when (game.platform) {
                    Platforms.PCSX2 -> Res.drawable.play2Logo
                    Platforms.HEORIC_GAMES_LAUCHER -> Res.drawable.heroicLogo
                    Platforms.MELONDS -> {
                        if (game.id == "melonds-launcher") Res.drawable.melonDSLogo else Res.drawable.dsLogo
                    }
                    Platforms.DOLPHIN -> {
                        if (game.id == "dolphin-launcher-id") Res.drawable.dolphinLogo else Res.drawable.wiiLogo
                    }
                    Platforms.PS3 -> Res.drawable.play3_logo_launcher
                    Platforms.AZAHAR -> {
                        if (game.id == "azahar-launcher") Res.drawable.azhar_logo else Res.drawable._3dsJuegos
                    }
                    Platforms.DUCKSTATION -> {
                        if (game.id == "duckstation-launcher-id") Res.drawable.playLogo else Res.drawable.playLogoGames
                    }
                    Platforms.NAVWEB -> {
                        val nav = game as? org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj
                        when (nav?.navigatorType) {
                            org.example.globalconsole.generalDomain.entititys.NavigatorsWeb.CHROME -> Res.drawable.Chrome_logo
                            org.example.globalconsole.generalDomain.entititys.NavigatorsWeb.EDGE -> Res.drawable.Edge_logo
                            org.example.globalconsole.generalDomain.entititys.NavigatorsWeb.FIREFOX -> Res.drawable.Firefox_logo
                            org.example.globalconsole.generalDomain.entititys.NavigatorsWeb.BRAVE -> Res.drawable.brave_logo
                            else -> null
                        }
                    }
                    else -> null
                }
                if (defaultImage == null && game.platform == Platforms.LOCALGAME) {
                    Text("PC", color = palette.accent, fontSize = 48.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center))
                }
                if (defaultImage != null) {
                    val isConsolePhoto = defaultImage == Res.drawable.playLogo
                    val isPlayStationScreen = defaultImage == Res.drawable.playLogoGames
                    val isBrowserLogo = game.platform == Platforms.NAVWEB
                    val imageScale = when {
                        isConsolePhoto || isPlayStationScreen -> 1f
                        isBrowserLogo -> 0.72f
                        else -> 0.58f
                    }
                    Image(
                        painter = painterResource(defaultImage),
                        contentDescription = null,
                        contentScale = if (isConsolePhoto) ContentScale.Crop else ContentScale.Fit,
                        modifier = Modifier.align(Alignment.Center)
                            .fillMaxSize(imageScale)
                            .then(if (isPlayStationScreen) Modifier.background(Color.Black).padding(8.dp) else Modifier)
                    )
                }
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
                listOf(Color.Transparent, palette.background.copy(alpha = .4f)))))
            Text(platformLabel(game.platform.name).uppercase(), color = palette.text,
                fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                    .background(palette.background.copy(alpha = .88f)).padding(horizontal = 8.dp, vertical = 5.dp))
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(
            if (isActive) palette.selection else palette.border.copy(alpha = .5f)))
        Column(Modifier.fillMaxWidth().background(
            if (isActive) palette.raised else palette.surface).padding(16.dp)) {
            Text(game.name, color = palette.text, fontSize = 17.sp, lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(44.dp))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(if (isActive) stringResource(Res.string.action_open) else platformLabel(game.platform.name),
                    color = if (isActive) palette.accent else palette.muted,
                    fontSize = 11.sp, letterSpacing = if (isActive) 1.5.sp else 0.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Box(Modifier.size(24.dp).background(if (isActive) palette.selection else Color.Transparent),
                    contentAlignment = Alignment.Center) {
                    Text(if (isActive) "A" else "↗", color = if (isActive) palette.onSelection else palette.muted,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
