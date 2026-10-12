# 5. Teclado en pantalla

GlobalConsole incluye un teclado virtual que se maneja con el mando. Hay dos
variantes que comparten el mismo diseño.

## 5.1. Teclado de la búsqueda

Se abre desde **Buscar** (barra superior). Filtra la biblioteca mientras
escribes.

| Botón | Acción |
| --- | --- |
| Cruceta / stick izquierdo | Mover la selección entre teclas (con recorrido circular). |
| **A** | Pulsar la tecla seleccionada. |
| **X** | Borrar el último carácter. |
| **Y** | Alternar mayúsculas. |
| **START** | Buscar y cerrar. |
| **B** | Cerrar conservando la búsqueda actual. |

La distribución incluye números, letras, ñ, vocales con tilde, ü, punto, barra,
coma, espacio e Intro.

## 5.2. Teclado flotante para aplicaciones externas

Sirve para escribir en navegadores, aplicaciones y emuladores. Es una ventana
siempre encima que **no le quita el foco** a la aplicación externa.

### Abrirlo y cerrarlo

- **START + R2** (el gatillo derecho físico), mientras estás en GlobalConsole
  o en una aplicación externa. La misma combinación lo cierra.
- Con el **ratón del mando** activo, un **clic (A)** sobre un campo de texto lo
  abre al soltar el botón. También se abre al hacer clic con el ratón físico.
- **B** lo cierra.

> [!NOTE]
> R3, START + R1 y START + L2 **no** abren el teclado. Si tu mando intercambia
> L2 y R2, véase [solución de problemas](06_solucion_de_problemas.md).

### Controles con el teclado abierto

| Botón | Acción |
| --- | --- |
| Cruceta / stick izquierdo | Mover la selección de teclas. |
| **A** | Escribir la tecla (en el texto pendiente del teclado). |
| **X** | Borrar el carácter anterior del texto pendiente. Con el texto vacío, **borra en el campo externo** (envía Retroceso). |
| **Y** | Mayúsculas. |
| **L2 / R2** | Mover el cursor dentro del texto pendiente. |
| **L1 / R1** | Mover el cursor del campo externo a izquierda y derecha. |
| **START** | Enviar el texto al campo y pulsar Intro. |
| **B** | Cerrar sin enviar. |

### Cómo funciona el envío

Lo que escribes se acumula en el teclado. Al pulsar **START** (o la tecla
Intro del teclado), GlobalConsole copia el texto al portapapeles y lo pega con
Ctrl+V en el campo que tenía el foco, seguido de Intro. Por eso el campo de
destino debe haber quedado enfocado antes de abrir el teclado.

### Borrar texto que ya estaba escrito

1. Abre el teclado con START + R2.
2. Con el texto pendiente vacío, pulsa **X** (o la tecla ⌫): cada pulsación
   borra un carácter en el campo externo.
3. Usa **L1 / R1** para colocar el cursor del campo antes de borrar.

## 5.3. Limitaciones

- La inyección de teclas usa AWT Robot. En **Wayland** no está garantizada; en
  X11 funciona como se describe.
- La detección de campos de texto por clic se basa en la forma del cursor, es
  una heurística y puede fallar en algunas aplicaciones. START + R2 es la
  alternativa fiable.
- Comprobada con un navegador y mando físico en el equipo del autor; otros
  entornos pueden diferir.

## 5.4. Referencias

Técnicas: [Búsqueda y teclado](../15_busqueda_y_osk.md) y
[Manejo del mando y foco](../12_manejo_gamepad_y_foco.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
