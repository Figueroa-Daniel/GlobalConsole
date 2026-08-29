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

Para las acciones del mando que aplican al "elemento actual" (ej. presionar botón Y/Triángulo para editar el tile actual), se debe verificar qué modo está activo y usar el índice correspondiente.
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

## Atajos en Estado Suspendido
Cuando se ejecuta un juego externo o un launcher (ej. RPCS3, MelonDS), la lectura general del mando se "suspende" para que la entrada vaya al emulador. En este estado suspendido, `GamepadManager` escucha combinaciones especiales críticas:
- **`START + SELECT (BACK)`**: Comando universal de cierre. Obliga a GlobalConsole a matar el proceso del juego/emulador y volver al menú principal.
- **`X + SELECT (BACK)`**: Alternador manual del modo ratón (`isMouseAllowedWhenSuspended`). Si se desactiva por esta vía, el cursor físico se transporta a `(0, 0)` para quedar oculto fuera de la vista principal del juego. Resulta útil en emuladores que requieren ratón en la interfaz pero donde molesta durante el gameplay.
