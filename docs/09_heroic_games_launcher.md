# 09. Heroic Games Launcher — Integración Completa

Este documento describe la integración completa de Heroic Games Launcher en GlobalConsole:
detección de SO, arquitectura de módulo propio, ejecución nativa y gestión de visibilidad
en la biblioteca principal.

---

## 1. Flujo de Ejecución

```
[HomeScreen — usuario selecciona "Heroic Games"]
       ↓
[HomeViewModel.onGameSelected(game)]
       ↓
[game.platform == Platforms.HEORIC_GAMES_LAUCHER]
       ↓
[ExecuteHGLauncherUseCase.invoke()]
       ↓
[LauncherHeroicGamesAdapter.executeLauncher()]
       ↓
       ├── Linux  → isInstalledOnLinux() → executeOnLinux()
       │                                   flatpak run com.heroicgameslauncher.hgl --fullscreen
       │
       └── Windows → isInstalledOnWindows() → executeOnWindows()
                                              %LOCALAPPDATA%\Programs\heroic\Heroic.exe --fullscreen
       │
       ▼
[process.waitFor()]  ← Bloquea el hilo hasta que Heroic se cierre
       ↓
[HomeViewModel reanuda → loadGames()]
       ↓
[GlobalConsole UI]
```

---

## 2. Flujo de Visibilidad en Biblioteca

```
[SetupPathDialog — usuario activa toggle de Heroic]
       ↓
[SettingsViewModel.setHeroicEnabled(true)]
       ↓
[EnableHGLauncherUseCase.invoke()]
       ↓
[HGLauncherRepository.saveHeroicEnabled(true)]
       ↓
[HGLauncherRepositoryImpl → escribe config.json]

[HomeViewModel.loadGames()]
       ↓
[FindHGLauncherUseCase.invoke()] → true
       ↓
[ShowHGLauncherUseCase.invoke()]
       ↓
[HGLauncherRepository.showHGLauncher()] → HGLauncherDto
       ↓
[HGLauncherDto.toDomain()] → HGLauncher (entidad de dominio)
       ↓
[HGLauncher se añade a la lista de juegos]
       ↓
[HomeUiState.Success — biblioteca con Heroic incluido]
```

---

## 3. Arquitectura por Capas

La implementación sigue estrictamente **Clean Architecture (MVVM)** y separa
la lógica de Heroic Games Launcher en su propio módulo, independiente de `settings`.

> **Principio de diseño:** Todo lo relacionado con Heroic Games Launcher vive en el módulo
> `HeroicGames/`. La capa `settings/` es responsable únicamente de las rutas de emuladores
> (ej. PCSX2). Ver motivación técnica en [08_persistencia_configuracion.md](08_persistencia_configuracion.md).

### 3.1 Capa de Datos

#### `LauncherHeroicGamesAdapter`
(`HeroicGames/data/database/LauncherHeroicGamesAdapter.kt`)

Adaptador que gestiona toda la interacción con el sistema operativo para la ejecución del proceso.

| Método | Responsabilidad |
|---|---|
| `executeLauncher()` | Detecta el SO y delega. Marcado `open` para herencia en tests. |
| `isInstalledOnLinux()` | `flatpak info com.heroicgameslauncher.hgl` → exit code 0 = instalado. |
| `executeOnLinux()` | `flatpak run com.heroicgameslauncher.hgl --fullscreen` + `waitFor()`. |
| `isInstalledOnWindows()` | `cmd /c if exist "%LocalAppData%\Programs\heroic\Heroic.exe"`. |
| `executeOnWindows()` | Ruta absoluta via `System.getenv("LOCALAPPDATA")` + `--fullscreen` + `waitFor()`. |

#### `HGLauncherDto`
(`HeroicGames/data/dto/HGLauncherDto.kt`)

DTO serializable con los datos del launcher: `id`, `name`, `urlGameExecute` e `image`.

