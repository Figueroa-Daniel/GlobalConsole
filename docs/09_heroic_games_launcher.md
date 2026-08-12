# 09. Integración de Heroic Games Launcher en GlobalConsole

Este documento detalla la arquitectura, el flujo de ejecución y las decisiones técnicas
tomadas para integrar Heroic Games Launcher como launcher nativo dentro de GlobalConsole,
siguiendo el mismo patrón establecido para PCSX2 (ver [04_pcsx2.md](04_pcsx2.md)).

---

## 1. Propósito y Alcance

Heroic Games Launcher es un lanzador de código abierto compatible con las tiendas Epic Games
y GOG en sistemas Linux y Windows. Su integración en GlobalConsole permite al usuario acceder
a su biblioteca de juegos de PC desde la interfaz 10-foot UI sin salir de la aplicación.

El sistema implementado cubre:

1. **Detección del SO:** Diferencia entre Linux y Windows para aplicar el mecanismo correcto.
2. **Verificación de instalación:** Comprueba que el launcher está disponible antes de intentar lanzarlo.
3. **Ejecución controlada:** Lanza el proceso en modo pantalla completa y bloquea el hilo hasta que el usuario cierre el launcher, permitiendo el retorno limpio a GlobalConsole.

---

## 2. Flujo de Ejecución

```
[GlobalConsole UI]
       │
       ▼
[HomeViewModel.onGameSelected(game: HGLauncher)]
       │
       ▼
[ExecuteHGLauncherUseCase.invoke()]  ← Dispatchers.IO
       │
       ▼
[LauncherHeroicGamesAdapter.executeLauncher()]
       │
       ├── Linux  → isInstalledOnLinux() → executeOnLinux()
       │                                   flatpak run com.heroicgameslauncher.hgl --fullscreen
       │
       └── Windows → isInstalledOnWindows() → executeOnWindows()
                                              %LOCALAPPDATA%\Programs\heroic\Heroic.exe --fullscreen
       │
       ▼
[process.waitFor()]  ← Bloquea hilo hasta que Heroic se cierre
       │
       ▼
[HomeViewModel reanuda → loadGames()]
       │
       ▼
[GlobalConsole UI]
```

---

## 3. Arquitectura por Capas

La implementación sigue estrictamente la **Clean Architecture (MVVM)** definida en
[01_arquitectura.md](01_arquitectura.md).

### 3.1 Capa de Datos

**`LauncherHeroicGamesAdapter`**
(`HeroicGames/data/database/LauncherHeroicGamesAdapter.kt`)

Adaptador responsable de toda la interacción con el sistema operativo. Expone un único
método público (`executeLauncher()`) y gestiona internamente la bifurcación por SO:

| Método | Responsabilidad |
|---|---|
| `executeLauncher()` | Detecta el SO y delega. Marcado `open` para herencia en tests. |
| `isInstalledOnLinux()` | `flatpak info com.heroicgameslauncher.hgl` → exit code 0 = instalado. |
| `executeOnLinux()` | `flatpak run com.heroicgameslauncher.hgl --fullscreen` + `waitFor()`. |
| `isInstalledOnWindows()` | Comando `cmd /c if exist "%LocalAppData%\Programs\heroic\Heroic.exe"`. |
| `executeOnWindows()` | Ruta absoluta via `System.getenv("LOCALAPPDATA")` + `--fullscreen` + `waitFor()`. |

