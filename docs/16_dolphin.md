# Integración de Dolphin Emulator

Este documento describe la integración del emulador Dolphin en GlobalConsole para la ejecución de juegos de Nintendo GameCube y Wii.

## Arquitectura y Ejecución
La integración de Dolphin sigue la misma estructura Clean Architecture definida para el proyecto (ver [01_arquitectura.md](01_arquitectura.md) y [03_modulos.md](03_modulos.md)).

La ejecución se realiza mediante procesos nativos del sistema operativo de manera similar a PCSX2 (ver [04_pcsx2.md](04_pcsx2.md)), asegurando el correcto ciclo de vida:
1. **Lanzamiento:** Creación de un proceso mediante comandos de consola para invocar a Dolphin.
2. **Espera:** Se bloquea el hilo con Coroutines para esperar a que el emulador se cierre de forma natural o forzada.
3. **Retorno:** Devolución limpia del foco a la interfaz principal de GlobalConsole (ver [12_manejo_gamepad_y_foco.md](12_manejo_gamepad_y_foco.md)).

## Tecnologías Implicadas
- Se emplean **Coroutines** para la gestión asíncrona del proceso (ver [02_tecnologias.md](02_tecnologias.md)).
- La inyección de dependencias está manejada por Koin (ver [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md)).
- Las rutas de Dolphin se persisten utilizando Settings (ver [08_persistencia_configuracion.md](08_persistencia_configuracion.md)).

## Flujo de Datos
- Las carátulas y las rutas a los archivos ISO/ROM de GameCube o Wii se procesan utilizando las utilidades de manejo de imágenes (ver [10_manejo_imagenes_y_caratulas.md](10_manejo_imagenes_y_caratulas.md)).
