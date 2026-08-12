package org.example.globalconsole.HeroicGames.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.example.globalconsole.HeroicGames.data.database.LauncherHeroicGamesAdapter

/**
 * Tests unitarios para [ExecuteHGLauncherUseCase].
 *
 * Se utiliza un [FakeHeroicLauncherAdapter] para simular el comportamiento del adaptador
 * sin realizar llamadas reales al sistema operativo, siguiendo el principio de aislamiento
 * de los tests unitarios (TDD).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-12
 */
class ExecuteHGLauncherUseCaseTest {

    /**
     * Implementación falsa de [LauncherHeroicGamesAdapter] que permite controlar
     * el resultado de [executeLauncher] sin interactuar con el sistema operativo real.
     *
     * @property shouldSucceed Determina si la llamada simulada retornará éxito o fallo.
     * @property wasExecuteCalled Registro de si el método fue invocado durante el test.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    private class FakeHeroicLauncherAdapter(
        private val shouldSucceed: Boolean
    ) : LauncherHeroicGamesAdapter() {

        var wasExecuteCalled = false
            private set

        /**
         * Simula la ejecución del launcher registrando la llamada y retornando el resultado configurado.
         *
         * @return El valor de [shouldSucceed] configurado en la construcción del fake.
         * @author Daniel Figueroa Vidal
         * @since 2026-08-12
         */
        override fun executeLauncher(): Boolean {
            wasExecuteCalled = true
            return shouldSucceed
        }
    }

    /**
     * Verifica que el caso de uso retorna `true` cuando el adaptador indica que
     * el lanzamiento del proceso fue exitoso.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun whenLaunchSucceeds_returnsTrue() = runTest {
        val fakeAdapter = FakeHeroicLauncherAdapter(shouldSucceed = true)
        val useCase = ExecuteHGLauncherUseCase(adapter = fakeAdapter)

        val result = useCase()

        assertTrue("El caso de uso debe retornar true cuando el adaptador tiene éxito", result)
    }

    /**
     * Verifica que el caso de uso retorna `false` cuando el adaptador indica que
     * el lanzamiento del proceso falló (launcher no instalado o error de proceso).
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun whenLaunchFails_returnsFalse() = runTest {
        val fakeAdapter = FakeHeroicLauncherAdapter(shouldSucceed = false)
        val useCase = ExecuteHGLauncherUseCase(adapter = fakeAdapter)

        val result = useCase()

        assertFalse("El caso de uso debe retornar false cuando el adaptador reporta fallo", result)
    }

    /**
     * Verifica que el caso de uso invoca al adaptador al ser ejecutado,
     * garantizando que la delegación hacia la capa de datos se produce correctamente.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    @Test
    fun whenInvoked_callsAdapterExecute() = runTest {
        val fakeAdapter = FakeHeroicLauncherAdapter(shouldSucceed = true)
        val useCase = ExecuteHGLauncherUseCase(adapter = fakeAdapter)

        useCase()

        assertTrue("El caso de uso debe invocar al adaptador", fakeAdapter.wasExecuteCalled)
    }
}