> **¿Por qué `open`?** La clase y su método principal son `open` para permitir que los tests
> unitarios hereden y sobreescriban `executeLauncher()` sin frameworks de mocking, siguiendo
> el patrón de Fakes descrito en [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md#5-koin-en-tests).

### 3.2 Capa de Dominio

**`ExecuteHGLauncherUseCase`**
(`HeroicGames/domain/usecase/ExecuteHGLauncherUseCase.kt`)

Caso de uso que aísla la lógica de negocio de la capa de datos. Recibe el adaptador por
inyección de constructor y garantiza que la operación bloqueante se ejecute en
`Dispatchers.IO`, liberando el hilo de la UI.

- Marcado `open` para permitir implementaciones Fake en los tests.
- El operador `invoke()` también es `open` por la misma razón.

### 3.3 Capa de Presentación

**`HomeViewModel`**
(`presesentation/viewModel/home/HomeViewModel.kt`)

El ViewModel recibe `ExecuteHGLauncherUseCase` como parámetro opcional (nullable) en su
constructor, siguiendo el patrón ya establecido para `ExecuteGameP2UseCase`. El `when`
exhaustivo sobre `Platforms.HEORIC_GAMES_LAUCHER` delega al use case.

---

## 4. Inyección de Dependencias (Koin)

Los nuevos componentes se registran en los módulos Koin existentes. Consultar la guía
completa en [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md).

### 4.1 DataModule

```kotlin
// Adaptador de Heroic Games Launcher para detección y ejecución nativa del proceso
single { LauncherHeroicGamesAdapter() }
```

Se registra como `single` porque no tiene estado mutable y puede ser compartido entre
distintas partes del grafo de dependencias.

### 4.2 DomainModule

```kotlin
// UseCase de Heroic Games Launcher
factory { ExecuteHGLauncherUseCase(adapter = get()) }
```

Se registra como `factory` porque los casos de uso son stateless y cada consumidor
debe obtener su propia instancia limpia.

---

## 5. Pruebas Unitarias (TDD)

**`ExecuteHGLauncherUseCaseTest`**
(`test/.../HeroicGames/domain/usecase/ExecuteHGLauncherUseCaseTest.kt`)

Se usa un `FakeHeroicLauncherAdapter` que hereda de `LauncherHeroicGamesAdapter` y
sobreescribe `executeLauncher()` para controlar el resultado sin hacer llamadas reales
al sistema operativo.

| Test | Valida |
|---|---|
| `whenLaunchSucceeds_returnsTrue` | El UseCase retorna `true` cuando el adaptador tiene éxito. |
| `whenLaunchFails_returnsFalse` | El UseCase retorna `false` cuando el adaptador reporta fallo. |
| `whenInvoked_callsAdapterExecute` | El UseCase delega la llamada al adaptador. |

> El aislamiento de los tests unitarios frente al sistema operativo real es un requisito
> obligatorio del proyecto. Ver reglas TDD en [01_arquitectura.md](01_arquitectura.md).

---

## 6. Detalles por Sistema Operativo

### 6.1 Linux (Flatpak)

- **Verificación:** `flatpak info com.heroicgameslauncher.hgl`
  - Exit code `0` = instalado. Cualquier otro código = no instalado o error.
- **Ejecución:** `flatpak run com.heroicgameslauncher.hgl --fullscreen`
- **¿Por qué Flatpak?** Es la distribución estándar y más común de Heroic Games Launcher
  en plataformas portátiles de Linux (como Steam Deck), coherente con el enfoque de PCSX2.

### 6.2 Windows

- **Verificación:** `cmd /c if exist "%LocalAppData%\Programs\heroic\Heroic.exe" (echo 1) else (echo 0)`
  - Salida `"1"` = instalado.
- **Ejecución:** `System.getenv("LOCALAPPDATA") + "\Programs\heroic\Heroic.exe" --fullscreen`
- **¿Por qué esta ruta?** Es la ruta de instalación estándar del instalador oficial de
  Heroic Games Launcher para Windows.

---

## 7. Referencias Cruzadas

- Arquitectura general de capas: [01_arquitectura.md](01_arquitectura.md)
- Tecnologías y dependencias (Coroutines, ProcessBuilder): [02_tecnologias.md](02_tecnologias.md)
- Estructura modular del proyecto: [03_modulos.md](03_modulos.md)
- Patrón de ejecución de proceso nativo (referencia PCSX2): [04_pcsx2.md](04_pcsx2.md)
- Guía de inyección de dependencias Koin: [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md)
- Contexto general de la IA para coherencia entre sesiones: [05_contexto_ia.md](05_contexto_ia.md)
