# 07. Inyección de Dependencias con Koin (KMP)

Este documento es la guía oficial sobre la inyección de dependencias con Koin adaptada a nuestro proyecto Kotlin Multiplatform (GlobalConsole).

---

## 1. ¿Qué es Koin y por qué lo usamos?

**Koin** es un framework de inyección de dependencias (DI) ligero y 100% Kotlin. Trabaja en tiempo de ejecución usando un DSL puro.

### Ventajas en GlobalConsole (KMP):
- **DSL puro en Kotlin**: Sin procesadores de anotaciones (KAPT/KSP), manteniendo la compilación muy rápida.
- **Soporte Multiplatform**: Funciona idénticamente en JVM/Escritorio y cualquier otro target que queramos añadir.
- **Desacoplamiento**: Evitamos inicializar clases gigantescas en el archivo `main.kt`.

---

## 2. Organización de Módulos

El árbol de dependencias se divide lógicamente en tres archivos dentro de `desktopApp/src/main/kotlin/org/example/globalconsole/di/`:

1. **`DataModule.kt`**: Provee los adaptadores de acceso a archivos (`GameP2FileSystemAdapter`), adaptadores de ejecución (`GamePCSX2Adapter`) y repositorios (`GameP2RepositoryImpl`). Los repositorios se exponen mediante su interfaz (ej. `GameP2Repository`). Se registran como `single { ... }` para mantener una única instancia.
2. **`DomainModule.kt`**: Provee todos los Casos de Uso del dominio (`GetGamesP2UseCase`, etc.). Se registran normalmente como `factory { ... }` ya que carecen de estado interno.
3. **`PresentationModule.kt`**: Provee los ViewModels (ej. `HomeViewModel`). Dependiendo de la librería Koin utilizada en KMP, se registran como `factory { ... }` o `viewModelOf(::MiVM)`.

---

## 3. Inicialización (Desktop KMP)

Koin se inicia una sola vez al arrancar la aplicación en `main.kt`:

```kotlin
fun main() = application {
    startKoin {
        modules(
            dataModule,
            domainModule,
            presentationModule
        )
    }

    // Inicialización de la UI de Compose
    Window(...) {
        // ...
    }
}
```

---

## 4. Consumo de Dependencias

Al no estar en Android, las formas de inyectar dependencias varían ligeramente, especialmente en Compose Desktop:

### 4.1 En la raíz de Compose (`main.kt`)
Se puede utilizar la API directa de Koin o la función `koinInject()` proporcionada por la librería de integración de Compose (`koin-compose`):

```kotlin
val viewModel: HomeViewModel = koinInject()
```

### 4.2 Dentro del módulo (DSL `get()`)
Al definir dependencias en los módulos, usamos `get()` para que Koin resuelva los parámetros:

```kotlin
val domainModule = module {
    factory { GetGamesP2UseCase(get()) }
}
```

---

## 5. Koin en Tests

En los tests unitarios puros (ej. `HomeViewModelTest`), **NO** utilizamos Koin. Se instancian las clases manualmente pasando `Fakes` o `Mocks` por constructor. Esto garantiza que las pruebas unitarias sean rápidas y no dependan de la configuración del framework DI.
