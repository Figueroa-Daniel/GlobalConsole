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

## 9. Acceso rápido y ajustes (2026-10-12)

En `feature/mejora-usabilidad-vista`, Y/triángulo enfoca Buscar en la biblioteca
(incluidos carga, vacío y error), Guardar en ajustes, Cerrar en Vista/Navegadores/
Juegos/Aplicaciones y Seleccionar en carpetas. No ejecuta la acción; A confirma.
X/cuadrado conserva el recorte de la carátula, respetando el modo de entrada.
La configuración presenta opciones compactas, encabezados, columnas con scroll
independiente y pie fijo. Las opciones siguen aplicándose al momento y las rutas
al guardar. No cambia el teclado virtual ni el procesamiento físico del mando.
Ventana temporal Xvfb: comprobados Y, Y+izquierda+A, cierres y aislamiento del
modal hijo. Validación gráfica a 1280×900 y 1000×700; suite de UseCases sin fallos.
Pendiente únicamente comprobación física con el mando del usuario.
Véase [usabilidad de configuración](25_usabilidad_configuracion.md).

## 10. Revisión del teclado (2026-10-12)

Resumen de `bugfix/teclado-virtual-foco`, ya en `dev`: el atajo del teclado se
evalúa también con GlobalConsole en primer plano; sus eventos viajan por
`oskEvents`; R2 usa histéresis (`TriggerPressUseCase`) y el mapeo del mando HJDX
se resuelve por GUID; borrar con el búfer vacío envía Retroceso al campo externo.
El registro temporal `[OSK-DIAG]` de `GamepadManager` sigue activo; las
decisiones intermedias de `15_busqueda_y_osk.md` (apartados 6 y 8.1) quedan
marcadas como superadas por el apartado 9. Pendiente: comprobación con mando
físico de los apartados 9 a 12 y de la usabilidad del apartado 9 de este archivo.
Véase
[Teclado](15_busqueda_y_osk.md) y [Mando y foco](12_manejo_gamepad_y_foco.md).


## 11. Guías de uso (2026-10-12)

Nueva carpeta `docs/guias/` con la documentación de uso (no técnica): instalación
de emuladores por Flatpak, carpetas de juegos por emulador, mando, biblioteca y
ajustes, emuladores, teclado en pantalla y solución de problemas. Índice en
[guias/README.md](guias/README.md). Las guías se redactaron a partir del código
actual; los datos que deben mantenerse al cambiar el código son los
identificadores Flatpak, las extensiones admitidas por emulador, los comandos de
lanzamiento y los atajos del mando.

Los hallazgos iniciales de esta revisión se comprobaron y corrigieron en el
apartado 12. El README raíz no existía en la revisión de código (no era un
archivo vacío). Las guías ya reflejan el comportamiento corregido.

## 12. Rutas de emuladores y biblioteca (2026-10-12)

Rama `bugfix/rutas-emuladores-y-biblioteca`, creada desde `dev`. Se conservan fuera
de los commits los cambios previos de `config.json` y `GamepadManager.kt`.

- Ejecutables persistidos mediante claves `executable.<id>` en `emulatorPaths`.
  Ajustes ofrece selección de archivos con mando, Automático y Guardar explícito.
  `BuildEmulatorCommandUseCase` resuelve las rutas al lanzar: binario configurado,
  Flatpak en Linux o nombre en PATH en Windows. Sin ruta personal de DuckStation
  ni dependencia de la carpeta de ROMs de PCSX2. RPCS3 admite lanzamiento Windows.
- DuckStation omite únicamente pistas BIN/IMG referenciadas por un CUE y mantiene
  juegos independientes. Todos los escáneres de ROMs asocian carátulas por nombre;
  las genéricas solo se usan si hay un juego en la carpeta. Recortes individuales
  `<juego>.cover.png` con prioridad.
- Eliminada la acción de borrado sin consumidores en HomeViewModel. La operación
  aún no implementada de DuckStation devuelve false, no un éxito ficticio.
- README raíz creado y guías actualizadas.

Validación: 144 pruebas de UseCases sin fallos (9 nuevas). Incluyen persistencia,
comandos Linux/Windows, escaneo de archivos temporales, selección de carátulas y
un proceso Linux ficticio con ruta que contiene espacios y salida 0/7. Ventana
Xvfb revisada: selector de archivos, guardado explícito, recarga y aislamiento
del modal padre. No se lanzaron emuladores reales ni se probó Windows/mando físico.
Véase [revisión técnica](26_revision_emuladores_y_biblioteca.md).
