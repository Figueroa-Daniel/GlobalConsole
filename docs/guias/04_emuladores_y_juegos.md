# 4. Emuladores y juegos

Cada emulador tiene su carpeta de juegos y su propio formato. Esta guía resume,
por emulador, qué instalar, qué archivos deja GlobalConsole en la biblioteca y
qué comando lanza. Prepara las carpetas según la
[guía de instalación](01_instalacion_y_preparacion.md) y actívalas en
[Ajustes](03_biblioteca_y_ajustes.md).

## 4.1. Resumen

| Consola | Emulador | Carpeta en Ajustes | Formatos que se listan | Carátula automática |
| --- | --- | --- | --- | --- |
| PS2 | PCSX2 | Ruta de juegos (PCSX2) | `.iso` | Sí |
| PS1 | DuckStation | Ruta de juegos (DuckStation PS1) | `.bin` `.cue` `.iso` `.img` `.chd` | No |
| GameCube / Wii | Dolphin | Ruta de juegos (Dolphin) | `.iso` `.rvz` `.wbfs` `.gcm` `.ciso` | Sí |
| Nintendo DS | melonDS | Ruta de juegos (Melon DS) | `.nds` | Sí |
| Nintendo 3DS | Azahar | Ruta de juegos (Azahar 3DS) | `.3ds` | Sí |
| PS3 | RPCS3 | No usa carpeta | — (solo launcher) | — |

El escaneo es recursivo y no distingue mayúsculas en las extensiones.

## 4.2. PlayStation 2 — PCSX2

1. Instala `net.pcsx2.PCSX2` y configura la BIOS y el mando dentro de PCSX2.
2. Deja las imágenes `.iso` en la carpeta de PCSX2 (mejor una subcarpeta por
   juego con su carátula).
3. En Ajustes activa el launcher si quieres abrir PCSX2 y escribe la ruta de la
   carpeta; **Guardar**.
4. Al abrir un juego se ejecuta `flatpak run net.pcsx2.PCSX2 -fullscreen -nogui <iso>`.

## 4.3. PlayStation 1 — DuckStation

1. Instala `org.duckstation.DuckStation` y configura la BIOS.
2. Prefiere **un solo archivo por juego** (`.chd`, `.iso` o `.img`).

> [!WARNING]
> Con un juego en `.cue` + `.bin`, GlobalConsole lista **ambos archivos** como si
> fueran dos juegos. Convierte a `.chd` o usa un archivo único para evitar
> entradas duplicadas.

3. Se lanza con `-fullscreen`. En Linux se usa Flatpak
   (`org.duckstation.DuckStation`); también admite un AppImage, véase
   [solución de problemas](06_solucion_de_problemas.md).

## 4.4. GameCube y Wii — Dolphin

1. Instala `org.DolphinEmu.dolphin-emu` y configura el mando en Dolphin.
2. Deja los juegos en la carpeta de Dolphin (`.iso`, `.rvz`, `.wbfs`, `.gcm`,
   `.ciso`).
3. Se lanza con `flatpak run org.DolphinEmu.dolphin-emu -b -e <juego>`: modo
   por lotes, que **cierra Dolphin al terminar el juego** y devuelve el control
   a GlobalConsole.

## 4.5. Nintendo DS — melonDS

1. Instala `net.kuribo64.melonDS` y configúralo (BIOS opcional según tu caso).
2. Deja los `.nds` en la carpeta de melonDS.
3. Se lanza con `-f` (pantalla completa). Durante la ejecución el stick derecho
   funciona como ratón, útil para la pantalla táctil; **X + BACK** oculta o
   muestra el cursor.

## 4.6. Nintendo 3DS — Azahar

1. Instala `org.azahar_emu.Azahar` y configúralo.
2. Deja los `.3ds` en la carpeta de Azahar.
3. Se lanza con `flatpak run org.azahar_emu.Azahar -f <juego>`.

## 4.7. PlayStation 3 — RPCS3

GlobalConsole **no escanea juegos de PS3**: solo abre la interfaz de RPCS3
(`flatpak run net.rpcs3.RPCS3`). Activa «PS3 Launcher (RPCS3)» en Ajustes. Elige
y lanza los juegos desde la propia ventana de RPCS3 con el stick derecho como
ratón; cuando ya no lo necesites, **X + BACK** oculta el cursor. RPCS3 no está
implementado en Windows.

## 4.8. Heroic Games Launcher

Instala `com.heroicgameslauncher.hgl` y activa «Heroic Games Launcher» en
Ajustes. GlobalConsole lo abre en pantalla completa; tus juegos de Epic, GOG o
Amazon se gestionan dentro de Heroic.

## 4.9. Navegadores, juegos locales y aplicaciones

Estas tres categorías **no necesitan carpeta**: GlobalConsole detecta lo
instalado y tú eliges qué mostrar en Ajustes.

- **Navegadores web**: Chrome, Firefox, Brave y Edge, por Flatpak o binario
  nativo.
- **Juegos locales de PC**: detecta juegos mediante los accesos `.desktop`
  (Linux). Los lanza con su directorio de trabajo.
- **Aplicaciones**: aplicaciones instaladas que no sean juegos, emuladores ni
  navegadores.

En todas, **START + BACK** cierra únicamente el proceso lanzado por
GlobalConsole. Algunos programas delegan en una instancia ya abierta (por
ejemplo Brave): en ese caso cerrarlos puede cerrar también sus otras ventanas.

## 4.10. Cerrar un juego y volver

Cierra el juego desde el propio emulador, o pulsa **START + BACK** o **Guía**.
Al volver, GlobalConsole recupera el foco y recarga la biblioteca.

## 4.11. Referencias técnicas

[PCSX2](../04_pcsx2.md), [MelonDS](../05_melonds.md), [Dolphin](../16_dolphin.md),
[DuckStation](../17_duckstation.md), [Azahar](../18_azahar_3ds.md),
[PS3](../13_ps3_launcher.md), [Heroic](../09_heroic_games_launcher.md),
[Navegadores](../22_navegadores_web.md), [Juegos locales](../23_juegos_locales.md)
y [Aplicaciones locales](../24_aplicaciones_locales.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
