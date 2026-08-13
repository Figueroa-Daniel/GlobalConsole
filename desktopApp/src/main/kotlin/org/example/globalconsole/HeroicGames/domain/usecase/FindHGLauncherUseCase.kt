package org.example.globalconsole.HeroicGames.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository

/**
 * Caso de uso de dominio que consulta si Heroic Games Launcher está habilitado
 * para aparecer en la biblioteca principal de GlobalConsole.
 *
 * Actúa como el punto de entrada único para determinar la visibilidad del launcher,
 * desacoplando al ViewModel de los detalles de persistencia.
 *
 * La clase es [open] para permitir la creación de implementaciones Fake en los
 * tests unitarios sin necesidad de frameworks de mocking.
 *
 * @property repository Repositorio de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
open class FindHGLauncherUseCase(
    private val repository: HGLauncherRepository
) {

    /**
     * Consulta la persistencia y retorna true si Heroic Games Launcher
     * debe mostrarse en la biblioteca principal.
     *
     * @return True si el launcher está habilitado, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        repository.isHeroicEnabled()
    }
}