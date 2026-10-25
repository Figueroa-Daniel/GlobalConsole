# Manejo del Gamepad y Foco en GlobalConsole

GlobalConsole utiliza un sistema híbrido de entrada que permite a los usuarios interactuar tanto con el teclado/ratón tradicionales como con un mando físico usando la biblioteca GLFW (a través de Lwjgl).

## GamepadManager
El `GamepadManager` es el núcleo del sistema, que ejecuta un ciclo de consulta (`polling`) en una corutina dedicada. Transforma los eventos de botones y palancas en flujos de datos (`StateFlow` y `SharedFlow`) de Compose.

### Modos de Entrada
Existen dos modos de entrada (`InputMode`):
1. **GAMEPAD**: Navegación por D-Pad o Stick Izquierdo. El foco de la interfaz cambia entre los elementos lógicos usando índices.
2. **MOUSE**: Movimiento mediante el Stick Derecho, que emula el movimiento nativo del cursor del ratón (estilo PS4 Remote Play). 

## Manejo de Focos e Índices
Debido al sistema híbrido, un componente UI como un `GameTile` puede ser "seleccionado" de dos maneras:
- Si el modo es `GAMEPAD`, el elemento está seleccionado si posee el índice lógico (ej. `focusedGameIndex`).
- Si el modo es `MOUSE`, el elemento está seleccionado si el ratón se encuentra encima de él (`isHovered` verdadero en Compose).

Para las acciones del mando que aplican al "elemento actual" (ej. presionar botón X/cuadrado para editar el tile actual), se debe verificar qué modo está activo y usar el índice correspondiente.
- Se debe rastrear `focusedGameIndex` escuchando eventos de navegación del mando.
- Se debe rastrear `hoveredGameIndex` añadiendo `onHover` a los tiles y actualizando un estado local.

## Prevención de Fugas de Eventos (Event Leaking)
Al abrir ventanas modales o diálogos emergentes (como el recortador de imágenes o teclado en pantalla OSK), el `GamepadManager` sigue enviando eventos de manera global. 

Para evitar que el componente base (como `HomeScreen`) procese acciones (por ejemplo, abrir un juego) mientras interactuamos con el diálogo modal, se deben aislar los eventos:
1. El `LaunchedEffect` del `HomeScreen` encargado de recolectar los eventos del gamepad DEBE incluir el estado de visibilidad del diálogo en su lista de `keys`.
2. Se debe agregar una condición de `early return` (retorno anticipado) al inicio del `LaunchedEffect` si el diálogo está abierto.

Ejemplo:
```kotlin
// IMPORTANTE: cropTargetGame != null actúa como key para reiniciar el efecto.
LaunchedEffect(gamepadManager, games.size, gridColumns, cropTargetGame != null) {
    // Si el diálogo está abierto, no recolectar eventos aquí.
    if (cropTargetGame != null) return@LaunchedEffect 
    gamepadManager?.events?.collectLatest { event ->
        // Procesamiento normal del menú principal...
    }
}
```
Esto garantiza que los inputs del usuario solo afecten al elemento activo superior y no atraviesen hacia el fondo.

## Eventos del teclado flotante (`oskEvents`)
Mientras el teclado flotante está visible, `GamepadManager` emite sus eventos
(cruceta, A, X, Y, L2/R2 de texto y confirmación con START) por `oskEvents` y no
por `events`. Así ninguna pantalla base, ni siquiera con GlobalConsole en primer
plano, procesa la pulsación que pertenece al teclado: es la aplicación de la
regla de fuga de eventos a una ventana que no es un diálogo de Compose.
`OskOverlay` recoge `oskEvents`; el `GamepadOSK` modal de la búsqueda interna
sigue usando `events`. Al abrir o cerrar el teclado se sincronizan los flancos
de todos los botones para que ninguno atraviese hacia la vista de fondo. Véase
[Búsqueda y OSK](15_busqueda_y_osk.md).

## Navegación en Vista Agrupada
Con agrupación activa (`GroupingMode.PLATFORM` / `CONSOLE_SERIES`) la lista contiene `Header` intercalados con tiles. Para que el mando navegue correctamente:
1. **Columnas reales:** `gridColumns` se calcula con la misma fórmula que `GridCells.Adaptive`: `(ancho + espaciado) / (mínimo + espaciado)`. Ignorar el espaciado desalinea las filas lógicas respecto a las visuales.
2. **Keys del efecto:** el `LaunchedEffect` del mando usa `state.items` (no solo `size`) como key; al reagrupar con el mismo número de elementos, de lo contrario se usaría una lista obsoleta.
3. **Ancla de hover:** `onHover` actualiza también `focusedGameIndex`, de modo que al pasar de ratón a mando la navegación parte del tile sobrevolado.
4. **Foco seguro:** si el tile destino no está compuesto (fuera de pantalla) se hace `scrollToItem` antes de `requestFocus`, protegido con `runCatching` para no cancelar el colector de eventos.

Ver también [Arquitectura](01_arquitectura.md) y [Búsqueda y OSK](15_busqueda_y_osk.md).

## Atajos en Estado Suspendido
Cuando se ejecuta un juego externo o un launcher (ej. RPCS3, MelonDS), la lectura general del mando se "suspende" para que la entrada vaya al emulador. En este estado suspendido, `GamepadManager` escucha combinaciones especiales críticas:
- **`START + SELECT (BACK)`**: Comando universal de cierre. Obliga a GlobalConsole a matar el proceso del juego/emulador y volver al menú principal.
- **`X + SELECT (BACK)`**: Alternador manual del modo ratón (`isMouseAllowedWhenSuspended`). Si se desactiva por esta vía, el cursor físico se transporta a `(0, 0)` para quedar oculto fuera de la vista principal del juego. Resulta útil en emuladores que requieren ratón en la interfaz pero donde molesta durante el gameplay.

## Ciclo de Vida de los Launchers de Emuladores
El estado `HomeUiState.GameRunning` (que suspende el mando y bloquea la UI) dura lo que tarda en retornar el `executeLauncher()` del UseCase. Por ello, los adaptadores de launcher (`LauncherMelonDSAdapter`, `LauncherDolphinAdapter`, `LauncherAzaharAdapter`, `LauncherDuckStationAdapter`) **deben bloquear con `process.waitFor()`** (siempre bajo `Dispatchers.IO` en el repositorio) hasta que el emulador se cierre, igual que los adaptadores de juegos y Heroic/PS3. Si retornan al instante, la aplicación se desbloquea mientras el emulador sigue abierto. Al cerrarse el proceso, el ViewModel vuelve a cargar la biblioteca y reactiva el mando.

**Ratón en launchers:** mientras un launcher está en ejecución, `HomeScreen` activa `isMouseAllowedWhenSuspended` para cualquier juego cuyo id contenga `-launcher` (y para MelonDS, PS3 y Heroic), de modo que el stick derecho siga moviendo el cursor dentro del emulador. Se puede alternar con `X + SELECT`.

Ver también [05_melonds.md](05_melonds.md), [16_dolphin.md](16_dolphin.md), [17_duckstation.md](17_duckstation.md) y [18_azahar_3ds.md](18_azahar_3ds.md).
