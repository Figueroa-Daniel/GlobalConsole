# Reglas para el manejo del Gamepad y el Foco

Al implementar, modificar o revisar código relacionado con la interacción del usuario mediante mandos (gamepads) y teclado/ratón en GlobalConsole, SE DEBEN cumplir siempre las siguientes reglas para garantizar una experiencia de usuario robusta y sin fallos:

## 1. Prevención de Fuga de Eventos (Event Leaking) en Diálogos
NUNCA permitas que una pantalla base procese eventos del `GamepadManager` si hay un diálogo modal, popup o capa superpuesta activa. 
**Obligatorio:**
Todo `LaunchedEffect(gamepadManager)` en un componente o pantalla base que recolecte eventos de botones y cruceta, DEBE incluir las variables de estado de visibilidad de sus diálogos (ej. `showDialog`, `activeItem != null`) dentro de sus parámetros de reinicio (`keys`), y DEBE hacer un retorno temprano (`return@LaunchedEffect`) si alguna es verdadera.
*Razón:* Al no hacer esto, al pulsar "A" (Confirmar) dentro del diálogo, también se pulsará en la pantalla trasera (ej. abriendo el juego a la vez que se acepta un diálogo).

## 2. Gestión Dual de Foco (Gamepad vs MOUSE)
GlobalConsole tiene un comportamiento dual (Stick Izquierdo = Navegación Lógica, Stick Derecho = Ratón libre).
**Obligatorio:**
Si un botón del gamepad (ej. `OPTIONS / Y`) ejecuta una acción contextual sobre el "elemento seleccionado", DEBES consultar el estado de entrada (`inputMode`).
- Si `inputMode == InputMode.MOUSE`, usa el elemento sobrevolado por el ratón (`hoveredIndex`).
- Si `inputMode == InputMode.GAMEPAD`, usa el elemento enfocado por navegación de teclado/mando (`focusedIndex`).

## 3. Rastreo Activo de onHover
Para cumplir con la regla anterior, los componentes interactivos como las tarjetas (Tiles) que respondan a eventos contextuales DEBEN exponer e implementar un callback de sobrevolado, p. ej. `onHover = { hoveredGameIndex = index }`. Utiliza `interactionSource.collectIsHoveredAsState()` para ello.

## 4. Prioridad de Eventos en Diálogos
Los diálogos que reciban el gamepad DEBEN definir un nuevo `LaunchedEffect` independiente que recolecte los eventos del mando y los consuma para sus acciones internas (A para confirmar, B para cancelar, Stick para modificar, etc.), sin depender de la UI base.
