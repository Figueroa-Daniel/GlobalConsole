package org.example.globalconsole.presesentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.hoverable

/**
 * Barra de navegación superior estilo Metro.
 * Contiene el título principal de la aplicación, el buscador minimalista y accesos rápidos.
 *
 * @param searchQuery Búsqueda de texto actual.
 * @param onSearchChanged Callback invocado al escribir en el buscador.
 * @param onRefreshClick Callback invocado al pulsar el botón de recarga.
 * @param onSettingsClick Callback invocado al pulsar el botón de configuración de ruta.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-09
 */
@Composable
fun MetroTopBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onRefreshClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F0F0F))
            .drawBehind {
                // Dibujar línea inferior
                drawLine(
                    color = Color(0xFF222222),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Título Estilo Metro
        Text(
            text = "GLOBAL // CONSOLE",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(end = 32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Caja de búsqueda Metro
        Row(
            modifier = Modifier
                .width(300.dp)
                .background(Color(0xFF161616))
                .border(1.dp, Color(0xFF444444), RectangleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "BUSCAR JUEGO...",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Botón Actualizar
        MetroButton(
            text = "RECARGAR",
            onClick = onRefreshClick
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Botón Ajustes
        MetroButton(
            text = "CONFIGURACIÓN",
            onClick = onSettingsClick
        )
    }
}
