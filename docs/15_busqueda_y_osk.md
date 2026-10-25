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

START + R2 físico alterna el teclado; START confirma una vez liberado el
atajo de apertura. Los botones usados para abrirlo no escriben letras por arrastre.
Al finalizar la aplicación externa se cierra también el teclado.

El clic sobre cursor de texto abre el teclado al soltar el botón (ratón o A).
El doble A fuera de texto no lo abre. La inyección con AWT Robot depende del
sistema gráfico; Wayland no garantiza su funcionamiento.

Validación manual pendiente con navegador y mando físicos: abrir con ambos
órdenes de START/R2, cerrar con el mismo combo o B, escribir y confirmar,
reabrir sin texto anterior y comprobar que L2/R3/R1 no abren el OSK y el clic en texto sí lo abre.
Véase también [Mando y foco](12_manejo_gamepad_y_foco.md).

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

### 8.1 Gatillos intercambiados en algunos mandos

> **Superado por el apartado 9.** La decisión de aceptar ambos gatillos se
> revirtió porque START + L2 abría el teclado sin querer. Se conserva el
> diagnóstico del mando HJDX, que sigue justificando su mapeo propio.

El diagnóstico con el mando HJDX (GUID `03000000632500002605000010010000`) mostró
que su R2 físico llega por el eje 4 (`LEFT_TRIGGER` del estándar GLFW) y su L2
por el eje 5. La solución anterior aceptaba ambos gatillos y provocaba la
apertura no deseada con L2. Se sustituye por la selección del eje según GUID
explicada en el apartado 9, conservando un único detector con histéresis.

@author Daniel Figueroa Vidal
@since 2026-10-12

## 9. Apertura exclusiva con START + R2 físico (2026-10-12)

Sustituye las alternativas de los apartados 6–8: en aplicaciones externas solo
START + R2 es el único atajo de mando. R3, START + R1 y START + L2 no lo abren.
La apertura por clic en texto se mantiene según el apartado 10. START con el teclado abierto conserva la confirmación;
B conserva el cierre. El buscador interno conserva su interacción propia.

El GUID HJDX documentado en 8.1 selecciona el eje izquierdo de GLFW como R2 físico;
los demás mandos usan el eje derecho estándar. Un caso de uso puro resuelve ese
valor y el detector con histéresis existente decide la pulsación. No se combinan
los dos ejes: esa combinación causaba la apertura no deseada con L2.
Las pruebas de casos de uso cubrirán ambos mapeos, controles excluidos, órdenes
del combo y pulsaciones mantenidas. Véase [Mando](12_manejo_gamepad_y_foco.md)
y [Contexto](19_contexto_ia.md).

Validación del apartado 9: pruebas de exclusión fallidas antes del cambio y
correctas después; batería completa sin fallos. El mapeo HJDX se basa en el
diagnóstico previo documentado, pendiente de repetir con el mando físico.

## 10. Restaurar clic sobre campos de texto (2026-10-12)

La exclusividad de START + R2 se aplica a los atajos de mando, no al clic sobre
campos de texto. Se restaura la apertura por clic físico y por A usado como clic
sobre un cursor de texto. Se espera a liberar el clic para que el campo conserve
el foco. No se restaura el doble A sobre zonas arbitrarias ni R3/START+L2/R1.
Un caso de uso controla el flanco de clic y cancela la apertura si el contexto
externo deja de estar activo. Se conserva la detección de cursor existente y sus
limitaciones X11/Wayland. Véase [Mando](12_manejo_gamepad_y_foco.md).

Validación del apartado 10: 129 pruebas correctas, incluyendo apertura al soltar,
no repetición, rechazo de clics fuera de texto y cancelación al salir del contexto.
Pendiente de comprobar en el navegador y mando físicos del usuario.

## 11. Cursor del campo externo con L1 y R1 (2026-10-12)

Mientras el teclado flotante esté visible sobre una aplicación externa, L1 envía
una pulsación de flecha izquierda y R1 una de flecha derecha al campo que conserva
el foco. El teclado flotante no cambia la tecla seleccionada ni el texto de su
búfer. La cruceta y el stick izquierdo siguen moviendo el foco entre teclas.

La decisión de flanco se aísla en un caso de uso de Domain; `GamepadManager`
inyecta las flechas con AWT Robot como ya hace con Backspace. Solo se procesa
con el teclado flotante activo y una aplicación suspendida, y se restablece el
estado al cerrarlo. Se verifica que cada pulsación produce un único movimiento,
que mantener pulsado no repite y que L1/R1 no alteran los atajos existentes.
La interacción real con el cursor de un navegador depende del foco de la ventana
externa y requiere comprobación en el entorno gráfico del usuario. Véase
[Mando y foco](12_manejo_gamepad_y_foco.md) y [Contexto](19_contexto_ia.md).

Validación del apartado 11: 131 pruebas correctas, incluidos flancos de L1/R1,
pulsaciones mantenidas y rechazo fuera del teclado externo. La ayuda del OSK
muestra «L1/R1 Cursor». Al cerrar el OSK, el estado de ambos botones queda
sincronizado para no emitir pulsaciones a la vista del fondo. Falta confirmar el foco y la inyección de flechas en un
navegador real con mando físico.

## 12. Edición del texto pendiente con L2 y R2 (2026-10-12)

Dentro del teclado flotante, L2 desplaza el cursor una posición a la izquierda
**en el texto preparado para enviar** y R2 una a la derecha. Ese cursor se muestra
en la vista previa. Al pulsar una tecla, su carácter se inserta en esa posición;
X/⌫ borra el carácter anterior. Los movimientos se detienen al principio y al
final del texto y respetan caracteres Unicode completos. L1/R1 siguen actuando
solo sobre el cursor del campo externo; cruceta y stick izquierdo siguen eligiendo
teclas.

Se usa el mismo mapeo físico de gatillos (incluido el GUID HJDX) y la histéresis
ya documentada. START + R2 conserva prioridad para abrir/cerrar el OSK: ese
combo no desplaza el cursor de edición ni al soltar START si R2 continúa pulsado.
Las pruebas se limitan a casos de uso: inserción/borrado en el cursor, límites,
Unicode, flancos y prioridad del combo. Véase [Mando](12_manejo_gamepad_y_foco.md)
y [Contexto](19_contexto_ia.md).

Validación del apartado 12: 135 pruebas correctas. Se cubrieron inserción y
borrado en el cursor, límites, Unicode, los dos mapeos físicos de gatillos y el
bloqueo de movimiento durante START + R2. En una vista X11 temporal se observó
`w│q` tras escribir `q`, mover el cursor y añadir `w`, sin desplazar la tecla
virtual resaltada. Queda pendiente la comprobación con mando físico.

## 13. Estado del registro de diagnóstico (2026-10-12)

`GamepadManager` conserva el registro temporal `[OSK-DIAG]` (mando, GUID,
botones y ejes 4 y 5) usado para localizar el mapeo de R2 descrito en 8.1. Sigue
activo por decisión del usuario mientras se valida con el mando físico; es una
salida de depuración y no forma parte del comportamiento funcional. Nota sobre
las decisiones intermedias: el apartado 8.1 (aceptar ambos gatillos) y la
cobertura de pruebas con R1/R3 del apartado 6 fueron sustituidos por el
apartado 9, sin alterar su texto original.

@author Daniel Figueroa Vidal
@since 2026-10-12
