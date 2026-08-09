# 03. Módulos de GlobalConsole

Este documento detalla la estructura modular de GlobalConsole y la responsabilidad de cada componente.

---

## 📦 1. Estructura Modular

El proyecto de GlobalConsole está dividido en dos módulos de Gradle principales:

```
[GlobalConsole Root]
  ├── shared/ (Módulo Multiplataforma principal)
  └── desktopApp/ (Módulo ejecutable específico para JVM/Escritorio)
```

### 1. Módulo `shared/`
- **Responsabilidad:** Contiene el código multiplataforma (Kotlin Multiplatform) que puede compilarse y reutilizarse en múltiples targets.
- **Estructura de Carpetas:**
  - `src/commonMain/kotlin/org/example/globalconsole/`: Código común para todas las plataformas.
    - `App.kt`: Pantalla o punto de partida visual con Compose.
    - `Greeting.kt`: Clase de ejemplo de negocio multiplataforma.
    - `GreetingUtil.kt`: Funciones utilitarias comunes.
    - `Platform.kt`: Definición del contrato `expect` para detalles dependientes del sistema operativo.
  - `src/commonTest/kotlin/`: Pruebas de lógica común con `kotlin.test`.
  - `src/jvmMain/kotlin/`: Implementación de código nativo específico de JVM (`actual`).
  - `src/jvmTest/kotlin/`: Pruebas unitarias de JVM.

### 2. Módulo `desktopApp/`
- **Responsabilidad:** Módulo ejecutable de escritorio que genera la aplicación nativa (Windows, Linux, macOS) utilizando Compose Desktop.
- **Estructura de Carpetas:**
  - `src/main/kotlin/org/example/globalconsole/`
    - `main.kt`: Punto de entrada de la aplicación. Realiza la inyección manual de dependencias y lanza el `AppNavHost`.
    - `generalDomain/`: Entidades de dominio comunes del lanzador (como `Game` y `Platforms`).
    - `juegosPcsx2/`: Módulo correspondiente al emulador PCSX2.
      - `domain/`: Entidades del emulador (como `GameP2`).
      - `data/`: DTOs, mappers, repositorios y adaptadores para la base de datos de archivos e invocación del emulador.
    - `settings/`: Configuración y rutas relativas al sistema operativo (`SettingsPlatforms.kt`).
    - `presesentation/`: Capa de presentación — UI, ViewModels y navegación.
      - `navigation/`: Grafo de navegación de la aplicación.
        - `AppRoutes.kt`: Define los destinos de navegación como objetos `@Serializable` (type-safe).
        - `AppNavHost.kt`: Composable `NavHost` que conecta rutas con pantallas.
      - `input/`: Gestión de periféricos nativos (gamepads).
        - `GamepadEvent.kt`: Eventos unificados de botones y direcciones.
        - `GamepadManager.kt`: Motor de lectura activa (polling) usando LWJGL 3 (GLFW). Incluye control de ratón nativo.
      - `viewModel/`: Carpeta raíz de ViewModels, organizada por pantalla.
        - `home/`: ViewModel y estado UI de la pantalla principal.
          - `HomeViewModel.kt`: ViewModel de la pantalla de biblioteca de juegos.
          - `HomeUiState.kt`: Estado de UI (`Loading`, `Success`, `Empty`, `Error`).
      - `view/`: Vistas y componentes UI de Compose.
        - `screen/`: Pantallas completas de la aplicación.
          - `HomeScreen.kt`: Pantalla principal de la biblioteca de juegos de PS2.
        - `components/`: Componentes reutilizables de UI.
          - `MetroTopBar.kt`: Barra superior estilo Metro Noir.
          - `GameTile.kt`: Tarjeta de juego individual para la cuadrícula.
          - `SetupPathDialog.kt`: Diálogo de configuración de ruta de ISOs.

---

## 🧪 2. Estructura de Tests

Los tests siguen la misma estructura de paquetes que el código de producción para mantener la coherencia:

```
src/test/kotlin/org/example/globalconsole/
  └── presesentation/
      └── viewModel/
          └── home/
              ├── HomeViewModelTest.kt
              └── fakes/
                  ├── FakeGetGamesP2UseCase.kt
                  └── FakeGameP2Repository.kt
```

> **Norma:** Los tests automáticos se limitan a `UseCases` y `ViewModels` (que los orquestan) e integraciones de input nativo aisladas. Los Fakes sustituyen la capa de datos para aislar la lógica de negocio.

---

## 🔗 3. Referencias Cruzadas
- Consultar los detalles de Clean Architecture en [01_arquitectura.md](01_arquitectura.md).
- Ver la tecnología empleada en los módulos en [02_tecnologias.md](02_tecnologias.md).
- Entender la ejecución del emulador en [04_pcsx2.md](04_pcsx2.md).

