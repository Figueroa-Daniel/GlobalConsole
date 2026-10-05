package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.globalconsole.presesentation.input.InputMode

/**
 * Acción plana de cabecera con selección subrayada y superficie táctil de 48 dp.
 * @param label Nombre visible de la acción.
 * @param onClick Acción asociada.
 * @param selected Foco lógico de navegación.
 * @param inputMode Prioridad entre mando y sobrevuelo.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-05
 */
@Composable
fun MetroBarAction(label: String, onClick: () -> Unit, selected: Boolean, inputMode: InputMode) {
    val palette = LocalMetroPalette.current
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    val active = if (inputMode == InputMode.MOUSE) hovered else selected
    Box(Modifier.height(MetroLayout.controlHeight)
        .background(if (active) palette.selection.copy(alpha = .12f) else Color.Transparent)
        .drawBehind {
            if (active) drawLine(palette.accent, Offset(12.dp.toPx(), size.height - 2.dp.toPx()),
                Offset(size.width - 12.dp.toPx(), size.height - 2.dp.toPx()), strokeWidth = 2.dp.toPx())
        }
        .hoverable(interactions)
        .clickable(interactionSource = interactions, indication = null, onClick = onClick)
        .padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
        Text(label, color = if (active) palette.accent else palette.muted,
            fontSize = 14.sp, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium)
    }
}
