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
  - `HomeViewModel.kt` con estados `Loading`, `Success`, `Empty`, `Error` en `HomeUiState`.
- **Navegación (Compose Multiplatform Navigation):**
  - `AppRoutes.kt`: Rutas type-safe con `@Serializable`.
  - `AppNavHost.kt`: Grafo de navegación con `NavHost`.
  - `main.kt`: Inyección manual de dependencias + `rememberNavController()` + `AppNavHost`.
- **Soporte de Gamepad Nativo (LWJGL 3 - GLFW):**
  - `GamepadEvent.kt`: Eventos de botones y direcciones de crucetas o sticks analógicos.
  - `GamepadManager.kt`: Ciclo de consulta activa (polling) a través de coroutines que publica eventos de entrada nativos filtrados.
  - `GamepadFocusNavigator.kt`: Traductor e inyector de comandos del gamepad al árbol de foco espacial nativo de Compose Desktop.
  - Integrado de forma reactiva en `HomeScreen.kt` y `GameTile.kt`.
- **Tests:**
  - `HomeViewModelTest.kt`: 6 pruebas de integración del ViewModel con Fakes.
  - `GamepadFocusNavigatorTest.kt`: Test de integración y comportamiento espacial de los botones y direcciones del gamepad.
  - `FakeGetGamesP2UseCase.kt` + `FakeGameP2Repository.kt` para aislar la capa de datos.
- **Estructura de Reglas de Agente:** Configuración bajo la carpeta `.agents/AGENTS.md`.

---

## 📋 2. Próximos Pasos (Pendientes)

1. **Inyección de Dependencias (Koin):**
   - Configurar Koin Multiplatform en `shared` y `desktopApp`.
   - Registrar `GameP2FileSystemAdapter`, `GamePCSX2Adapter` y `GameP2RepositoryImpl`.
2. **Persistencia de Rutas:**
   - La ruta de ISOs (`ROUTE_PCSX2_GAMES` en `SettingsPlatforms.kt`) actualmente se guarda en RAM (variable global `var`).
   - Implementar persistencia real (archivo de configuración en disco o preferencias del sistema).
3. **Nuevas Pantallas de Navegación:**
   - Pantalla de detalle de juego (`GameDetailRoute(gameId: String)`).
   - Pantalla de configuración (`SettingsRoute`).
   - Añadir las rutas en `AppRoutes.kt` y los composables en `AppNavHost.kt`.
4. **Corrección del Bug de Borrado (ver `06_analisis_errores_data.md`):**
   - En `GameP2RepositoryImpl.deleteGameP2()`, pasar directamente el `id` al adaptador en lugar del nombre.

---

## 🔗 3. Referencias Cruzadas
- Arquitectura Clean: [01_arquitectura.md](01_arquitectura.md).
- Tecnologías: [02_tecnologias.md](02_tecnologias.md).
- Módulos del proyecto: [03_modulos.md](03_modulos.md).
- Errores de Datos: [06_analisis_errores_data.md](06_analisis_errores_data.md).
