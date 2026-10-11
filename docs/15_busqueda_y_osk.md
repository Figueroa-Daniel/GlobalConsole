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
