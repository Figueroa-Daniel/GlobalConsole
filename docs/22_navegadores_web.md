# Integración de Navegadores Web (navigatorWeb)

Este documento describe la arquitectura, detección multiplataforma, ciclo de vida de procesos y manejo de navegación por mando para la integración de navegadores web en GlobalConsole.

---

## 1. Visión General

El módulo `navigatorWeb` permite al usuario utilizar los navegadores web instalados en su equipo (Google Chrome, Mozilla Firefox, Brave y Microsoft Edge) como accesos directos de primera clase integrados en la biblioteca de juegos de GlobalConsole.

Siguiendo el diseño unificado de la aplicación, cada navegador se gestiona como un elemento activable/desactivable en la configuración:
- **Habilitado:** El navegador pasa a formar parte de la biblioteca general (`Platforms.NAVWEB`), mostrando su logo oficial y permitiendo su lanzamiento inmediato con el gamepad.
- **Deshabilitado:** Se retira de la biblioteca sin desinstalarlo del sistema.

---

## 2. Arquitectura y Capas

La integración sigue rigurosamente Clean Architecture (ver [01_arquitectura.md](01_arquitectura.md)):

```
org.example.globalconsole.navigatorWeb
├── data
│   ├── database
│   │   └── NavigatorAccesAdapter.kt       # Detección nativa del SO y gestión de procesos
│   ├── dto
│   │   └── NavWebObjDto.kt                # DTO para transporte de datos
│   ├── mappers
│   │   └── Mappers.kt                     # Mapeo DTO <-> Entidad de Dominio
│   ├── repository
│   │   └── NavWebRepository.kt           # Interfaz de repositorio
│   └── repositoryImpl
│       └── NavWebRepositoryImpl.kt       # Implementación con persistencia y caché
├── domain
│   ├── entitys
│   │   └── NavWebObj.kt                   # Modelo de dominio para biblioteca
│   └── usecase
│       ├── GetInstalledNavsUseCase.kt     # Lista navegadores instalados en el SO
│       ├── GetNavigatorsWebUseCase.kt     # Lista navegadores habilitados
│       ├── AddNavUseCase.kt              # Habilita un navegador en la biblioteca
│       ├── RemoveNavUseCase.kt           # Deshabilita un navegador
│       ├── IsEnabledNavUseCase.kt         # Consulta si un navegador está habilitado
│       ├── ExecuteNavUseCase.kt           # Lanza el navegador y monitoriza su proceso
│       └── CloseNavUseCase.kt             # Cierra el proceso del navegador
└── presesentation
    ├── view
    │   └── components
    │       └── NavigatorsSettingsDialog.kt # Diálogo de configuración con mando
    └── viewModel
        ├── settings/SettingsViewModel.kt  # Gestión del estado de navegadores
        └── home/HomeViewModel.kt          # Integración en la biblioteca y ciclo de ejecución
```

---

## 3. Detección Multiplataforma (`NavigatorAccesAdapter`)

La detección en el sistema operativo considera tanto paquetes en contenedores como binarios nativos del sistema:

### Linux
1. **Flatpak:** Se comprueba la presencia de IDs canónicos (`com.google.Chrome`, `org.mozilla.firefox`, `com.brave.Browser`, `com.microsoft.Edge`) mediante `flatpak list --app`.
2. **Binarios Nativos (Fallback):** En distribuciones como Zorin OS o Ubuntu donde los navegadores están instalados mediante paquetes DEB o en `/usr/bin/`, se comprueban binarios mediante `which` / `command -v`:
   - Google Chrome: `google-chrome`, `google-chrome-stable`
   - Mozilla Firefox: `firefox`
   - Brave: `brave-browser`, `brave`
   - Microsoft Edge: `microsoft-edge`, `microsoft-edge-stable`

### Windows
Se comprueban las rutas típicas en `Program Files` y `Program Files (x86)` o la presencia de los ejecutables directos (`chrome.exe`, `firefox.exe`, `brave.exe`, `msedge.exe`).

