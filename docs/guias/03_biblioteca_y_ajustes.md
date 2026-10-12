# 3. Biblioteca y ajustes

## 3.1. La biblioteca

La pantalla principal muestra una cuadrícula de tiles con todo lo que has
habilitado: juegos de cada emulador, los propios emuladores (launchers),
juegos y aplicaciones locales, navegadores y Heroic. Arriba hay una barra con
cuatro acciones:

| Acción | Para qué sirve |
| --- | --- |
| **Buscar** | Filtra la biblioteca escribiendo con el [teclado en pantalla](05_teclado_en_pantalla.md). |
| **Recargar** | Vuelve a escanear las carpetas de juegos. Úsalo tras añadir o quitar archivos. |
| **Vista** | Cambia la agrupación, la posición de los emuladores y los colores. |
| **Ajustes** | Activa emuladores, indica las carpetas de juegos y gestiona navegadores, juegos y aplicaciones. |

Si una búsqueda no da resultados, la barra superior sigue accesible para
buscar de nuevo.

## 3.2. Primera configuración, paso a paso

1. Abre **Ajustes**.
2. En **Opciones y biblioteca**, activa los emuladores que usas (por ejemplo
   «Mostrar Dolphin en la biblioteca»). Esto cambia al momento y añade el
   launcher de ese emulador a la biblioteca.
3. En **Carpetas de juegos**, indica la carpeta de cada emulador creada en la
   [guía de instalación](01_instalacion_y_preparacion.md): PCSX2, Melon DS,
   Dolphin, Azahar (3DS) y DuckStation (PS1). Escribe la ruta o usa **Examinar**.
4. Pulsa **Guardar**. Las opciones se aplican al instante, pero **las rutas
   solo se aplican al guardar**.
5. Vuelve a la biblioteca: los juegos aparecen tras la recarga automática o con
   **Recargar**.

Otras opciones de Ajustes:

- **Control del gamepad**: sensibilidad del ratón cuando usas el stick derecho.
- **Navegadores web**: elige qué navegadores instalados aparecen.
- **Juegos locales de PC** y **Aplicaciones**: elige qué entradas del sistema se
  muestran. Deshabilitar una entrada solo la oculta; no desinstala nada.

## 3.3. Vista y apariencia

En **Vista** puedes ajustar, con ← → y A:

| Opción | Valores |
| --- | --- |
| **Agrupación** | Sin agrupar, por plataforma o por familia de consolas. |
| **Emuladores** (posición de los launchers) | Arriba del todo, o junto a los juegos. |
| **Color del brillo** | Una paleta de acentos (cian, azul, violeta, rosa, ámbar, verde, rojo…). |
| **Intensidad del brillo** | De 0 a 100. El borde de selección se mantiene aunque el brillo sea 0. |

Los cambios de Vista se guardan automáticamente. El fondo siempre es oscuro.

## 3.4. Carátulas

Cada juego usa como carátula la primera imagen (`.png`, `.jpg`, `.jpeg`) de su
carpeta; véase la [organización de carpetas](01_instalacion_y_preparacion.md).
Con una carátula presente, **X** abre el recortador para encuadrarla: mueve la
imagen con el stick izquierdo, haz zoom con los gatillos y confirma.

> [!NOTE]
> Los juegos de DuckStation (PS1) no cargan carátula de su carpeta en esta
> versión.

## 3.5. Qué pasa al abrir un elemento

- **Un juego de emulador**: se lanza el emulador en pantalla completa con ese
  juego. GlobalConsole queda en espera. Al cerrar el juego, o con
  **START + BACK** o el botón **Guía**, vuelves a la biblioteca.
- **Un launcher de emulador**: abre la interfaz del propio emulador.
- **Navegador, aplicación local o Heroic**: se abren y permiten usar el ratón
  con el stick derecho; véase [uso del mando](02_uso_del_mando.md).

Detalle por emulador en [emuladores y juegos](04_emuladores_y_juegos.md).

## 3.6. Referencias

Técnicas: [Persistencia de la configuración](../08_persistencia_configuracion.md),
[Persistencia de la vista](../20_persistencia_vista.md),
[Metro personalizable](../21_metro_personalizable.md),
[Recortador de imágenes](../11_recortador_imagenes.md) y
[Usabilidad de la configuración](../25_usabilidad_configuracion.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
