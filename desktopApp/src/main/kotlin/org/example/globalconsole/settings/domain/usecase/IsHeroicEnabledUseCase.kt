package org.example.globalconsole.settings.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.globalconsole.settings.domain.SettingsRepository

/**
 * Caso de uso de dominio encargado de recuperar la preferencia del usuario
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
open class IsHeroicEnabledUseCase(
    private val repository: SettingsRepository
) {

    /**
     * Consulta la persistencia y retorna true si Heroic Games Launcher está habilitado.
     *
     * @return True si el launcher debe mostrarse en la biblioteca, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-12
     */
    open suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        return@withContext repository.isHeroicEnabled()
    }
}
