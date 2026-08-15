package org.example.globalconsole.melonDS.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.melonDS.data.database.GameMelonDSAdapter
import org.example.globalconsole.melonDS.data.database.LauncherMelonDSAdapter
import org.example.globalconsole.melonDS.data.repository.MelonDSRepository

/**
 * Implementación del repositorio de Melon DS que delega las operaciones a los adaptadores de la capa de datos.
 *
 * @property launcherAdapter Adaptador para gestionar el launcher de Melon DS.
 * @property gameAdapter Adaptador para gestionar la ejecución de juegos de Melon DS.
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class MelonDSRepositoryImpl(
    private val launcherAdapter: LauncherMelonDSAdapter = LauncherMelonDSAdapter(),
    private val gameAdapter: GameMelonDSAdapter = GameMelonDSAdapter()
): MelonDSRepository {

    override suspend fun executeLauncher(): Boolean = withContext(Dispatchers.IO) {
        launcherAdapter.executeLauncher()
    }

    override suspend fun closeLauncher(): Boolean = withContext(Dispatchers.IO) {
        launcherAdapter.closeLauncher()
    }

    override suspend fun executeGame(executeUrl: String?): Boolean = withContext(Dispatchers.IO) {
        gameAdapter.executeGame(executeUrl)
    }
}