#### `HGLauncherRepository` (interfaz)
(`HeroicGames/data/repository/HGLauncherRepository.kt`)

Contrato del repositorio del módulo. Centraliza todas las operaciones de Heroic:

| Método | Responsabilidad |
|---|---|
| `isHGLauncherInstalled()` | Detecta si el launcher está instalado. |
| `showHGLauncher()` | Retorna el `HGLauncherDto` con los datos del launcher. |
| `hideHGLauncher()` | Oculta Heroic de la biblioteca persistiendo `enabled=false`. |
| `executeHGLauncher()` | Delega la ejecución al adaptador nativo. |
| `isHeroicEnabled()` | Recupera la preferencia de visibilidad del usuario. |
| `saveHeroicEnabled(Boolean)` | Persiste la preferencia de visibilidad del usuario. |

#### `HGLauncherRepositoryImpl`
(`HeroicGames/data/repositoryImpl/HGLauncherRepositoryImpl.kt`)

Implementación del repositorio. Persiste la preferencia `heroicEnabled` en `config.json`
utilizando el modelo compartido [`AppConfig`](../desktopApp/src/main/kotlin/org/example/globalconsole/config/AppConfig.kt).
Lee el objeto completo antes de escribir para preservar los campos de otros módulos:

```kotlin
// Patrón de escritura segura
val current = readConfig()                          // lee AppConfig completo
val updated = current.copy(heroicEnabled = enabled) // modifica SOLO heroicEnabled
configFile.writeText(json.encodeToString(updated))  // reescribe AppConfig completo
```

#### Mapper
(`HeroicGames/data/mappers/Mappers.kt`)

```kotlin
fun HGLauncherDto.toDomain() = HGLauncher(
    id = id,
    name = name,
    urlGameExecute = urlGameExecute,
    image = image,
    platform = Platforms.HEORIC_GAMES_LAUCHER
)
```

---

### 3.2 Capa de Dominio

#### Entidad: `HGLauncher`
(`HeroicGames/domain/entitys/HGLauncher.kt`)

Extiende `Game` con la plataforma `Platforms.HEORIC_GAMES_LAUCHER`. Es la entidad
que fluye a través del ViewModel hasta la UI.

#### Use Cases

| Clase | Responsabilidad |
|---|---|
| `FindHGLauncherUseCase` | Consulta `isHeroicEnabled()`. Reemplaza `IsHeroicEnabledUseCase` de settings. |
| `EnableHGLauncherUseCase` | Llama a `saveHeroicEnabled(true)`. Activa Heroic en biblioteca. |
| `HideHGLauncherUseCase` | Llama a `hideHGLauncher()` → `saveHeroicEnabled(false)`. Oculta Heroic. |
| `ShowHGLauncherUseCase` | Llama a `showHGLauncher().toDomain()`. Obtiene entidad de dominio. |
| `ExecuteHGLauncherUseCase` | Llama a `executeHGLauncher()`. Lanza el proceso nativo. |

> **¿Por qué use cases separados para Enable y Hide?** Principio de Responsabilidad Única (SRP):
> cada use case representa una sola intención del usuario. Facilita el testing independiente
> y documenta explícitamente el contrato de cada operación.

Todos los use cases son `open` para permitir implementaciones Fake sin frameworks de mocking.

---

### 3.3 Capa de Presentación

#### `HomeViewModel`
(`presesentation/viewModel/home/HomeViewModel.kt`)

Recibe por inyección `FindHGLauncherUseCase` y `ShowHGLauncherUseCase`. En `loadGames()`:

```kotlin
val heroicEntry: List<Game> = if (findHGLauncherUseCase?.invoke() == true) {
    val launcher = showHGLauncherUseCase?.invoke()
    if (launcher != null) listOf(launcher) else emptyList()
} else {
    emptyList()
}
```

> **Decisión de diseño:** El ViewModel no construye el objeto `HGLauncher` directamente.
> Delega la obtención de datos al `ShowHGLauncherUseCase`, respetando Clean Architecture.

