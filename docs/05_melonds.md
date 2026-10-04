# 05. Integración de Melon DS en GlobalConsole

Este documento detalla el mecanismo utilizado en GlobalConsole para integrar y ejecutar el emulador Melon DS (Nintendo DS) en sistemas operativos Windows y Linux de forma nativa, basándose en la arquitectura existente para emuladores.

---

## ⚙️ 1. Flujo de Ejecución

La comunicación con emuladores externos debe hacerse a través de procesos nativos del sistema operativo (`ProcessBuilder`), controlando el ciclo de vida de la aplicación de extremo a extremo. Para Melon DS, se han implementado dos flujos:

### Flujo del Juego
El juego se lanza de forma análoga a [PCSX2](04_pcsx2.md):
```
[GlobalConsole UI] ──> Lanza Proceso MelonDS (-f <ruta_rom>) ──>
                     [UI de GlobalConsole queda en segundo plano]
                     ──> Espera a la terminación del Proceso (waitFor) ──>
                     [Retorno limpio a GlobalConsole UI]
```

### Flujo del Launcher
Dado que Melon DS cuenta con un launcher propio que resulta útil abrir, se permite lanzarlo directamente:
```
[GlobalConsole UI] ──> Lanza Proceso Launcher MelonDS ──>
                     [UI de GlobalConsole queda en segundo plano (bloqueada)]
                     ──> Espera a la terminación del Proceso (waitFor) ──>
                     [Retorno limpio a GlobalConsole UI]
```

---

## 🖥️ 2. Detalles por Sistema Operativo

Los adaptadores `GameMelonDSAdapter` y `LauncherMelonDSAdapter` detectan automáticamente el sistema operativo y aplican el comando adecuado.

### Linux (Flatpak)
- **Launcher:** `flatpak run net.kuribo64.melonDS`
- **Juego:** `flatpak run net.kuribo64.melonDS -f <ruta_rom>`
- **Cierre del Launcher:** `process.destroy()` sobre el proceso de Flatpak.

### Windows
- **Launcher:** `<ROUTE_MELONDS_EXECUTABLE>`
- **Juego:** `<ROUTE_MELONDS_EXECUTABLE> -f <ruta_rom>`
- **Cierre del Launcher:** `process.destroy()` sobre el proceso nativo.

---

## 🗄️ 3. Adaptadores de Datos de Melon DS

- **GameMelonDSAdapter:** Responsable del lanzamiento y control de la ejecución del juego a pantalla completa. Bloquea el hilo con `waitFor()`.
- **LauncherMelonDSAdapter:** Responsable de lanzar el emulador sin un juego específico. Bloquea el hilo con `waitFor()` de la misma forma que los juegos, permitiendo que GlobalConsole suspenda sus inputs hasta que el launcher finalice. Sigue permitiendo `closeLauncher()` en caso de cierre forzado.
- **MelonDSRepositoryImpl:** Une los adaptadores y provee los métodos a los *UseCases* de la capa de dominio respetando Clean Architecture.

---

## 🔗 4. Referencias Cruzadas
- Conocer la arquitectura general de capas en [01_arquitectura.md](01_arquitectura.md).
- Revisar la integración del emulador base (PCSX2) en [04_pcsx2.md](04_pcsx2.md).
