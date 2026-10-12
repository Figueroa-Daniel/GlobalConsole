# 6. Solución de problemas y limitaciones

## 6.1. No aparece ningún juego

1. Comprueba que activaste el emulador en **Ajustes** y que escribiste la ruta
   de su carpeta y pulsaste **Guardar** (las rutas solo se aplican al guardar).
2. Pulsa **Recargar** en la barra superior.
3. Verifica las extensiones admitidas en [emuladores y juegos](04_emuladores_y_juegos.md).
4. La carpeta debe existir y ser un directorio.

## 6.2. Aparecen juegos duplicados en PS1

Comprueba que el CUE contiene las referencias `FILE` correctas a sus pistas.
Los BIN/IMG referenciados se omiten; los independientes se mantienen. Tener el
mismo juego en CHD y CUE sigue creando dos entradas: conserva solo el formato
que quieras mostrar, sin eliminar las pistas necesarias para el CUE.

## 6.3. Todas las carátulas son la misma

Usa imágenes con el nombre del juego (`Juego.iso` y `Juego.jpg`). Las genéricas
solo se usan si hay un juego en la carpeta. Véase la
[organización de carpetas](01_instalacion_y_preparacion.md).

## 6.4. El emulador no arranca

- Si has configurado un ejecutable, comprueba que existe y tiene permiso de ejecución.
  Puedes volver a **Automático** desde Ajustes.
- En Linux, el modo Automático usa Flatpak. Comprueba con
  `flatpak info <identificador>` que está instalado (identificadores en la
  [guía de instalación](01_instalacion_y_preparacion.md)).
- Abre el emulador por su cuenta y confirma que el juego funciona allí.
- Ejecuta GlobalConsole desde una terminal: los errores de lanzamiento se
  imprimen en la salida.

## 6.5. START + R2 no abre el teclado

- Pulsa **START y R2 a la vez** (R2 es el gatillo derecho). R3, R1 y L2 no son
  atajos.
- Algunos mandos informan los gatillos al revés. Solo el mando **HJDX** está
  corregido; para otro mando con los gatillos intercambiados haría falta añadir
  su identificador (GUID) al código. Alternativa: abrir el teclado haciendo
  clic (A) sobre un campo de texto con el ratón del mando.
- En aplicaciones sin ratón del mando (juegos de emulador) el atajo START + R2
  sigue funcionando, pero el clic para abrirlo no.

## 6.6. El teclado envía el texto al sitio equivocado

El texto se pega en la ventana que tiene el foco del sistema. Haz clic en el
campo de destino antes de abrir el teclado y no cambies de ventana mientras
escribes.

## 6.7. El teclado no escribe en Wayland

La inyección de teclas con AWT Robot no está garantizada en Wayland. Usa una
sesión X11 si necesitas el teclado flotante.

## 6.8. No vuelvo a GlobalConsole tras cerrar una aplicación

Pulsa **START + BACK** o **Guía**. Si la aplicación delega en otra instancia ya
abierta (por ejemplo Brave), el proceso lanzado puede terminar enseguida y la
ventana real seguir abierta: ciérrala manualmente.

## 6.9. Limitaciones conocidas de esta versión

| Tema | Detalle |
| --- | --- |
| Windows | Las rutas y comandos están cubiertos por pruebas automáticas. La ejecución real de emuladores y la detección de aplicaciones requieren comprobación en Windows. |
| Mandos | Solo el mando HJDX tiene corrección de gatillos intercambiados. |
| Teclado flotante | Depende del sistema gráfico; Wayland no garantizado. |
| Configuración | Se guarda en `config.json` del directorio de trabajo. Cambiar de carpeta de ejecución equivale a empezar con una configuración nueva. |
| Validación | La interfaz se ha verificado con pruebas automáticas y ventanas de prueba; la validación con mando físico y emuladores reales cubre solo el equipo del autor. |

## 6.10. Referencias

[Instalación](01_instalacion_y_preparacion.md), [mando](02_uso_del_mando.md),
[teclado](05_teclado_en_pantalla.md), [Persistencia](../08_persistencia_configuracion.md)
y [Contexto del proyecto](../19_contexto_ia.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
