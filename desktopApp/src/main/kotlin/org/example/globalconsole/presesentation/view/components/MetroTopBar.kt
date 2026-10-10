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
import org.jetbrains.compose.resources.stringResource
import globalconsole.shared.generated.resources.*
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
 * @param compact Reduce la cabecera en ventanas pequeñas.
 * @param itemCount Número de elementos visibles.
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
    compact: Boolean = false,
    itemCount: Int = 0,
    contentMargin: Dp = MetroLayout.compactMargin
) {
    val palette = LocalMetroPalette.current
    Column(Modifier.fillMaxWidth()
        .padding(horizontal = contentMargin, vertical = if (compact) 16.dp else 24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(2) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(2) { col -> Box(Modifier.size(10.dp).background(
                            palette.selection.copy(alpha = if (row == col) 1f else .45f))) }
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(stringResource(Res.string.header_brand), color = palette.text, fontSize = 15.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.weight(1f))
            Text(stringResource(Res.string.header_item_count, itemCount), color = palette.accent, fontSize = 11.sp, letterSpacing = 2.sp)
        }
        if (!compact) {
            Spacer(Modifier.height(24.dp))
            Text(stringResource(Res.string.header_library_title), color = palette.text, fontSize = 40.sp,
                fontWeight = FontWeight.Light, letterSpacing = (-1).sp)
            Text(stringResource(Res.string.header_library_subtitle), color = palette.muted, fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(Modifier.height(if (compact) 16.dp else 24.dp))
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val searchWidth = minOf(380.dp, (maxWidth - 312.dp).coerceAtLeast(160.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.width(searchWidth).height(MetroLayout.controlHeight)
                    .background(palette.surface).border(1.dp,
                        if (focusedButton == TopBarFocus.SEARCH && inputMode == InputMode.GAMEPAD)
                            palette.accent else palette.border),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f).padding(start = 16.dp), contentAlignment = Alignment.CenterStart) {
                        if (searchQuery.isEmpty()) {
                            Text(stringResource(Res.string.search_placeholder), color = palette.muted, fontSize = 14.sp)
                        }
                        BasicTextField(
                            value = searchQuery, onValueChange = onSearchChanged, singleLine = true,
                            cursorBrush = SolidColor(palette.accent),
                            textStyle = TextStyle(color = palette.text, fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    MetroBarAction(stringResource(Res.string.action_search), onSearchClick, focusedButton == TopBarFocus.SEARCH, inputMode)
                }
                MetroBarAction(stringResource(Res.string.action_refresh), onRefreshClick, focusedButton == TopBarFocus.REFRESH, inputMode)
                MetroBarAction(stringResource(Res.string.action_view), onViewClick, focusedButton == TopBarFocus.VIEW, inputMode)
                MetroBarAction(stringResource(Res.string.action_settings), onSettingsClick, focusedButton == TopBarFocus.SETTINGS, inputMode)
            }
        }
    }
}
