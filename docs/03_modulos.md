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
- **Responsabilidad:** Módulo ejecutable de escritorio que genera la aplicación de escritorio nativa (Windows, Linux, macOS) utilizando Compose Desktop.
- **Estructura de Carpetas:**
  - `src/main/kotlin/org/example/globalconsole/`:
    - `main.kt`: Punto de entrada de la aplicación de escritorio (`fun main()`).
    - `generalDomain/`: Entidades de dominio comunes del lanzador (como `Game` y `Platforms`).
    - `juegosPcsx2/`: Módulo correspondiente al emulador PCSX2.
      - `domain/`: Entidades del emulador (como `GameP2`).
      - `data/`: DTOs, mappers, repositorios y adaptadores para la base de datos de archivos e invocación del emulador.
    - `settings/`: Configuración y rutas relativas al sistema operativo (como `SettingsPlatforms.kt`).

---

## 🔗 2. Referencias Cruzadas
- Consultar los detalles de Clean Architecture en [01_arquitectura.md](01_arquitectura.md).
- Ver la tecnología empleada en los módulos en [02_tecnologias.md](02_tecnologias.md).
