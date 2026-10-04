package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.globalconsole.presesentation.input.InputMode

/** Destinos de la barra accesibles por cruceta. @author Daniel Figueroa Vidal */
enum class TopBarFocus { NONE, SEARCH, REFRESH, VIEW, SETTINGS }

/**
 * Cabecera Metro con marca, jerarquía de biblioteca y acciones alineadas.
 * @param searchQuery Búsqueda actual.
 * @param onSearchChanged Actualiza la búsqueda con teclado físico.
 * @param focusedButton Acción seleccionada por el mando.
 * @param inputMode Modo de entrada actual.
 * @param onSearchClick Abre el teclado virtual.
 * @param onRefreshClick Recarga los juegos.
 * @param onViewClick Abre la personalización.
 * @param onSettingsClick Abre los ajustes.
 * @param contentMargin Margen compartido con la cuadrícula.
 * @return Unit.
 * @throws Nothing No lanza excepciones.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-04
 */
@Composable
fun MetroTopBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    focusedButton: TopBarFocus = TopBarFocus.NONE,
    inputMode: InputMode = InputMode.GAMEPAD,
    onSearchClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onViewClick: () -> Unit,
    onSettingsClick: () -> Unit,
    contentMargin: Dp = MetroLayout.compactMargin
) {
    val palette = LocalMetroPalette.current
    Column(Modifier.fillMaxWidth().background(palette.surface)
        .padding(horizontal = contentMargin, vertical = 20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(4.dp, 32.dp).background(palette.accent))
            Spacer(Modifier.width(12.dp))
            Text("GLOBAL / CONSOLE", color = palette.text, fontSize = 20.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.weight(1f))
            Text("BIBLIOTECA", color = palette.muted, fontSize = 12.sp, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f).height(MetroLayout.controlHeight)
                .background(palette.background).border(1.dp, palette.border)
                .padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                if (searchQuery.isEmpty()) {
                    Text("Buscar un juego…", color = palette.muted, fontSize = 14.sp)
                }
                BasicTextField(
                    value = searchQuery, onValueChange = onSearchChanged, singleLine = true,
                    cursorBrush = SolidColor(palette.accent),
                    textStyle = TextStyle(color = palette.text, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            MetroButton("BUSCAR", onSearchClick, isFocused = focusedButton == TopBarFocus.SEARCH, inputMode = inputMode)
            MetroButton("RECARGAR", onRefreshClick, isFocused = focusedButton == TopBarFocus.REFRESH, inputMode = inputMode)
            MetroButton("VISTA", onViewClick, isFocused = focusedButton == TopBarFocus.VIEW, inputMode = inputMode)
            MetroButton("AJUSTES", onSettingsClick, isFocused = focusedButton == TopBarFocus.SETTINGS, inputMode = inputMode)
        }
    }
}
