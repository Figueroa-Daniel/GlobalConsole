package org.example.globalconsole.HeroicGames.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository

/**
 * Caso de uso de dominio que habilita Heroic Games Launcher en la biblioteca principal
 * de GlobalConsole persistiendo la preferencia del usuario.
 *
 * Sigue el principio de responsabilidad única (SRP): este use case gestiona
 * exclusivamente la activación del launcher, mientras que [HideHGLauncherUseCase]
 * gestiona la desactivación.
 *
 * La clase es [open] para permitir la creación de implementaciones Fake en los
 * tests unitarios sin necesidad de frameworks de mocking.
 *
 * @property repository Repositorio de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
open class EnableHGLauncherUseCase(
    private val repository: HGLauncherRepository
) {

    /**
     * Habilita Heroic Games Launcher en la biblioteca principal persistiendo la preferencia.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    open suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repository.saveHeroicEnabled(true)
    }
}
