package org.example.globalconsole.HeroicGames.domain.usecase

import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository

/**
 * Caso de uso encargado de cerrar forzosamente el Heroic Games Launcher.
 *
 * @property repository Repositorio del launcher Heroic Games.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-15
 */
class CloseHGLauncherUseCase(private val repository: HGLauncherRepository) {
    /**
     * Ejecuta la acción de cierre.
     *
     * @return True si se cerró exitosamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-15
     */
    suspend operator fun invoke(): Boolean {
        return repository.closeLauncher()
    }
}
