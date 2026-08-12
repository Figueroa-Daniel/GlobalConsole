package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso de dominio encargado de persistir la preferencia del usuario
 * sobre si Heroic Games Launcher debe aparecer en la biblioteca principal.
 *
 * La clase es [open] para permitir la creación de implementaciones Fake en los
 * tests unitarios sin necesidad de frameworks de mocking.
 *
 * @property repository Repositorio de configuración del proyecto.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-12
 */
open class SaveHeroicEnabledUseCase(
    private val repository: SettingsRepository
) {

    /**
     * Persiste la preferencia del usuario sobre la visibilidad de Heroic Games Launcher.
     *
     * @param enabled True para mostrar el launcher en la biblioteca, false para ocultarlo.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    open suspend operator fun invoke(enabled: Boolean) = withContext(Dispatchers.IO) {
        repository.saveHeroicEnabled(enabled)
    }
}
