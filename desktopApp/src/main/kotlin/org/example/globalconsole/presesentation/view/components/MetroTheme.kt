package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.example.globalconsole.settings.domain.AppearanceSettings
import globalconsole.shared.generated.resources.*

/**
 * Paleta Metro común; el acento nunca sustituye las superficies oscuras.
 * @property accent Color de foco y detalles.
 * @property glow Intensidad normalizada del halo.
 * @property selection Tono original para relleno y halo, incluidos colores oscuros.
 * @author Daniel Figueroa Vidal
 */
data class MetroPalette(
    val accent: Color = Color(0xFF53E0CF),
    val glow: Float = .45f,
    val selection: Color = accent
) {
    val onSelection = if (selection.luminance() > .179f) Color.Black else Color.White
    val background = Color(0xFF08090E)
    val surface = Color(0xFF11131B)
    val raised = Color(0xFF1A1D29)
    val border = Color(0xFF303443)
    val text = Color(0xFFF2F5FA)
    val muted = Color(0xFFA6B3C4)
}

val LocalMetroPalette = staticCompositionLocalOf { MetroPalette() }

/**
 * Propaga la apariencia a ventanas y diálogos Compose.
 * @param settings Preferencias del dominio.
 * @param content Contenido de la aplicación.
 * @return Unit.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@Composable
fun MetroTheme(settings: AppearanceSettings, content: @Composable () -> Unit) {
    val selection = metroAccentColor(settings.accentColor)
    val accent = (0..10).asSequence().map { lerp(selection, Color.White, it * .05f) }
        .firstOrNull { it.luminance() >= .30f } ?: Color.White
    val palette = MetroPalette(accent, settings.glowIntensity.coerceIn(0, 100) / 100f, selection)
    CompositionLocalProvider(LocalMetroPalette provides palette) {
        MaterialTheme(colorScheme = darkColorScheme(
            primary = palette.selection, onPrimary = palette.onSelection,
            secondary = accent, background = palette.background,
            surface = palette.surface, onSurface = palette.text,
            onBackground = palette.text, outline = palette.border
        ), content = content)
    }
}

/**
 * Dibuja un halo estático de bajo coste y un borde visible incluso con brillo cero.
 * @param active Indica selección actual.
 * @param palette Colores del tema.
 * @return Modificador de foco.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
fun Modifier.metroFocus(active: Boolean, palette: MetroPalette): Modifier =
    drawBehind {
        if (active && palette.glow > 0f) {
            for (step in 1..10) {
                val spread = (step * 1.4f).dp.toPx()
                drawRect(
                    color = palette.selection.copy(alpha = (11 - step) * .015f * palette.glow),
                    topLeft = Offset(-spread, -spread),
                    size = Size(size.width + spread * 2, size.height + spread * 2),
                    style = Stroke(2.dp.toPx())
                )
            }
        }
    }.border(if (active) 2.dp else 1.dp, if (active) palette.accent else palette.border)

/**
 * Traduce identificadores internos en nombres legibles de plataforma.
 * @param name Identificador de plataforma o título de grupo.
 * @return Etiqueta para la interfaz.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@Composable
fun platformLabel(name: String): String {
    val resource = when (name) {
        "SECTION_LAUNCHERS" -> Res.string.section_launchers
        "SECTION_GAMES" -> Res.string.section_games
        "HEORIC_GAMES_LAUCHER" -> Res.string.platform_heroic
        "PCSX2" -> Res.string.platform_pcsx2
        "MELONDS" -> Res.string.platform_melonds
        "DOLPHIN" -> Res.string.platform_dolphin
        "PS3" -> Res.string.platform_ps3
        "AZAHAR" -> Res.string.platform_azahar
        "DUCKSTATION" -> Res.string.platform_duckstation
        "NAVWEB" -> Res.string.platform_navweb
        else -> return name
    }
    return stringResource(resource)
}

/**
 * Resuelve el tono original elegido, separado del tono de texto accesible.
 * @param name Identificador persistido del color.
 * @return Color del relleno y del halo.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
fun metroAccentColor(name: String): Color = when (name) {
    "BLUE" -> Color(0xFF75B7FF)
    "VIOLET" -> Color(0xFFB6A0FF)
    "PINK" -> Color(0xFFFF91BE)
    "AMBER" -> Color(0xFFFFCA70)
    "GREEN" -> Color(0xFF9DDF84)
    "RED" -> Color(0xFFC93645)
    "CRIMSON" -> Color(0xFF852748)
    "NAVY" -> Color(0xFF183567)
    "COBALT" -> Color(0xFF244EB5)
    "TEAL" -> Color(0xFF008B87)
    "ORANGE" -> Color(0xFFEE8949)
    "ELECTRIC_PURPLE" -> Color(0xFF9F20FF)
    "NEON_PURPLE" -> Color(0xFFD400FF)
    "ULTRAVIOLET" -> Color(0xFF6F00FF)
    else -> Color(0xFF53E0CF)
}

/**
 * Presenta el nombre del color sin depender de posiciones en listas paralelas.
 * @param name Identificador persistido del color.
 * @return Nombre legible en castellano.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@Composable
fun metroAccentLabel(name: String): String = stringResource(when (name) {
    "BLUE" -> Res.string.color_blue
    "VIOLET" -> Res.string.color_violet
    "PINK" -> Res.string.color_pink
    "AMBER" -> Res.string.color_amber
    "GREEN" -> Res.string.color_green
    "RED" -> Res.string.color_red
    "CRIMSON" -> Res.string.color_crimson
    "NAVY" -> Res.string.color_navy
    "COBALT" -> Res.string.color_cobalt
    "TEAL" -> Res.string.color_teal
    "ORANGE" -> Res.string.color_orange
    "ELECTRIC_PURPLE" -> Res.string.color_electric_purple
    "NEON_PURPLE" -> Res.string.color_neon_purple
    "ULTRAVIOLET" -> Res.string.color_ultraviolet
    else -> Res.string.color_cyan
})

/**
 * Ritmo espacial compartido entre geometría visual y navegación del mando.
 * @author Daniel Figueroa Vidal
 */
