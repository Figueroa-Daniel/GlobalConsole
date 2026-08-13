# 05. Contexto Continuo para la IA

Este documento actúa como memoria continua y estado del proyecto para las sesiones de trabajo de la IA en GlobalConsole.

---

## 📍 1. Estado Actual

### Implementado
- **Estructura del Proyecto:** Módulo multiplataforma `shared` y ejecutable JVM de escritorio `desktopApp`.
- **Lanzador PCSX2 (Capa de Datos):**
  - `GameP2FileSystemAdapter`: Escaneo local de ISOs de PS2 en base a un directorio raíz y eliminación física de archivos en Windows y Linux.
  - `GamePCSX2Adapter`: Lanzamiento del comando nativo a pantalla completa y sin interfaz gráfica (`-fullscreen` y `-nogui`) en Windows y Linux (mediante Flatpak).
  - `GameP2Repository` e implementación `GameP2RepositoryImpl`: Repositorio con sistema de caché en memoria y mapeo a entidades de dominio.
- **Entidades de Dominio:** `Game`, `Platforms` (enum) y `GameP2`.
- **Casos de Uso del Dominio (PCSX2):**
  - Implementación de `GetGamesP2UseCase`, `ExecuteGameP2UseCase`, `DeleteGameP2UseCase` y `SearchGamesP2UseCase` bajo `domain.usecase`.
  - `GetGamesP2UseCase` declarada `open` para permitir Fakes en tests.
  - Pruebas unitarias completas bajo TDD.
- **Capa de Presentación (Completa):**
  - `HomeScreen.kt`: Pantalla principal de biblioteca de juegos con estética Metro Noir (oscura, blanco/negro, brillos).
  - `MetroTopBar.kt`: Barra superior con logo y búsqueda integrada.
  - `GameTile.kt`: Tarjeta de juego para la cuadrícula.
  - `SetupPathDialog.kt`: Diálogo para configurar la ruta de la carpeta de ISOs (ruta guardada en RAM mientras dure la sesión).
  - `HomeViewModel.kt` con estados `Loading`, `Success`, `Empty`, `Error` y `GameRunning` en `HomeUiState`.
- **Navegación (Compose Multiplatform Navigation):**
  - `AppRoutes.kt`: Rutas type-safe con `@Serializable`.
  - `AppNavHost.kt`: Grafo de navegación con `NavHost`.
  - `main.kt`: Inyección manual de dependencias + `rememberNavController()` + `AppNavHost`.
- **Soporte de Gamepad Nativo (LWJGL 3 - GLFW):**
  - `GamepadEvent.kt`: Eventos de botones y direcciones de crucetas o sticks analógicos.
  - `GamepadManager.kt`: Ciclo de consulta activa (polling) a través de coroutines que publica eventos de entrada nativos filtrados. Incluye control de ratón nativo (stick derecho) de forma global para todos los monitores del SO, e implementa un sistema de suspensión reactiva.
  - Integrado de forma reactiva en `HomeScreen.kt` y `GameTile.kt`. La navegación es por índices para confinar el foco a la cuadrícula, y el movimiento libre del ratón (hover) se sincroniza en tiempo real con el foco interno de Compose.
- **Tests:**
  - `HomeViewModelTest.kt`: 6 pruebas de integración del ViewModel con Fakes.
  - `FakeGetGamesP2UseCase.kt` + `FakeGameP2Repository.kt` para aislar la capa de datos.
  - `SaveEmulatorPathUseCaseTest.kt` + `GetEmulatorPathUseCaseTest.kt`: 7 pruebas TDD para los UseCases de configuración.
  - `FakeSettingsRepository.kt` para aislar tests del disco.
- **Persistencia de Configuración (Completa):**
  - `SettingsRepository.kt`: Interfaz de dominio para gestión de rutas.
  - `SettingsRepositoryImpl.kt`: Persistencia en `config.json` con `kotlinx.serialization`.
  - `SaveEmulatorPathUseCase.kt` y `GetEmulatorPathUseCase.kt`: Casos de uso con validación.
  - `SettingsUiState.kt` y `SettingsViewModel.kt`: Estado y lógica del diálogo de configuración.
  - `SetupPathDialog.kt`: Actualizado con navegación por gamepad (D-Pad entre botones) y conexión al SettingsViewModel.
  - `GameP2FileSystemAdapter.kt`: Refactorizado para obtener la ruta desde `GetEmulatorPathUseCase` (no más variables globales).
  - Módulos Koin actualizados: `DataModule`, `DomainModule`, `PresentationModule`.
- **Estructura de Reglas de Agente:** Migrado a `.agents/rules/` con punto de entrada `AGENTS.md`.

---

## 📋 2. Próximos Pasos (Pendientes)

1. **Corrección del Bug de Borrado (ver `06_analisis_errores_data.md`):**
   - En `GameP2RepositoryImpl.deleteGameP2()`, pasar directamente el `id` al adaptador en lugar del nombre.
2. **Nuevas Pantallas de Navegación:**
   - Pantalla de detalle de juego (`GameDetailRoute(gameId: String)`).
   - Pantalla de configuración (`SettingsRoute`).
   - Añadir las rutas en `AppRoutes.kt` y los composables en `AppNavHost.kt`.
3. **Eliminar `SettingsPlatforms.kt`:** La variable global ya está deprecada. Eliminar cuando se confirme que ningún otro módulo la referencia.

---

## 🔗 3. Referencias Cruzadas
- Arquitectura Clean: [01_arquitectura.md](01_arquitectura.md).
- Tecnologías: [02_tecnologias.md](02_tecnologias.md).
- Módulos del proyecto: [03_modulos.md](03_modulos.md).
- Errores de Datos: [06_analisis_errores_data.md](06_analisis_errores_data.md).
- Persistencia de Configuración: [08_persistencia_configuracion.md](08_persistencia_configuracion.md).
