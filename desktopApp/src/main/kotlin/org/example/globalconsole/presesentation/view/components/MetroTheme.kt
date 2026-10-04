package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.example.globalconsole.settings.domain.AppearanceSettings

/**
 * Paleta Metro común; el acento nunca sustituye las superficies oscuras.
 * @property accent Color de foco y detalles.
 * @property glow Intensidad normalizada del halo.
 * @author Daniel Figueroa Vidal
 */
data class MetroPalette(val accent: Color = Color(0xFF53E0CF), val glow: Float = .45f) {
    val background = Color(0xFF090C11)
    val surface = Color(0xFF121821)
    val raised = Color(0xFF1B2430)
    val border = Color(0xFF344151)
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
    val accent = when (settings.accentColor) {
        "BLUE" -> Color(0xFF75B7FF)
        "VIOLET" -> Color(0xFFB6A0FF)
        "PINK" -> Color(0xFFFF91BE)
        "AMBER" -> Color(0xFFFFCA70)
        "GREEN" -> Color(0xFF9DDF84)
        else -> Color(0xFF53E0CF)
    }
    val palette = MetroPalette(accent, settings.glowIntensity.coerceIn(0, 100) / 100f)
    CompositionLocalProvider(LocalMetroPalette provides palette) {
        MaterialTheme(colorScheme = darkColorScheme(
            primary = accent, onPrimary = palette.background,
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
            for (step in 1..6) {
                val spread = step.dp.toPx()
                drawRect(
                    color = palette.accent.copy(alpha = (7 - step) * .014f * palette.glow),
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
fun platformLabel(name: String): String = when (name) {
    "HEORIC_GAMES_LAUCHER" -> "Heroic"
    "PCSX2" -> "PlayStation 2"
    "MELONDS" -> "Nintendo DS"
    "DOLPHIN" -> "GameCube / Wii"
    "PS3" -> "PlayStation 3"
    "AZAHAR" -> "Nintendo 3DS"
    "DUCKSTATION" -> "PlayStation"
    else -> name
}
