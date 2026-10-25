# Integración de DuckStation Emulator

Este documento detalla la integración del emulador DuckStation en GlobalConsole para la ejecución de juegos de PlayStation 1.

## 1. Arquitectura y Ejecución
DuckStation se incorpora siguiendo los principios de Clean Architecture del proyecto (ver [01_arquitectura.md](01_arquitectura.md) y [03_modulos.md](03_modulos.md)).

El modelo de ejecución replica la gestión por procesos nativos utilizada en otros módulos como Heroic Games Launcher o PCSX2 (ver [09_heroic_games_launcher.md](09_heroic_games_launcher.md) y [04_pcsx2.md](04_pcsx2.md)).
Las fases principales son:
1. **Lanzamiento:** Invocación del ejecutable de DuckStation de forma nativa enviando los parámetros necesarios (tanto para arrancar la ISO como para arrancar su launcher).
2. **Espera:** Pausado asíncrono con Coroutines y `Process.waitFor()` hasta que la ventana y el proceso finalicen.
3. **Retorno de Foco:** Manejo específico para recuperar la UI y devolver el control al gamepad (ver [12_manejo_gamepad_y_foco.md](12_manejo_gamepad_y_foco.md)).

## 2. Tecnologías Implicadas
- **Coroutines y ProcessBuilder** para la ejecución y gestión asíncrona (ver [02_tecnologias.md](02_tecnologias.md)).
- Resolución de dependencias mediante Koin (ver [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md)).
- Almacenamiento de preferencias y configuración utilizando la capa de persistencia (ver [08_persistencia_configuracion.md](08_persistencia_configuracion.md)).

## 3. Correcciones de biblioteca y ejecutable (2026-10-12)

El escaneo excluye pistas BIN/IMG referenciadas por un CUE y mantiene los archivos
independientes. Las carátulas se asocian por nombre; una imagen genérica solo se
aplica a carpetas con un juego. Ya no existe una ruta AppImage personal fija:
el ejecutable se elige en Ajustes y, sin selección, se usa Flatpak en Linux o
DuckStation.exe en PATH en Windows. El borrado no implementado devuelve false.
Véase [revisión y pruebas](26_revision_emuladores_y_biblioteca.md).