---

## 4. Ciclo de Vida y Ejecución de Procesos

1. **Lanzamiento:** Se invoca el navegador mediante `ProcessBuilder`, capturando la instancia de `Process`.
2. **Suspensión de la UI:** La aplicación pasa al estado `isSuspended = true` en `GamepadManager`, pausando la navegación por menús de GlobalConsole (ver [12_manejo_gamepad_y_foco.md](12_manejo_gamepad_y_foco.md)).
3. **Cierre de Procesos y Fallback:**
   - Debido a que los navegadores modernos lanzan procesos secundarios (subprocesos sandboxed, procesos hijos de Flatpak o bifurcaciones), un simple `process.destroy()` puede no cerrar la ventana visible.
   - `NavigatorAccesAdapter.closeNavigator()` implementa un cierre escalonado: primero intenta `process.destroy()`, y si sigue activo recurre a `killall -9` (Linux) o `taskkill /IM ... /F` (Windows) para el ejecutable correspondiente.
4. **Reanudación del Control:** Al finalizar el proceso (o cerrarse con el combo del mando), GlobalConsole limpia el proceso activo y restablece el foco completo del gamepad.

---

## 5. Control con Gamepad, Ratón y Teclado Flotante (OSK)

Durante la ejecución de un navegador, el gamepad pasa a modo suspendido con emulación de ratón:
- **Movimiento del ratón:** Stick analógico derecho con aceleración cuadrática.
- **Clic izquierdo:** Botón A (o Cruz).
- **Combo de Salida:** Pulsar simultáneamente `BACK` (Select) + `START` emite `GamepadEvent.Button.HOME` para forzar el cierre escalonado y regresar a la biblioteca.
- **Teclado en Pantalla Flotante (OSK Overlay):**
  - **Apertura automática:** Al hacer clic con **A** sobre un campo de texto en el navegador, el sistema detecta el cursor `xterm`/`ibeam` mediante `SystemCursorDetector` (usando `XFixesGetCursorImage` en X11) o mediante una doble pulsación rápida con **A**, abriendo el teclado flotante tras enfocar el campo.
  - **Atajos manuales:** Puede abrirse y cerrarse en cualquier momento pulsando **`R3`** (clic hacia adentro del stick analógico derecho) o mediante el combo **`START + R2`** / **`START + R1`**.
  - **Interacción:** El usuario navega con la cruceta o stick izquierdo, selecciona letras con A, borra con X, alterna mayúsculas con Y, o cancela con B o `R3`.
  - **Inyección de texto:** Al pulsar Intro (↵) o START, el texto se inyecta de forma instantánea en el campo enfocado del navegador mediante el portapapeles (`Ctrl + V`) y simulación de la tecla Intro vía AWT Robot.

---

## 6. Persistencia Unificada (`AppConfig`)

Las preferencias se persisten en `config.json` dentro de la clave `enabledNavigators` utilizando el modelo `AppConfig` (ver [08_persistencia_configuracion.md](08_persistencia_configuracion.md)):
- `NavWebRepositoryImpl` inicializa de forma perezosa la caché a partir de `config.json`, verificando que los navegadores sigan instalados en el SO actual.
- Al habilitar o deshabilitar un navegador, se actualiza la caché y se reescribe `config.json` preservando de manera atómica todos los demás campos de la configuración.

---

## 7. Inyección de Dependencias (Koin)

Todas las dependencias están registradas en los módulos de Koin (ver [07_inyeccion_dependencias_koin.md](07_inyeccion_dependencias_koin.md)):
- `dataModule`: `NavigatorAccesAdapter`, `NavWebRepository`.
- `domainModule`: `GetInstalledNavsUseCase`, `GetNavigatorsWebUseCase`, `AddNavUseCase`, `RemoveNavUseCase`, `IsEnabledNavUseCase`, `ExecuteNavUseCase`, `CloseNavUseCase`.
- `presentationModule`: Inyectados en `SettingsViewModel` y `HomeViewModel`.
