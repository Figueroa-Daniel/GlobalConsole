package org.example.globalconsole.navigatorWeb.data.repositoryImpl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.globalconsole.config.AppConfig
import org.example.globalconsole.generalDomain.entititys.NavigatorsWeb
import org.example.globalconsole.generalDomain.entititys.Platforms
import org.example.globalconsole.navigatorWeb.data.database.NavigatorAccesAdapter
import org.example.globalconsole.navigatorWeb.data.dto.NavWebObjDto
import org.example.globalconsole.navigatorWeb.data.mappers.toNavWebObj
import org.example.globalconsole.navigatorWeb.data.repository.NavWebRepository
import org.example.globalconsole.navigatorWeb.domain.entitys.NavWebObj
import java.io.File

/**
 * Implementación de [NavWebRepository]. Combina la detección y ejecución nativa de navegadores
 * ([NavigatorAccesAdapter]) con una caché en memoria persistida en `config.json` de los navegadores habilitados por el usuario.
 *
 * Sigue el patrón unificado de persistencia mediante [AppConfig] para no sobreescribir otros ajustes del sistema.
 *
 * @param navigatorAccesAdapter Adaptador nativo de navegadores.
 * @param configFile Archivo de configuración compartido (por defecto `config.json`).
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class NavWebRepositoryImpl(
    private val navigatorAccesAdapter: NavigatorAccesAdapter,
    private val configFile: File = File("config.json")
) : NavWebRepository {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val navigatorsInMemoryCache = mutableListOf<NavWebObjDto>()
    private var isCacheInitialized = false

    /**
     * Carga de manera perezosa los navegadores habilitados guardados previamente en `config.json`,
     * validando que sigan existiendo en el sistema operativo actual.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private suspend fun ensureCacheInitialized() = withContext(Dispatchers.IO) {
        if (!isCacheInitialized) {
            val config = readConfig()
            val installed = navigatorAccesAdapter.isNavigatorsInstalled()
            config.enabledNavigators.forEach { navName ->
                val navEnum = runCatching { NavigatorsWeb.valueOf(navName) }.getOrNull()
                if (navEnum != null && navEnum in installed && navigatorsInMemoryCache.none { it.navigatorType == navEnum }) {
                    val url = runCatching { navigatorAccesAdapter.getUrlExecutionNavigator(navEnum) }.getOrNull()
                    if (url != null) {
                        navigatorsInMemoryCache.add(
                            NavWebObjDto(
                                id = navEnum.name,
                                name = navEnum.name,
                                urlGameExecute = url,
                                image = null,
                                platform = Platforms.NAVWEB,
                                navigatorType = navEnum
                            )
                        )
                    }
                }
            }
            isCacheInitialized = true
        }
    }

    /**
     * Obtiene los tipos de navegador gestionados por la aplicación, es decir, los de la caché
     * (instalados y habilitados). Es la lista que se muestra en la interfaz.
     *
     * @return Lista de [NavigatorsWeb] presentes en la caché.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun isNavigatorInstalled(): List<NavigatorsWeb> {
        ensureCacheInitialized()
        return navigatorsInMemoryCache.map { it.navigatorType }
    }

    /**
     * Obtiene los navegadores realmente instalados en el sistema (ocultos o no),
     * para que la configuración elija cuáles habilitar.
     *
     * @return Lista de [NavigatorsWeb] instalados.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun getInstalledNavigators(): List<NavigatorsWeb> =
        navigatorAccesAdapter.isNavigatorsInstalled()

    /**
     * Oculta un navegador en el adaptador (la interfaz usa habilitar/deshabilitar).
     *
     * @param navigator Navegador a ocultar.
     * @return True si pasó a estar oculto, false si ya lo estaba.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun hideNavigator(navigator: NavigatorsWeb): Boolean =
        navigatorAccesAdapter.hideNavigator(navigator)

    /**
     * Vuelve a mostrar un navegador oculto en el adaptador.
     *
     * @param navigator Navegador a mostrar.
     * @return True si dejó de estar oculto, false si no lo estaba.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun showNavigator(navigator: NavigatorsWeb): Boolean =
        navigatorAccesAdapter.showNavigator(navigator)

    /**
     * Ejecuta el navegador indicado y espera a que se cierre.
     *
     * @param navWebObj Navegador a ejecutar.
     * @return True si el proceso finalizó correctamente, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun executeNavigator(navWebObj: NavWebObj): Boolean =
        navigatorAccesAdapter.executeNavigator(navWebObj.urlGameExecute, navWebObj.navigatorType)

    /**
     * Obtiene los navegadores habilitados, que son los que se muestran en la biblioteca.
     *
     * @return Lista de [NavWebObj] habilitados.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun getNavigatorsWeb(): List<NavWebObj> {
        ensureCacheInitialized()
        return navigatorsInMemoryCache.map { it.toNavWebObj() }
    }

    /**
     * Cierra el navegador en ejecución.
     *
     * @return True si se cerró correctamente, false en caso de error.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun closeNavigator(): Boolean =
        navigatorAccesAdapter.closeNavigator()

    /**
     * Indica si un navegador está habilitado por el usuario.
     *
     * @param navigator Navegador a consultar.
     * @return True si está en la caché de habilitados.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun isEnabledNavigator(navigator: NavigatorsWeb): Boolean {
        ensureCacheInitialized()
        return navigatorsInMemoryCache.any { it.navigatorType == navigator }
    }

    /**
     * Habilita un navegador si está instalado y no estaba ya habilitado,
     * y persiste la preferencia en `config.json`.
     *
     * @param navigator Navegador a habilitar.
     * @return True si se añadió a la caché, false si no está instalado o ya estaba habilitado.
     * @throws IllegalArgumentException Si la combinación SO/navegador no está soportada.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun saveEnabledNavigator(navigator: NavigatorsWeb): Boolean = withContext(Dispatchers.IO) {
        ensureCacheInitialized()
        if (isEnabledNavigator(navigator) || navigator !in navigatorAccesAdapter.isNavigatorsInstalled()) {
            return@withContext false
        }
        val url = navigatorAccesAdapter.getUrlExecutionNavigator(navigator)
        navigatorsInMemoryCache.add(
            NavWebObjDto(
                id = navigator.name,
                name = navigator.name,
                urlGameExecute = url,
                image = null,
                platform = Platforms.NAVWEB,
                navigatorType = navigator
            )
        )
        val current = readConfig()
        val updated = current.copy(enabledNavigators = (current.enabledNavigators + navigator.name).distinct())
        saveConfig(updated)
        true
    }

    /**
     * Deshabilita un navegador eliminándolo de la caché y actualizando `config.json`.
     *
     * @param navigator Navegador a eliminar.
     * @return True si estaba habilitado y se eliminó, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    override suspend fun deleteNavigator(navigator: NavigatorsWeb): Boolean = withContext(Dispatchers.IO) {
        ensureCacheInitialized()
        val removed = navigatorsInMemoryCache.removeAll { it.navigatorType == navigator }
        if (removed) {
            val current = readConfig()
            val updated = current.copy(enabledNavigators = current.enabledNavigators.filter { it != navigator.name })
            saveConfig(updated)
        }
        removed
    }

    /**
     * Lee y deserializa el archivo `config.json` en [AppConfig].
     *
     * @return La configuración actual o una instancia por defecto de [AppConfig].
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun readConfig(): AppConfig {
        if (!configFile.exists()) return AppConfig()
        return try {
            json.decodeFromString<AppConfig>(configFile.readText())
        } catch (e: Exception) {
            AppConfig()
        }
    }

    /**
     * Persiste el objeto [AppConfig] completo en `config.json`.
     *
     * @param config Configuración a guardar.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    private fun saveConfig(config: AppConfig) {
        try {
            configFile.writeText(json.encodeToString(config))
        } catch (e: Exception) {
            System.err.println("Error al persistir configuración de navegadores: ${e.message}")
        }
    }
}