object MetroLayout {
    val compactMargin = 24.dp
    val wideMargin = 32.dp
    val tileMinimum = 200.dp
    val tileGap = 20.dp
    val controlHeight = 48.dp
    val tileInset = 20.dp
}

/**
 * Iluminación ambiental estática sobre negro; nunca cambia el contraste de contenido.
 * @param palette Paleta e intensidad guardadas.
 * @return Fondo con luz localizada, apagada al establecer brillo cero.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
fun Modifier.metroAtmosphere(palette: MetroPalette): Modifier = background(palette.background).drawBehind {
    if (palette.glow > 0f) {
        drawRect(Brush.radialGradient(
            listOf(palette.selection.copy(alpha = .24f * palette.glow), Color.Transparent),
            center = Offset(size.width * .88f, 0f), radius = size.width * .8f
        ))
        drawRect(Brush.radialGradient(
            listOf(palette.selection.copy(alpha = .10f * palette.glow), Color.Transparent),
            center = Offset(0f, size.height), radius = size.width * .65f
        ))
    }
}

/**
 * Superficie de panel con borde discreto y remate superior del acento elegido.
 * @param palette Paleta del usuario.
 * @return Marco Metro compartido por los diálogos.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
fun Modifier.metroPanel(palette: MetroPalette): Modifier =
    background(Brush.verticalGradient(listOf(palette.raised, palette.surface)))
        .border(1.dp, palette.border)
        .drawBehind {
            drawRect(Brush.horizontalGradient(listOf(palette.selection, palette.accent, Color.Transparent)),
                size = Size(size.width, 3.dp.toPx()))
        }
