# 2. Uso del mando

GlobalConsole se maneja entero con mando. También funciona con teclado y ratón,
y ambos mundos conviven.

## 2.1. Dos modos de entrada

| Modo | Cómo se activa | Qué hace |
| --- | --- | --- |
| **Mando** | Mover la cruceta o el stick izquierdo | Mueve el foco entre elementos (tiles, botones, filas). |
| **Ratón** | Mover el stick derecho | Mueve el cursor del sistema; A hace clic. |

El modo cambia solo según lo último que hayas tocado. Los botones que actúan
sobre «el elemento seleccionado» (por ejemplo X) usan el elemento enfocado en
modo mando y el que está bajo el cursor en modo ratón.

## 2.2. En la biblioteca

| Botón | Acción |
| --- | --- |
| Cruceta / stick izquierdo | Mover el foco entre juegos (también salta entre grupos). |
| **A** | Abrir el juego o emulador enfocado; en la barra superior, activar el botón enfocado. |
| **Y / △** | Saltar directamente al botón **Buscar** de la barra superior (no ejecuta nada; A confirma). |
| **X / □** | Abrir el recortador de la carátula del elemento activo (solo si tiene imagen). |
| **Guía (botón central)** | Cerrar el juego activo y volver a GlobalConsole. |
| **B** | Cerrar el diálogo de ajustes si está abierto. |
| **Stick derecho** | Cursor del ratón. |

## 2.3. En diálogos (ajustes, vista, carpetas, recortador)

Un diálogo abierto recibe el mando en exclusiva: lo que pulses no llega a la
biblioteca de fondo.

| Pantalla | Controles |
| --- | --- |
| Ajustes | Cruceta para recorrer opciones; **A** activa/confirma; **Y / △** va a **Guardar**; **B** cierra. |
| Vista y apariencia | **↑ ↓** elegir fila; **← →** ajustar el valor; **A** siguiente valor; **B** cerrar; **Y / △** ir a Cerrar. |
| Juegos locales, Aplicaciones, Navegadores | **↑ ↓** navegar; **A** habilitar/deshabilitar; **B** volver; **Y / △** ir a Cerrar. |
| Selector de carpetas | Cruceta para entrar y salir de carpetas; **Y / △** enfoca **Seleccionar**; **←** pasa a Cancelar; **↑** vuelve a la lista. |
| Recortador de carátula | Stick izquierdo para mover la imagen y gatillos para el zoom; **Y / △** enfoca Confirmar. |

## 2.4. Durante un juego

Cuando lanzas un juego, GlobalConsole **suspende** su propia navegación y deja
el mando al emulador. Solo quedan activos estos atajos de GlobalConsole:

| Combinación | Acción |
| --- | --- |
| **START + BACK (Select)** | Volver a GlobalConsole y cerrar el proceso que lanzó la aplicación. |
| **Guía** | Volver a GlobalConsole. |
| **START + R2** | Abrir o cerrar el [teclado en pantalla](05_teclado_en_pantalla.md). |
| **X + BACK** | Ocultar o mostrar el cursor del ratón (solo en las aplicaciones con ratón, ver abajo). |

### Ratón y teclado en pantalla durante la ejecución

El stick derecho como ratón está disponible durante la ejecución de: los
**launchers** de emuladores, **melonDS**, **PS3 (RPCS3)**, **Heroic**, las
**aplicaciones locales** y los **navegadores**. No lo está en los juegos de
PCSX2, DuckStation, Dolphin o Azahar lanzados desde la biblioteca, que se
controlan con el mando del propio emulador.

Con el ratón activo, **A** hace clic. Si el clic cae sobre un campo de texto, se
abre solo el teclado en pantalla.

## 2.5. Mandos con gatillos intercambiados

Algunos mandos informan de L2 y R2 al revés del estándar. GlobalConsole conoce
el mando **HJDX** (GUID `03000000632500002605000010010000`) y lo corrige. Con
otro mando que tenga los gatillos invertidos, **START + R2** puede no abrir el
teclado; véase [solución de problemas](06_solucion_de_problemas.md).

## 2.6. Referencias

Técnicas: [Manejo del mando y foco](../12_manejo_gamepad_y_foco.md) y
[Acceso rápido y configuración](../25_usabilidad_configuracion.md). Siguiente:
[biblioteca y ajustes](03_biblioteca_y_ajustes.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
