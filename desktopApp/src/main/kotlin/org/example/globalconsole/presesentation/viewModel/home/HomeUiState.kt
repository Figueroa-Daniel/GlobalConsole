package org.example.globalconsole.presesentation.viewModel.home

import org.example.globalconsole.generalDomain.entititys.Game

/**
 * Representa los posibles estados de la UI de la pantalla principal de GlobalConsole.
 * Cada estado encapsula los datos necesarios para renderizar la pantalla de biblioteca de juegos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-05
 */
sealed interface HomeUiState {

    /**
     * Estado inicial: la pantalla está cargando datos de todas las fuentes.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    data object Loading : HomeUiState

    /**
     * Estado de éxito: se han obtenido juegos correctamente.
     *
     * @property games Lista completa de todos los juegos disponibles en todas las fuentes.
     * @property filteredGames Lista de juegos filtrada por la búsqueda activa del usuario.
     *                         Igual a [games] si no hay ninguna búsqueda activa.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    data class Success(
        val games: List<Game>,
        val filteredGames: List<Game>
    ) : HomeUiState

    /**
     * Estado vacío: no hay juegos disponibles en ninguna fuente.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    data object Empty : HomeUiState

    /**
     * Estado de error: ocurrió un fallo al cargar los datos de alguna fuente.
     *
     * @property message Mensaje descriptivo del error ocurrido.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-05
     */
    data class Error(val message: String) : HomeUiState
}
