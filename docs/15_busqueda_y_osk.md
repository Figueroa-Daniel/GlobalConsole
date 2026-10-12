# 15. Búsqueda reactiva y teclado en pantalla

## 1. Arquitectura

`HomeViewModel` filtra los juegos en memoria al cambiar `searchQuery`.
`GamepadOSK` presenta el texto y delega las operaciones en `KeyboardInputUseCase`,
un caso de uso puro sin dependencias de Compose ni GLFW. `KeyboardState` conserva
texto, mayúsculas, fila, columna y columna preferida. Mando y clic comparten la
misma operación para evitar diferencias al escribir o confirmar.

## 2. Distribución y controles

1. Números 1–0, guion y borrar.
2. QWERTY y á/é.
3. ASDF, ñ, í/ó.
4. Mayúsculas, ZXCV, ú/ü, punto y barra.
5. Coma, espacio e Intro.

Cruceta o stick izquierdo mueve el foco con recorrido circular. Al atravesar la
última fila se conserva la columna horizontal preferida, y todos los índices se
actualizan atómicamente. La fila enfocada se desplaza automáticamente a la vista.

- **A:** pulsar la tecla seleccionada, incluida Intro para confirmar.
- **X / Cuadrado:** borrar el último punto de código, sin romper pares UTF-16.
- **Y / Triángulo:** alternar mayúsculas inmediatamente.
- **START:** confirmar y cerrar.
- **B:** cerrar conservando la búsqueda reactiva actual.
- **Stick derecho:** ratón; clic y hover usan los mismos botones Metro.

## 3. Apariencia y seguridad de entrada

El teclado hereda el acento y halo del tema global, mantiene el fondo oscuro y
se adapta a ventanas de 800×600 y superiores. La biblioteca deja de recoger
entrada mientras el teclado está abierto. Una búsqueda sin resultados mantiene
accesible la barra superior para volver a buscar.

## 4. Pruebas

`KeyboardInputUseCaseTest` recorre cada posición y sus cuatro direcciones, verifica
recuperación de columna, mayúsculas, números, ñ, espacio, borrado vacío, borrado de
un carácter suplementario e Intro mediante A. La revisión gráfica adicional
simula los eventos del mando sobre la pantalla real en una ventana aislada.
No sustituye una prueba con un mando físico.

## 5. Referencias

- [Arquitectura](01_arquitectura.md)
- [Mando y foco](12_manejo_gamepad_y_foco.md)
- [Metro personalizable](21_metro_personalizable.md)
- [Contexto](19_contexto_ia.md)

@author Daniel Figueroa Vidal
@since 2026-10-04

## 6. Teclado sobre navegadores externos

La ventana del OSK no debe tomar el foco del navegador: se configura como no
activable antes de mostrarla y se controla mediante eventos del mando. Su
contenido solo existe mientras está visible, para no consumir pulsaciones desde
una ventana oculta y para reiniciar el texto en cada apertura. Koin se inicializa
una sola vez, fuera de las recomposiciones provocadas por la visibilidad.

R3 y START + R2/R1 alternan el teclado; START confirma una vez liberado el
atajo de apertura. Los botones usados para abrirlo no escriben letras por arrastre.
Al finalizar la aplicación externa se cierra también el teclado.

En X11 la detección automática consulta el cursor al hacer clic con A o con el
ratón físico. Los píxeles de XFixes son `unsigned long`: se leen con el tamaño
nativo, también en Linux de 64 bits. La forma de cursor es una heurística y puede
aparecer sobre texto no editable; R3 y el doble toque de A son alternativas cuando
el navegador no permite reconocer el campo. Wayland no garantiza esta detección
ni la inyección con AWT Robot.

Validación manual pendiente con navegador y mando físicos: abrir con ambos
órdenes de START/R2, cerrar con R3/B, escribir y confirmar, reabrir sin texto
anterior, y comprobar clics sobre campos y retorno al dashboard. Véase también
[Mando y foco](12_manejo_gamepad_y_foco.md).

`OskShortcutUseCase` mantiene los flancos independientemente del resto de botones.
Sus pruebas cubren ambos órdenes de START/R2, pulsaciones mantenidas, R1, R3,
confirmación y reinicio. Verificación realizada: las 100 pruebas de escritorio
pasan; una comprobación aislada con Xvfb distingue el cursor de texto del cursor
normal y detecta pulsación y liberación del botón físico. Esto no verifica la
superposición sobre ventanas de navegador en el gestor de ventanas del usuario.

## 7. Sesiones de Brave ya abiertas

Brave puede delegar la apertura en una instancia existente y terminar el proceso
lanzador. Esperar solo a ese proceso devuelve prematuramente GlobalConsole a la
biblioteca, desactivando los atajos del OSK. El adaptador debe seguir los procesos
principales del navegador seleccionado cuando el lanzador termina correctamente.
No se consideran procesos de renderizado, GPU ni navegadores distintos. El cierre
se limita a los procesos de la sesión seguida; no debe usar `killall` sobre todos
los navegadores instalados. Al compartir una instancia existente, cerrar esa
sesión con START + BACK cierra también sus otras ventanas.

La política de selección se valida en un caso de uso puro, incluidos Brave nativo,
Flatpak, procesos auxiliares y selección de otro navegador. Véase
[Arquitectura](01_arquitectura.md).

Verificación: 102 pruebas de escritorio correctas y comprobación aislada del
adaptador con procesos controlados: el lanzador termina, la sesión sigue activa,
el cierre natural termina la espera, el cierre explícito no afecta a otro
navegador y un lanzamiento fallido devuelve error. Falta la prueba interactiva
con Brave real tras reiniciar GlobalConsole. Si Brave mantiene procesos en segundo
plano al cerrar su última ventana, START + BACK termina la sesión seguida.

## 8. Atajo en primer plano y borrado en campos externos

**Problema.** START + R2 no abría el teclado y no se podía borrar el texto ya
existente de un campo.

**Causas y decisiones.**

1. El atajo solo se evaluaba con una aplicación externa abierta. Ahora
   `GamepadManager` lo evalúa también con GlobalConsole en primer plano. Mientras
   el teclado está abierto, sus eventos se emiten por un flujo propio
   (`oskEvents`) para no filtrarse a la pantalla base, según la regla 1 de
   [Mando y foco](12_manejo_gamepad_y_foco.md).
2. R2 es un eje analítico (reposo −1 o 0 según el controlador). El umbral único
   0,1 provocaba lecturas inestables. `TriggerPressUseCase` aplica histéresis:
   pulsado por encima de 0,5 y liberado por debajo de 0,2.
3. El `⌫` solo borraba el búfer del teclado. Con el búfer vacío,
   `KeyboardInputUseCase` incrementa `externalDeletes` y la capa de presentación
   envía Retroceso al campo externo con AWT Robot. Así se puede borrar lo escrito
   antes de abrir el teclado, también en los campos internos de GlobalConsole.

**Impacto.** La lógica nueva reside en casos de uso puros con pruebas
(`TriggerPressUseCaseTest`, `KeyboardInputUseCaseTest`); la inyección de teclas
sigue en la capa de presentación. Véase [Arquitectura](01_arquitectura.md) y
[Contexto](19_contexto_ia.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
