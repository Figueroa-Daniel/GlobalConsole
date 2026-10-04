# 20. Persistencia de la Configuración de Vista

Índice
1. [Problema](#1-problema)
2. [Decisión técnica](#2-decisión-técnica)
3. [Arquitectura](#3-arquitectura)
4. [Flujo](#4-flujo)
5. [Tests](#5-tests)
6. [Referencias cruzadas](#6-referencias-cruzadas)

---

## 1. Problema
La configuración del diálogo de vista (modo de agrupación y posición de los launchers) vivía solo en memoria dentro de `HomeViewModel`. Al reiniciar la aplicación se perdía y el usuario debía reconfigurarla en cada sesión.

## 2. Decisión técnica
Se reutiliza el mecanismo ya existente de `config.json` (`AppConfig` + patrón de escritura segura, ver [08_persistencia_configuracion.md](08_persistencia_configuracion.md)) en lugar de introducir un nuevo almacenamiento: no añade dependencias y mantiene un único punto de configuración.

Los valores se guardan como `String` (nombre del enum) en `AppConfig` porque `GroupingMode` y `LauncherPosition` pertenecen a la capa de presentación y el dominio no debe depender de ella. El `HomeViewModel` convierte a enum con `valueOf` seguro; ante un valor desconocido o ausente usa los valores por defecto (`NONE` / `INLINE`).

## 3. Arquitectura
```
config/AppConfig.kt                  ← nuevos campos groupingMode, launcherPosition
settings/domain/ViewSettings.kt      ← entidad de dominio (Strings)
settings/domain/SettingsRepository   ← saveViewSettings() / getViewSettings()
settings/domain/usecase/
├── SaveViewSettingsUseCase.kt
└── GetViewSettingsUseCase.kt
settings/data/SettingsRepositoryImpl ← lee/escribe config.json
presentation/viewModel/home/HomeViewModel ← carga al iniciar y guarda al cambiar
```

## 4. Flujo
```
[ViewSettingsDialog] → HomeViewModel.setGroupingMode / setLauncherPosition
        ↓
[SaveViewSettingsUseCase] → SettingsRepositoryImpl → config.json

[HomeViewModel.loadGames] → (primera vez) GetViewSettingsUseCase → aplica modos → construye lista
```
La carga ocurre antes de construir la primera lista para que la biblioteca aparezca ya agrupada.

## 5. Tests
TDD exclusivo sobre UseCases: `ViewSettingsUseCasesTest` (valores por defecto y persistencia tras guardar) usando `FakeSettingsRepository`.

## 6. Referencias cruzadas
- [01_arquitectura.md](01_arquitectura.md)
- [08_persistencia_configuracion.md](08_persistencia_configuracion.md)
- [12_manejo_gamepad_y_foco.md](12_manejo_gamepad_y_foco.md)
- [19_contexto_ia.md](19_contexto_ia.md)

@author Daniel Figueroa Vidal
@since 2026-10-04
