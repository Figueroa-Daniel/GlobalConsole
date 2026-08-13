package org.example.globalconsole.HeroicGames.domain.usecase.fakes

import org.example.globalconsole.HeroicGames.data.dto.HGLauncherDto
import org.example.globalconsole.HeroicGames.data.repository.HGLauncherRepository

/**
 * Implementación fake de [HGLauncherRepository] para uso exclusivo en pruebas unitarias.
 * Permite controlar el estado de visibilidad del launcher y simular errores
 * sin recurrir a la capa de datos real.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-13
 */
class FakeHGLauncherRepository : HGLauncherRepository {

    /** Controla el resultado de [isHeroicEnabled]. Falso por defecto. */
    var heroicEnabled: Boolean = false

    /** Si es true, [hideHGLauncher] retornará false simulando un fallo. */
    var shouldHideFail: Boolean = false

    /**
     * No implementado en el fake; lanza [UnsupportedOperationException].
     *
     * @return Siempre lanza excepción.
     * @throws UnsupportedOperationException en todos los casos.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun isHGLauncherInstalled(): Boolean {
        throw UnsupportedOperationException("No implementado en el fake")
    }

    /**
     * Simula la ocultación del launcher estableciendo [heroicEnabled] a false.
     *
     * @return True si [shouldHideFail] es false, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun hideHGLauncher(): Boolean {
        if (shouldHideFail) return false
        heroicEnabled = false
        return true
    }

    /**
     * Retorna un [HGLauncherDto] estático con los datos del launcher.
     *
     * @return [HGLauncherDto] con datos de prueba.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun showHGLauncher(): HGLauncherDto = HGLauncherDto(
        id = "heroic-launcher",
        name = "Heroic Games",
        urlGameExecute = "com.heroicgameslauncher.hgl"
    )

    /**
     * No implementado en el fake; lanza [UnsupportedOperationException].
     *
     * @return Siempre lanza excepción.
     * @throws UnsupportedOperationException en todos los casos.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun executeHGLauncher(): Boolean {
        throw UnsupportedOperationException("No implementado en el fake")
    }

    /**
     * Retorna el valor actual de [heroicEnabled].
     *
     * @return True si el launcher está habilitado en el fake.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun isHeroicEnabled(): Boolean = heroicEnabled

    /**
     * Actualiza [heroicEnabled] con el valor recibido.
     *
     * @param enabled True para habilitar el launcher, false para ocultarlo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-13
     */
    override suspend fun saveHeroicEnabled(enabled: Boolean) {
        heroicEnabled = enabled
    }
}
