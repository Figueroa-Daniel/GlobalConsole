# 1. Instalación y preparación

GlobalConsole **no incluye emuladores**: es un frontend que lanza los emuladores
que ya tienes instalados. Para jugar hay que instalar cada emulador por separado
y dejar los juegos en carpetas que luego se indican en Ajustes.

## 1.1. Qué necesitas

1. Un mando compatible con el estándar de gamepad de GLFW (la mayoría de mandos
   Xbox, PlayStation y genéricos con mapeo SDL). Ver [uso del mando](02_uso_del_mando.md).
2. Linux o Windows de escritorio. Puedes seleccionar el ejecutable en Ajustes.
   Sin ruta personalizada se usa Flatpak en Linux o el PATH en Windows
   (véase el apartado 1.6 para los límites de validación).
3. Los emuladores que quieras usar (apartado 1.2).
4. Tus propios juegos, obtenidos legalmente (copias de tus discos o cartuchos).
5. Para PS1 y PS2, la BIOS de tu propia consola, configurada dentro del emulador.

## 1.2. Emuladores a instalar

En Linux GlobalConsole busca cada emulador por su identificador de **Flatpak**.
Instálalos con estos comandos (cada uno es independiente; instala solo los que uses):

| Consola | Emulador | Identificador | Comando |
| --- | --- | --- | --- |
| PlayStation 2 | PCSX2 | `net.pcsx2.PCSX2` | `flatpak install flathub net.pcsx2.PCSX2` |
| PlayStation 1 | DuckStation | `org.duckstation.DuckStation` | `flatpak install flathub org.duckstation.DuckStation` |
| GameCube / Wii | Dolphin | `org.DolphinEmu.dolphin-emu` | `flatpak install flathub org.DolphinEmu.dolphin-emu` |
| Nintendo DS | melonDS | `net.kuribo64.melonDS` | `flatpak install flathub net.kuribo64.melonDS` |
| Nintendo 3DS | Azahar | `org.azahar_emu.Azahar` | `flatpak install flathub org.azahar_emu.Azahar` |
| PlayStation 3 | RPCS3 | `net.rpcs3.RPCS3` | `flatpak install flathub net.rpcs3.RPCS3` |
| Juegos de tiendas | Heroic Games Launcher | `com.heroicgameslauncher.hgl` | `flatpak install flathub com.heroicgameslauncher.hgl` |

Los identificadores son los que usa el código de GlobalConsole en modo Automático.
Para PCSX2, melonDS, Dolphin, Azahar, DuckStation o RPCS3 instalados por otro medio,
abre **Ajustes → Ejecutables de emuladores**, selecciona el archivo y guarda.
Heroic conserva su integración Flatpak.

## 1.3. Configurar cada emulador antes de usarlo

Abre **una vez** cada emulador de forma independiente (desde su icono normal) y
déjalo listo, porque GlobalConsole lo lanza luego en pantalla completa y sin
asistente:

1. Configura el vídeo y el audio.
2. Configura el mando dentro del emulador (cada emulador tiene sus propios
   controles).
3. PCSX2 y DuckStation: indica la ruta de la BIOS de tu consola.
4. Comprueba que un juego arranca desde el propio emulador.

## 1.4. Carpetas de juegos

Crea **una carpeta por emulador** y deja dentro los juegos de esa consola. El
nombre y la ubicación son libres; esta es una organización recomendada:

```text
~/GlobalConsole-Juegos/
├── PCSX2/          (PS2)         → archivos .iso
├── DuckStation/    (PS1)         → .chd, .iso, .img (o .cue + .bin)
├── Dolphin/        (GameCube/Wii)→ .iso, .rvz, .wbfs, .gcm, .ciso
├── MelonDS/        (Nintendo DS) → .nds
└── Azahar/         (Nintendo 3DS)→ .3ds
```

GlobalConsole recorre cada carpeta **de forma recursiva**, así que puedes usar
subcarpetas. RPCS3 y Heroic no usan carpeta de juegos: solo abren su propia
interfaz (véase [emuladores y juegos](04_emuladores_y_juegos.md)).

### Una subcarpeta por juego

Para que las carátulas funcionen bien, usa **una subcarpeta por juego** con su
imagen (`.png`, `.jpg` o `.jpeg`) junto al archivo del juego:

```text
PCSX2/
├── Mi Juego A/
│   ├── Mi Juego A.iso
│   └── caratula.png
└── Mi Juego B/
    ├── Mi Juego B.iso
    └── caratula.jpg
```

También puedes tener varios juegos en una carpeta: usa `Mi Juego.iso` junto a
`Mi Juego.jpg` (o `.png`/`.jpeg`). El recorte individual `Mi Juego.cover.png`
tiene prioridad. Las imágenes genéricas (`cover_cropped`, `cover`, `folder`) y
una imagen única sin nombre coincidente solo se usan en carpetas con un juego.
Si hay varias imágenes sin asociación clara, se muestra la carátula por defecto.
DuckStation aplica el mismo criterio. El nombre del juego es el del archivo sin extensión.

## 1.5. Arrancar GlobalConsole

Desde la carpeta del proyecto:

```bash
./gradlew :desktopApp:run
```

La configuración se guarda en un archivo `config.json` situado en el
**directorio de trabajo** de la aplicación (con el comando anterior, la carpeta
`desktopApp`). Conserva ese archivo si quieres mantener tus ajustes.

## 1.6. Windows

En **Ajustes → Ejecutables de emuladores**, usa **Examinar** para elegir el `.exe`
de PCSX2, melonDS, Dolphin, Azahar, DuckStation o RPCS3. Pulsa **Guardar**; no
coloques el ejecutable en la carpeta de ROMs. **Automático** vuelve a buscar
`pcsx2-qt.exe`, `melonds.exe`, `Dolphin.exe`, `azahar.exe`, `DuckStation.exe` o
`rpcs3.exe` en el PATH. RPCS3 abre su interfaz, no escanea juegos de PS3.

La construcción de comandos de Windows está cubierta por pruebas; no se ha
validado esta corrección en un equipo Windows real. Detalles en
[solución de problemas](06_solucion_de_problemas.md).

## 1.7. Siguiente paso

Continúa con el [uso del mando](02_uso_del_mando.md) y después con
[biblioteca y ajustes](03_biblioteca_y_ajustes.md), donde se indican las rutas
creadas en el apartado 1.4.

Referencias técnicas: [Persistencia de la configuración](../08_persistencia_configuracion.md),
[Imágenes y carátulas](../10_manejo_imagenes_y_caratulas.md) y
[Arquitectura](../01_arquitectura.md).

@author Daniel Figueroa Vidal
@since 2026-10-12
