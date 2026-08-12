package org.example.globalconsole.HeroicGames.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.HeroicGames.data.database.LauncherHeroicGamesAdapter

/**
 * Caso de uso de dominio encargado de invocar la ejecución de Heroic Games Launcher.
 * Delega la lógica de sistema al adaptador de la capa de datos y asegura que
 * la operación se realice fuera del hilo principal mediante [Dispatchers.IO].
 *
 * La clase es [open] para permitir la creación de implementaciones Fake en los tests unitarios
 * sin necesidad de frameworks de mocking.
 *
 * @property adapter Adaptador responsable de la ejecución nativa del proceso.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-12
 */
open class ExecuteHGLauncherUseCase(
    private val adapter: LauncherHeroicGamesAdapter
) {

    /**
     * Ejecuta Heroic Games Launcher lanzando el proceso nativo en el hilo de I/O.
     *
     * @return True si el launcher se inició y cerró correctamente, false en caso de error
     *         o si el launcher no está instalado en el sistema.
     * @throws Exception Si se produce un error inesperado al iniciar el proceso.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        return@withContext adapter.executeLauncher()
    }
}