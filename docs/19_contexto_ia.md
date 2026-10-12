# 05. Contexto Continuo para la IA

Este documento actúa como memoria continua y estado del proyecto para las sesiones de trabajo de la IA en GlobalConsole.

---

## 📍 1. Estado Actual

### Implementado
- **Metro personalizable:** tema oscuro común, quince acentos (incluidos rojo, marino y tres morados neón) y halo 0–100 guardados en `config.json`; editor de vista navegable por filas, teclado español con lógica en UseCase y mejoras de foco en diálogos. Biblioteca con cabecera adaptable y buscador limitado a 380 dp, acciones planas subrayadas, imagen de PlayStation ampliada, tarjetas verticales, títulos separados e iluminación ambiental regulable; 90 pruebas superadas y revisión gráfica con eventos de mando simulados. Ver [21_metro_personalizable.md](21_metro_personalizable.md).
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
  - `GamepadOSK.kt`: Teclado en pantalla nativo (On-Screen Keyboard), navegable 100% por gamepad para búsquedas reactivas.
  - `HomeViewModel.kt` con estados `Loading`, `Success`, `Empty`, `Error` y `GameRunning` en `HomeUiState`.
- **Navegación (Compose Multiplatform Navigation):**
  - `AppRoutes.kt`: Rutas type-safe con `@Serializable`.
  - `AppNavHost.kt`: Grafo de navegación con `NavHost`.
  - `main.kt`: Inyección manual de dependencias + `rememberNavController()` + `AppNavHost`.
- **Soporte de Gamepad Nativo (LWJGL 3 - GLFW):**
  - `GamepadEvent.kt`: Eventos de botones y direcciones de crucetas o sticks analógicos. Añadido evento `DELETE` (Botón X/Cuadrado).
  - `GamepadManager.kt`: Ciclo de consulta activa (polling) a través de coroutines que publica eventos de entrada nativos filtrados. Incluye control de ratón nativo (stick derecho) de forma global para todos los monitores del SO, e implementa un sistema de suspensión reactiva. La sensibilidad del ratón es configurable por el usuario mediante un `StateFlow`.
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
- **Persistencia de la Vista:** Agrupación y posición de launchers se guardan en `config.json` (`GetViewSettingsUseCase` / `SaveViewSettingsUseCase`). Ver [20_persistencia_vista.md](20_persistencia_vista.md).
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

## 4. Juegos locales (2026-10-11)

Implementado `localGames` en `feature/local-games`: detección Linux/Windows,
preferencias `enabledLocalGames`, siete casos de uso, Koin, biblioteca y diálogo
ADD Local Game. El diálogo base suspende su colector mientras la gestión local
está abierta; el cierre y cancelación solo afectan al árbol de procesos iniciado.
Validación: 110 pruebas, revisión gráfica X11 y recorrido de ejecución con procesos
temporales. El escaneo real Linux detectó 4 juegos. Windows y mando físico quedan
pendientes de validación. Ver [Juegos locales](23_juegos_locales.md).

## 5. Aplicaciones locales (2026-10-11)

Implementado `localApps` en `feature/local-apps`: detección Linux/Windows,
selección `enabledLocalApps`, repositorio de dominio y ocho casos de uso
(incluida clasificación). Ajustes → Aplicaciones → Añadir aplicación permite
habilitar u ocultar herramientas; aparecen en su propia categoría de biblioteca.
Se excluyen juegos, emuladores, navegadores y gestores de juegos conocidos.

Juegos y aplicaciones comparten lectura de accesos, resolución de iconos,
control de procesos y bloqueo de preferencias. Conservadas las mejoras previas
de juegos (Bottles/Citra e iconos reverse-DNS). Navegación de mando del diálogo
hijo aislada del padre; ratón/OSK permitidos durante una aplicación local.
Validación: 120 pruebas correctas y recorrido gráfico con configuración temporal,
selección, desplazamiento, reapertura, lanzamiento, cierre y retorno a biblioteca.
Windows real, mando físico y OSK en programas externos requieren comprobación
en hardware. Detalles en [Aplicaciones locales](24_aplicaciones_locales.md).

## 6. Apertura exclusiva del teclado externo (2026-10-12)

`OskShortcutUseCase` solo alterna con START + R2. Se eliminan R3 y START + R1
como alternativas. Se conserva la apertura por clic sobre texto (ratón o A),
restaurada con `OpenOskOnTextClickUseCase`; el doble A fuera de texto no abre.
`OskRightTriggerUseCase` selecciona el eje 4 para el GUID HJDX documentado y el
5 para mandos estándar: L2 deja de aceptarse como equivalente de R2. Se mantiene
la histéresis, confirmación con START y cierre con B. Pruebas de casos de uso
verificadas; pendiente comprobación física. Ver [Teclado](15_busqueda_y_osk.md).

## 7. Cursor externo desde el teclado flotante (2026-10-12)

Con el OSK abierto sobre una aplicación externa, L1/R1 envían flechas
izquierda/derecha al campo que conserva el foco. La selección de teclas sigue
en la cruceta y el stick izquierdo. `OskExternalCursorUseCase` detecta flancos y
`GamepadManager` inyecta las flechas; el control se desactiva fuera de ese
contexto. Véase [Teclado en pantalla](15_busqueda_y_osk.md).

## 8. Texto pendiente del OSK con L2/R2 (2026-10-12)

El teclado flotante mantiene un cursor propio en el texto que aún no se ha
enviado. L2/R2 lo desplazan por caracteres Unicode completos; las teclas se
insertan en el cursor y borrar elimina el carácter anterior. L1/R1 conservan
el control del campo externo, mientras la cruceta y el stick seleccionan teclas.
`OskTextTriggerUseCase` detecta flancos y cede prioridad a START + R2.
Véase [Teclado en pantalla](15_busqueda_y_osk.md).
