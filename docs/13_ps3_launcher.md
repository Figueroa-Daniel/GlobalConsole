# 13. PS3 Launcher (RPCS3) — Integración

Este documento describe la integración del emulador de PlayStation 3 (RPCS3) en GlobalConsole.
Se implementa exclusivamente como un Launcher, es decir, no se encarga de extraer ni escanear juegos de PS3 localmente, sino de lanzar la interfaz del emulador (RPCS3) permitiendo el uso del ratón simulado para interactuar con su UI.

---

## 1. Flujo de Ejecución

```
[HomeScreen — usuario selecciona "RPCS3"]
       ↓
[HomeViewModel.onGameSelected(game)]
       ↓
[game.platform == Platforms.PS3]
       ↓
[ExecutePS3LauncherUseCase.invoke()]
       ↓
[LauncherPS3Adapter.executeLauncher()]
       ↓
       ├── Linux  → isInstalledOnLinux() → executeOnLinux()
       │                                   flatpak run net.rpcs3.RPCS3
       │
       └── Windows → TODO (Pendiente de implementar, no ejecutable directamente por ahora)
       │
       ▼
[process.waitFor()]  ← Bloquea el hilo hasta que RPCS3 se cierre
       ↓
[HomeViewModel reanuda → loadGames()]
       ↓
[GlobalConsole UI]
```

> **Nota sobre el control:** Durante la ejecución del emulador (estado `GameRunning` con plataforma `PS3`), se permite explícitamente el uso del modo ratón en el gamepad (`isMouseAllowedWhenSuspended = true`) para poder navegar por la interfaz de RPCS3.

---

## 2. Flujo de Visibilidad en Biblioteca

El usuario puede habilitar o deshabilitar la aparición del launcher de PS3 en su biblioteca principal desde el diálogo de configuración (`SetupPathDialog`).

```
[SetupPathDialog — usuario activa toggle de PS3]
       ↓
[SettingsViewModel.setPs3Enabled(true)]
       ↓
[EnablePS3LauncherUseCase.invoke()]
       ↓
[PS3LauncherRepository.savePs3Enabled(true)]
       ↓
[PS3LauncherRepositoryImpl → escribe config.json (ps3Enabled = true)]
```

---

## 3. Arquitectura por Capas

La arquitectura sigue el mismo diseño que `HeroicGames`.

### 3.1 Capa de Datos

- **`LauncherPS3Adapter`**: Ejecuta nativamente `flatpak run net.rpcs3.RPCS3` en Linux. La ejecución en Windows está marcada como TODO por limitación técnica de ejecución directa.
- **`PS3LauncherDto`**: DTO con identificador `ps3-launcher`, nombre `RPCS3` y url de ejecución correspondiente.
- **`PS3LauncherRepository` / `PS3LauncherRepositoryImpl`**: Encargados de gestionar la persistencia en `AppConfig` (`ps3Enabled`) y delegar la ejecución al adapter.
- **`Mappers.kt`**: Mapea el DTO a entidad de dominio `PS3Launcher`.

### 3.2 Capa de Dominio

- **Entidad `PS3Launcher`**: Representa el juego/launcher en la capa de dominio con la plataforma `Platforms.PS3`.
- **Use Cases**:
  - `FindPS3LauncherUseCase`: Devuelve si el launcher está habilitado.
  - `EnablePS3LauncherUseCase`: Habilita el launcher.
  - `HidePS3LauncherUseCase`: Oculta el launcher.
  - `ShowPS3LauncherUseCase`: Retorna la entidad para ser mostrada en la UI.
  - `ExecutePS3LauncherUseCase`: Llama a la ejecución.
  - `ClosePS3LauncherUseCase`: Cierra forzosamente el proceso activo si lo hay.

### 3.3 Capa de Presentación

- **`HomeViewModel`**: Inyecta los Use Cases para obtener el launcher si está habilitado y añadirlo a la biblioteca general. Controla el lanzamiento mediante `ExecutePS3LauncherUseCase`.
- **`SettingsViewModel`**: Gestiona el toggle desde la UI mediante `Enable` / `Hide` UseCases.

---

## 4. Inyección de Dependencias (Koin)

Los componentes de `PS3Launcher` están registrados en `DataModule.kt` y `DomainModule.kt` como `single` o `factory` siguiendo el estándar del proyecto.

---

## 5. Referencias Cruzadas

- Arquitectura general de capas: [01_arquitectura.md](01_arquitectura.md)
- Heroic Games Launcher (base arquitectónica): [09_heroic_games_launcher.md](09_heroic_games_launcher.md)
- Tecnologías y dependencias: [02_tecnologias.md](02_tecnologias.md)
- Estructura modular del proyecto: [03_modulos.md](03_modulos.md)

---

@author Daniel Figueroa Vidal
@since 2026-08-22