#### `SettingsViewModel`
(`presesentation/viewModel/settings/SettingsViewModel.kt`)

Recibe por inyección `FindHGLauncherUseCase`, `EnableHGLauncherUseCase` y `HideHGLauncherUseCase`.
El método `setHeroicEnabled(enabled)` despacha al use case correcto:

```kotlin
fun setHeroicEnabled(enabled: Boolean) {
    viewModelScope.launch {
        if (enabled) enableHGLauncherUseCase()
        else hideHGLauncherUseCase()
        _heroicEnabled.value = enabled
    }
}
```

---

## 4. Inyección de Dependencias (Koin)

Consultar la guía completa en [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md).

### 4.1 DataModule

```kotlin
// Adaptador nativo — single porque no tiene estado mutable
single { LauncherHeroicGamesAdapter() }

// Repositorio expuesto por su interfaz
single<HGLauncherRepository> { HGLauncherRepositoryImpl(adapter = get()) }
```

### 4.2 DomainModule

```kotlin
// Ejecución
factory { ExecuteHGLauncherUseCase(adapter = get()) }

// Datos del launcher
factory { ShowHGLauncherUseCase(repository = get()) }

// Gestión de visibilidad en biblioteca
factory { FindHGLauncherUseCase(repository = get()) }
factory { EnableHGLauncherUseCase(repository = get()) }
factory { HideHGLauncherUseCase(repository = get()) }
```

---

## 5. Pruebas Unitarias (TDD)

### 5.1 `ExecuteHGLauncherUseCaseTest`
(`test/.../HeroicGames/domain/usecase/ExecuteHGLauncherUseCaseTest.kt`)

Usa `FakeHeroicLauncherAdapter` (hereda de `LauncherHeroicGamesAdapter`) para controlar el resultado.

| Test | Valida |
|---|---|
| `whenLaunchSucceeds_returnsTrue` | UseCase retorna `true` cuando el adaptador tiene éxito. |
| `whenLaunchFails_returnsFalse` | UseCase retorna `false` cuando el adaptador reporta fallo. |
| `whenInvoked_callsAdapterExecute` | UseCase delega la llamada al adaptador. |

### 5.2 `HGLauncherVisibilityUseCasesTest`
(`test/.../HeroicGames/domain/usecase/HGLauncherVisibilityUseCasesTest.kt`)

Usa `FakeHGLauncherRepository` (implementación en memoria del repositorio completo).

| Test | Valida |
|---|---|
| `findHGLauncher_whenDisabled_returnsFalse` | Estado inicial = false por defecto. |
| `findHGLauncher_whenEnabled_returnsTrue` | Retorna true cuando está habilitado. |
| `enableHGLauncher_setsHeroicEnabledToTrue` | Persiste `true` en el repositorio. |
| `hideHGLauncher_setsHeroicEnabledToFalse` | Persiste `false` en el repositorio. |
| `hideHGLauncher_onSuccess_returnsTrue` | Retorna `true` si la operación es exitosa. |
| `hideHGLauncher_onFailure_returnsFalse` | Retorna `false` si el repositorio falla. |
| `showHGLauncher_returnsMappedDomainEntity` | Entidad mapeada con todos los campos correctos. |
| `fullVisibilityCycle_enableThenHide_stateIsConsistent` | Ciclo completo enable→find→hide→find. |

### 5.3 `AppConfigIntegrationTest`
(`test/.../config/AppConfigIntegrationTest.kt`)

Verifica que `SettingsRepositoryImpl` y `HGLauncherRepositoryImpl` no se destruyen
mutuamente al escribir en `config.json` (regresión del Bug 1).

