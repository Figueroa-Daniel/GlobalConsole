# 1. GlobalConsole

Frontend de escritorio para tu biblioteca de juegos y emuladores,
con una interfaz estilo Metro pensada para el salón y **controlada al 100 % con
mando**. Reúne en una sola pantalla los juegos de varios emuladores y Heroic.
Tú dejas tus ISOs en carpetas, las seleccionas en Ajustes y los juegos aparecen
en la biblioteca. Los lanza en pantalla completa y,
al terminar, te devuelve a la biblioteca. Desarrollado con Kotlin, Compose
Multiplatform y arquitectura MVVM con casos de uso.

> [!IMPORTANT]
> GlobalConsole **no incluye emuladores, BIOS ni juegos**. Es un frontend que
> lanza los emuladores que ya tienes instalados. Instala primero lo que vayas a
> usar (apartado 3) y usa solo copias de juegos obtenidas legalmente.

## 1.1. Índice

1. [GlobalConsole](#1-globalconsole)
2. [Qué puede lanzar](#2-qué-puede-lanzar)
3. [Antes de empezar: instalar emuladores y launchers](#3-antes-de-empezar-instalar-emuladores-y-launchers)
4. [Preparar las carpetas de juegos](#4-preparar-las-carpetas-de-juegos)
5. [Ejecutar GlobalConsole](#5-ejecutar-globalconsole)
6. [Primera configuración paso a paso](#6-primera-configuración-paso-a-paso)
7. [Cómo funciona](#7-cómo-funciona)
8. [Controles](#8-controles)
9. [Teclado en pantalla](#9-teclado-en-pantalla)
10. [Personalización](#10-personalización)
11. [Compatibilidad y límites conocidos](#11-compatibilidad-y-límites-conocidos)
12. [Solución rápida de problemas](#12-solución-rápida-de-problemas)
13. [Desarrollo](#13-desarrollo)
14. [Documentación](#14-documentación)

## 2. Qué puede lanzar

| Categoría | Programa | Escanea juegos | Formatos / notas |
| --- | --- | --- | --- |
| PlayStation 2 | PCSX2 | Sí | `.iso` |
| PlayStation 1 | DuckStation | Sí | `.chd` `.cue` `.bin` `.iso` `.img` |
| GameCube / Wii | Dolphin | Sí | `.iso` `.rvz` `.wbfs` `.gcm` `.ciso` |
| Nintendo DS | melonDS | Sí | `.nds` |
| Nintendo 3DS | Azahar | Sí | `.3ds` |
| PlayStation 3 | RPCS3 | No | Solo abre la interfaz de RPCS3; los juegos se eligen allí |
| Tiendas de PC | Heroic Games Launcher | No | Epic, GOG y Amazon se gestionan dentro de Heroic |

Para los emuladores que escanean juegos, basta con **dejar las ISOs/ROMs en una
carpeta y seleccionarla en Ajustes** (apartado 4). Cada emulador también aparece
como **launcher** (un tile que abre su interfaz).

## 3. Antes de empezar: instalar emuladores y launchers

GlobalConsole solo lanza lo que ya está instalado, así que este paso es
imprescindible. Instala únicamente los que vayas a usar.

### 3.1. Linux (modo Automático: Flatpak)

En Linux busca cada programa por su identificador de **Flatpak**. Con
[Flatpak y Flathub](https://flathub.org/setup) configurados:

```sh
flatpak install flathub net.pcsx2.PCSX2                    # PS2
flatpak install flathub org.duckstation.DuckStation        # PS1
flatpak install flathub org.DolphinEmu.dolphin-emu         # GameCube / Wii
flatpak install flathub net.kuribo64.melonDS               # Nintendo DS
flatpak install flathub org.azahar_emu.Azahar              # Nintendo 3DS
flatpak install flathub net.rpcs3.RPCS3                    # PS3
flatpak install flathub com.heroicgameslauncher.hgl        # Epic / GOG / Amazon
```


### 3.2. Windows

Instala cada emulador con su instalador o versión portable. Después, en
**Ajustes → Ejecutables de emuladores**, elige el `.exe` con **Examinar**. En
modo Automático se busca en el PATH `pcsx2-qt.exe`, `melonds.exe`,
`Dolphin.exe`, `azahar.exe`, `DuckStation.exe` o `rpcs3.exe`. Heroic se espera en
`%LocalAppData%\Programs\heroic\Heroic.exe`.

### 3.3. Instalado por otro medio (AppImage, paquete, binario propio)

En **Ajustes → Ejecutables de emuladores** selecciona el archivo y guarda.
Tiene prioridad sobre el modo Automático; para volver a él, restáuralo.

### 3.4. Configura cada emulador una vez

Abre cada emulador por su cuenta (icono normal) y déjalo listo, porque
GlobalConsole lo lanza luego en pantalla completa y sin asistente:

1. Configura vídeo y audio.
2. Configura el mando dentro del emulador (cada uno tiene sus controles).
3. PCSX2 y DuckStation: indica la BIOS de tu propia consola.
4. Comprueba que un juego arranca desde el propio emulador.

## 4. Preparar las carpetas de juegos

GlobalConsole **no importa ni copia juegos**: lee las carpetas que tú le indiques.
El flujo es siempre el mismo:

1. **Crea una carpeta por emulador** y **coloca dentro las ISOs/ROMs** de esa
   consola (por ejemplo, todas tus `.iso` de PS2 en una carpeta `PCSX2`).
2. En **Ajustes → Carpetas de juegos**, **selecciona esa carpeta** del emulador
   correspondiente y pulsa **Guardar**.
3. GlobalConsole escanea la carpeta y **los juegos aparecen en la biblioteca**.

El nombre y la ubicación de las carpetas son libres y se recorren de forma
recursiva, así que puedes usar subcarpetas. Solo se listan las extensiones
admitidas por cada emulador.

```text
~/GlobalConsole-Juegos/
├── PCSX2/          (PS2)          → .iso
├── DuckStation/    (PS1)          → .chd, .iso, .img (o .cue + .bin)
├── Dolphin/        (GameCube/Wii) → .iso, .rvz, .wbfs, .gcm, .ciso
├── MelonDS/        (Nintendo DS)  → .nds
└── Azahar/         (Nintendo 3DS) → .3ds
```

RPCS3 y Heroic **no necesitan carpeta**: solo abren su propia interfaz.

### Carátulas

Se asocian por nombre. Lo más cómodo es una subcarpeta por juego con su imagen:

```text
PCSX2/
├── Mi Juego A/
│   ├── Mi Juego A.iso
│   └── caratula.png
└── Mi Juego B/
    ├── Mi Juego B.iso
    └── caratula.jpg
```

También valen varios juegos en una carpeta si cada imagen se llama como su
juego (`Mi Juego.iso` y `Mi Juego.jpg`). Admite `.png`, `.jpg` y `.jpeg`.
Con una carátula, pulsa **X** sobre el juego para recortarla; el recorte se
guarda como `Mi Juego.cover.png` sin afectar a otros juegos. En PS1, un `.cue`
se muestra como un juego y sus pistas `.bin` se ocultan automáticamente.

## 5. Ejecutar GlobalConsole

Necesitas un JDK compatible con el Gradle Wrapper del proyecto (desarrollado y
probado con JDK 21) y un entorno gráfico de escritorio. Desde la raíz del proyecto:

```sh
./gradlew :desktopApp:run
```

En Windows usa `gradlew.bat`. La primera compilación descarga dependencias.

Para generar un instalador de tu sistema (formatos `.deb`, `.msi` y `.dmg`; cada
uno se construye en su propio sistema operativo):

```sh
./gradlew :desktopApp:packageDeb    # Linux
./gradlew :desktopApp:packageMsi    # Windows
```

La configuración se guarda en `config.json` dentro del **directorio de
trabajo**. Conserva ese archivo para mantener tus ajustes; ejecutar desde otra
carpeta equivale a empezar de cero.

## 6. Primera configuración paso a paso

1. Abre **Ajustes** (barra superior).
2. En **Opciones y biblioteca**, activa los emuladores que usas. Se añade su
   launcher a la biblioteca al instante.
3. En **Carpetas de juegos**, elige con **Examinar** (o escribe) la carpeta donde
   dejaste las ISOs/ROMs de cada emulador: PCSX2, Melon DS, Dolphin, Azahar y
   DuckStation. Una carpeta por emulador, como se explica en el apartado 4.
4. Si instalaste algún emulador fuera de Flatpak, elígelo en **Ejecutables de
   emuladores**.
5. Pulsa **Guardar**. Las opciones se aplican al momento, pero **las rutas solo
   se aplican al guardar**.
6. Vuelve a la biblioteca: los juegos de esas carpetas aparecen tras la recarga
   automática o con **Recargar**. Cada vez que añadas o quites ISOs, pulsa
   **Recargar**.
7. Opcional: ajusta la sensibilidad del ratón del mando.

## 7. Cómo funciona

- **Biblioteca:** una cuadrícula de tiles con juegos, launchers, Heroic,
  navegadores, juegos locales y aplicaciones, que puedes agrupar por plataforma
  o familia de consolas. La barra superior ofrece **Buscar**, **Recargar**,
  **Vista** y **Ajustes**.
- **Lanzar:** al abrir un juego se ejecuta el emulador en pantalla completa con
  ese juego. GlobalConsole **suspende su propia navegación** y deja el mando al
  emulador, esperando a que el proceso termine.
- **Volver:** al cerrar el juego desde el emulador, o con **START + BACK** o el
  botón **Guía**, GlobalConsole recupera el foco y recarga la biblioteca.
- **Ratón con el mando:** el stick derecho mueve el cursor y **A** hace clic en
  launchers, melonDS, RPCS3 y Heroic. No está activo en los juegos de PCSX2,
  DuckStation, Dolphin ni Azahar.
- **Entrada dual:** el modo cambia solo entre mando y ratón según lo último que
  uses. Teclado y ratón también funcionan.

## 8. Controles

| Botón | En la biblioteca |
| --- | --- |
| Cruceta / stick izquierdo | Mover el foco |
| **A** | Abrir el elemento enfocado o activar el botón |
| **B** | Cerrar el diálogo abierto |
| **X / □** | Recortar la carátula del elemento |
| **Y / △** | Ir a **Buscar** (en diálogos, a Guardar o Cerrar) |
| Stick derecho | Cursor del ratón |

| Durante un juego | Acción |
| --- | --- |
| **START + BACK** | Volver a GlobalConsole y cerrar el proceso lanzado |
| **Guía** | Volver a GlobalConsole |
| **START + R2** | Abrir o cerrar el teclado en pantalla |
| **X + BACK** | Ocultar o mostrar el cursor (donde hay ratón) |

Un diálogo abierto recibe el mando en exclusiva. Detalle completo en la
[guía del mando](docs/guias/02_uso_del_mando.md).

## 9. Teclado en pantalla

- **Búsqueda:** desde **Buscar** filtra la biblioteca mientras escribes.
- **Teclado flotante:** **START + R2** (o un clic **A** sobre un campo de texto)
  abre un teclado siempre visible que escribe en la ventana activa (por ejemplo
  en Heroic o en un launcher) sin quitarle el foco. **START** envía el texto con
  Intro y **B** cierra.

Más información en la [guía del teclado](docs/guias/05_teclado_en_pantalla.md).

## 10. Personalización

En **Vista** puedes cambiar, con el mando y se guarda solo, la agrupación, la
posición de los launchers, el color del brillo y su intensidad (0 a 100). El
fondo siempre es oscuro.

## 11. Compatibilidad y límites conocidos

| Tema | Detalle |
| --- | --- |
| Linux | Plataforma principal, verificada con mando físico y emuladores reales. |
| Windows | Rutas y comandos cubiertos por pruebas; falta validación real en un equipo Windows. |
| Wayland | El teclado flotante usa AWT Robot y no está garantizado; usa una sesión X11 si lo necesitas. |
| Mandos | Se requiere un mando compatible con el estándar de GLFW/SDL. Solo el HJDX tiene corrección de gatillos intercambiados. |
| PS3 | Solo launcher de RPCS3; no escanea juegos. |
| Duplicados PS1 | El mismo juego en CHD y CUE crea dos entradas; conserva un formato. |
| macOS | El proyecto define un paquete `.dmg`, pero la detección de emuladores y el lanzamiento solo están implementados para Linux y Windows. |

## 12. Solución rápida de problemas

- **No aparecen juegos:** comprueba que activaste el emulador, que la carpeta
  existe, que pulsaste **Guardar** y que usaste **Recargar**.
- **El emulador no arranca:** verifica con `flatpak info <identificador>` que
  está instalado, o elige su ejecutable en Ajustes. Comprueba que el juego
  funciona abriéndolo en el propio emulador y lanza GlobalConsole desde una
  terminal para ver los errores.
- **START + R2 no abre el teclado:** pulsa ambos a la vez; si tu mando invierte
  los gatillos, abre el teclado con un clic **A** sobre un campo de texto.
- **No vuelvo a GlobalConsole:** pulsa **START + BACK** o **Guía**.

Lista completa en la [solución de problemas](docs/guias/06_solucion_de_problemas.md).

## 13. Desarrollo

```sh
./gradlew :desktopApp:run      # ejecutar
./gradlew :desktopApp:test     # pruebas (directorio de trabajo aislado)
```

El proyecto sigue Clean Architecture (MVVM): dominio con casos de uso, datos con
repositorios y adaptadores, y presentación con ViewModels y Compose. Las pruebas
se escriben sobre los casos de uso (TDD). Antes de contribuir, lee las
[reglas del proyecto](.agents/AGENTS.md): commits en español con prefijo `[GC]`,
ramas `feature/`, `bugfix/` u `hotfix/`, y documentación actualizada con el
código.

## 14. Documentación

- **Guías de usuario:** [índice](docs/guias/README.md) con instalación, mando,
  biblioteca y ajustes, emuladores, teclado y solución de problemas.
- **Documentación técnica:** [índice](docs/README.md) y
  [arquitectura](docs/01_arquitectura.md).

Las pruebas automáticas no sustituyen la comprobación física con cada emulador,
sistema operativo y mando.

@author Daniel Figueroa Vidal
@since 2026-10-12
