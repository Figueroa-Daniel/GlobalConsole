# Integración de Azahar Emulator (Nintendo 3DS)

Este documento documenta la integración del emulador Azahar para la plataforma Nintendo 3DS dentro de GlobalConsole.

## Arquitectura y Ejecución
La integración de Azahar se implementa siguiendo el patrón estándar del proyecto y la Clean Architecture, al igual que los demás módulos (ver [01_arquitectura.md](01_arquitectura.md)).

Su ejecución está automatizada bajo el mismo esquema de ciclo de vida que procesos nativos como MelonDS (ver [05_melonds.md](05_melonds.md)) o PCSX2:
1. **Lanzamiento nativo:** Llamada al binario de Azahar mediante subprocesos enviando las opciones para carga rápida y pantalla completa.
2. **Ejecución y Bloqueo:** Uso de `Process.waitFor()` y Coroutines para bloquear la interacción del launcher principal mientras el juego está activo.
3. **Restauración:** Cierre limpio del proceso y recuperación del control total por parte del gamepad en GlobalConsole (ver [12_manejo_gamepad_y_foco.md](12_manejo_gamepad_y_foco.md)).

## Tecnologías y Componentes
- Al igual que el resto de los módulos, se inyectan repositorios, casos de uso y modelos de datos utilizando Koin (ver [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md)).
- La gestión de rutas base de Azahar se administra mediante la persistencia general del proyecto (ver [08_persistencia_configuracion.md](08_persistencia_configuracion.md)).