| Test | Valida |
|---|---|
| `saveEmulatorPath_afterHeroicEnabled_preservesHeroicEnabled` | Guardar ruta no borra `heroicEnabled`. |
| `saveHeroicEnabled_afterEmulatorPathSaved_preservesEmulatorPath` | Activar Heroic no borra la ruta del emulador. |
| `disableHeroic_doesNotDestroyEmulatorPath` | Deshabilitar Heroic no destruye las rutas. |
| `interleavedWrites_maintainFullConsistency` | Escrituras intercaladas mantienen coherencia. |
| `hideHGLauncher_preservesEmulatorPath` | `hideHGLauncher()` no destruye las rutas del emulador. |

### 5.4 `HomeViewModelTest` (tests de Heroic)
(`test/.../presesentation/viewModel/home/HomeViewModelTest.kt`)

Usa `FakeFindHGLauncherUseCase` y `FakeShowHGLauncherUseCase`.

| Test | Valida |
|---|---|
| `loadGames_withHeroicEnabled_includesHeroicInGamesList` | Heroic aparece en lista cuando está activo. |
| `loadGames_withHeroicDisabled_doesNotIncludeHeroicInGamesList` | Heroic no aparece cuando está inactivo. |

---

## 6. Detalles por Sistema Operativo

### 6.1 Linux (Flatpak)

- **Verificación:** `flatpak info com.heroicgameslauncher.hgl`
  - Exit code `0` = instalado.
- **Ejecución:** `flatpak run com.heroicgameslauncher.hgl --fullscreen`
- **¿Por qué Flatpak?** Es la distribución estándar en plataformas portátiles Linux (Steam Deck).

### 6.2 Windows

- **Verificación:** `cmd /c if exist "%LocalAppData%\Programs\heroic\Heroic.exe" (echo 1) else (echo 0)`
- **Ejecución:** `System.getenv("LOCALAPPDATA") + "\Programs\heroic\Heroic.exe" --fullscreen`
- **¿Por qué esta ruta?** Es la ruta del instalador oficial de Heroic Games Launcher para Windows.

---

## 7. Integración en la UI

### 7.1 Toggle de Visibilidad en Configuración

El diálogo `SetupPathDialog` incluye un `Switch` de Material3 para activar/desactivar
Heroic en la biblioteca. La preferencia se persiste en `config.json` (campo `heroicEnabled`)
a través del `SettingsViewModel` → `EnableHGLauncherUseCase` / `HideHGLauncherUseCase`.

**Navegación por gamepad:**

| D-Pad | Acción |
|---|---|
| UP | Mueve el foco a la sección del toggle de Heroic |
| DOWN | Mueve el foco a la sección de botones de PCSX2 |
| LEFT/RIGHT | Navega entre EXAMINAR → CANCELAR → GUARDAR |
| A (CONFIRM) | Activa el toggle si está enfocado; confirma la acción del botón activo |

### 7.2 Entrada en la Biblioteca Principal

Cuando el toggle está activo, `HomeViewModel.loadGames()` llama a `ShowHGLauncherUseCase`
para obtener el `HGLauncher` mapeado desde el repositorio, que se mezcla con los juegos
de PCSX2 en la lista ordenada por nombre.

Al seleccionarlo, `onGameSelected()` detecta `Platforms.HEORIC_GAMES_LAUCHER`
y delega a `ExecuteHGLauncherUseCase`.

---

## 8. Referencias Cruzadas

- Arquitectura general de capas: [01_arquitectura.md](01_arquitectura.md)
- Tecnologías y dependencias (Coroutines, ProcessBuilder): [02_tecnologias.md](02_tecnologias.md)
- Estructura modular del proyecto: [03_modulos.md](03_modulos.md)
- Patrón de ejecución de proceso nativo (referencia PCSX2): [04_pcsx2.md](04_pcsx2.md)
- Guía de inyección de dependencias Koin: [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md)
- Persistencia de configuración del proyecto: [08_persistencia_configuracion.md](08_persistencia_configuracion.md)
- Contexto general de la IA: [05_contexto_ia.md](05_contexto_ia.md)

---

@author Daniel Figueroa Vidal
@since 2026-08-12
@updated 2026-08-13
