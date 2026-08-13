package org.example.globalconsole.presesentation.viewModel.home.fakes

import org.example.globalconsole.HeroicGames.domain.usecase.FindHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.fakes.FakeHGLauncherRepository

/**
 * Implementación fake de [FindHGLauncherUseCase] para pruebas unitarias del ViewModel.
 * Permite controlar el resultado de la consulta de visibilidad de Heroic Games Launcher.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class FakeFindHGLauncherUseCase : FindHGLauncherUseCase(repository = FakeHGLauncherRepository()) {

    /** Valor que devolverá la invocación del UseCase. Falso por defecto. */
    var heroicEnabled: Boolean = false

    /**
     * Retorna el valor configurado en [heroicEnabled].
     *
     * @return True si Heroic está habilitado en el fake.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun invoke(): Boolean = heroicEnabled
}
