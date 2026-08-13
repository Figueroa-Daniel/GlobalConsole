package org.example.globalconsole.presesentation.viewModel.home.fakes

import org.example.globalconsole.HeroicGames.domain.entitys.HGLauncher
import org.example.globalconsole.HeroicGames.domain.usecase.ShowHGLauncherUseCase
import org.example.globalconsole.HeroicGames.domain.usecase.fakes.FakeHGLauncherRepository
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Implementación fake de [ShowHGLauncherUseCase] para pruebas unitarias del ViewModel.
 * Retorna siempre una instancia estática de [HGLauncher] con datos de prueba conocidos.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class FakeShowHGLauncherUseCase : ShowHGLauncherUseCase(repository = FakeHGLauncherRepository()) {

    /**
     * Retorna un [HGLauncher] con datos de prueba predefinidos.
     *
     * @return Instancia estática de [HGLauncher] para verificación en tests.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun invoke(): HGLauncher = HGLauncher(
        id = "heroic-launcher",
        name = "Heroic Games",
        urlGameExecute = "com.heroicgameslauncher.hgl",
        platform = Platforms.HEORIC_GAMES_LAUCHER
    )
}
