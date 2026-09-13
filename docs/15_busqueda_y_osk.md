# 09. Búsqueda Reactiva y Teclado en Pantalla (OSK)

Este documento detalla la arquitectura implementada para la funcionalidad de búsqueda dentro de GlobalConsole, la cual ha sido diseñada para ser navegable 100% mediante Gamepad sin requerir el uso de teclado físico ni simulación de ratón.

## 1. Búsqueda Reactiva (`HomeViewModel`)

La lógica de búsqueda reside íntegramente en la capa de presentación dentro del `HomeViewModel`. Se ha optado por un enfoque reactivo utilizando Kotlin `StateFlow`.

### Flujo de Estado
1. El ViewModel mantiene un `MutableStateFlow<String>` llamado `searchQuery`.
2. La lista de juegos cargada de los distintos emuladores reside en memoria dentro del estado `HomeUiState.Success`.
3. Al invocar `onSearchQueryChanged(query: String)`:
   - Se actualiza el `searchQuery`.
   - Se filtra la lista original de juegos reteniendo aquellos cuyo `name` contenga el `query` (case-insensitive).
   - Se emite un nuevo estado `HomeUiState.Success` que incluye tanto la lista completa original (`games`) como la lista resultante (`filteredGames`).
4. La UI (`HomeScreen`) observa estos cambios y repinta automáticamente el grid de juegos usando la lista filtrada.

Esta aproximación evita hacer consultas de disco o I/O continuas mientras el usuario teclea, asegurando cero latencia en la interfaz.

## 2. Teclado Virtual: `GamepadOSK`

Para proveer una experiencia de consola ("10-foot UI"), se descartaron los teclados del sistema operativo por requerir manejo del ratón. En su lugar, se implementó `GamepadOSK`, un componente `@Composable` nativo.

### Diseño de la Cuadrícula
El teclado se estructura como una lista bidimensional (`List<List<String>>`) siguiendo la disposición QWERTY tradicional:
- **Fila 0:** Letras de la fila superior + `⌫` (Backspace).
- **Fila 1:** Letras de la fila central + `↵` (Intro/Dummy).
- **Fila 2:** Letra de la fila inferior + `SHIFT` (Alternancia de capas).
- **Fila 3:** Tecla gigante `ESPACIO`.

### Navegación
La navegación intercepta directamente el `SharedFlow` de `GamepadManager`.
- El teclado mantiene el foco lógico con `focusedRow` y `focusedCol`.
- **Wrap-around:** Moverse a la izquierda en la primera columna lleva al foco a la última columna de esa misma fila, y viceversa. Arriba/Abajo también hacen wrap-around entre filas.
- Las teclas especiales ("ESPACIO", "SHIFT") no rompen el foco por columna porque la lógica del grid confina dinámicamente la columna máxima a la longitud de la fila actual (`layout[focusedRow].size - 1`).

### Integración de Eventos GLFW
- **Direcciones (Cruceta/Stick):** Mueven el cursor (`focusedRow` y `focusedCol`).
- **Botón A (`CONFIRM`):** Escribe el carácter actualmente enfocado. Si el foco está en "SHIFT", invierte el booleano `isUpperCase` y renderiza el array superior de teclas.
- **Botón X / Cuadrado (`DELETE`):** Borra el último carácter del texto. Se implementó directamente desde `GamepadManager` para dar inmediatez sin requerir que el usuario navegue hasta la tecla `⌫` en pantalla.
- **Botón B (`BACK`):** Cierra el teclado descartando la operación de búsqueda (o dejándola como estaba).
- **Botón START (`MENU`):** Confirma la búsqueda actual, cierra el modal, y el texto filtrado permanece activo en la pantalla principal.

### Sincronización en Tiempo Real
El OSK recibe como parámetro `initialText = searchQuery` y un callback `onTextChanged`. A medida que el usuario pulsa botones en el OSK, el callback actualiza el `HomeViewModel`, de forma que el usuario puede ver los resultados en el fondo oscurecido de la pantalla mientras escribe.
