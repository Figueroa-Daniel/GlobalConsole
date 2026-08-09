# 02. Tecnologías de GlobalConsole

Este documento describe el stack tecnológico utilizado en GlobalConsole y justifica técnicamente la adopción de cada dependencia y framework.

---

## 💻 1. Stack Tecnológico

### Kotlin (versión 2.4.0)
- **Por qué:** Como lenguaje principal del desarrollo multiplataforma. Ofrece tipado estático seguro, concisión y interoperabilidad directa con múltiples sistemas operativos (JVM, Native, Android/iOS en un futuro si se requiere).
- **Impacto:** Permite compartir el 100% de la lógica de dominio y datos en el módulo `shared`.

### Compose Multiplatform (versión 1.11.1)
- **Por qué:** Permite crear interfaces de usuario de forma declarativa compartiendo código entre escritorio (Windows, Linux, macOS) y móviles. Es fundamental para lograr una UI adaptada a pantallas grandes ("10-foot UI") manteniendo una renderización de alto rendimiento.
- **Impacto:** El diseño visual y los estados de UI se programan una sola vez.

### AndroidX Lifecycle (versión 2.11.0-beta01)
- **Por qué:** Proporciona los componentes estándar de arquitectura `ViewModel` y el control del estado del ciclo de vida en entornos Compose de forma nativa e idiomática.
- **Impacto:** Garantiza un flujo unidireccional de datos (UDF) estructurado bajo el patrón MVVM.

### Kotlinx Coroutines (versión 1.11.0)
- **Por qué:** Manejo de concurrencia y asincronía no bloqueante. Indispensable para realizar E/S en archivos (como buscar isos) y para invocar llamadas de comandos de terminal de forma asíncrona sin congelar el hilo principal de la interfaz de usuario.
- **Impacto:** Evita caídas de frames en el renderizado de la UI.

### Material 3 (versión 1.11.0-alpha07)
- **Por qué:** Framework de diseño estándar de Google. Proporciona componentes visuales consistentes, con soporte nativo de accesibilidad, temas oscuros y animaciones micro-interactivas.

### kotlin.test
- **Por qué:** Librería de pruebas unificada y estándar de JetBrains para Kotlin Multiplatform. Permite escribir aserciones y tests en el código común (`commonTest`) que se compilan y ejecutan de manera idéntica en JVM/escritorio y otros destinos, sin atarse a APIs dependientes del sistema operativo como JUnit plano en código común.

---

## 🛠️ 2. Propuestas Pendientes

### Koin
- **Por qué:** Para resolver la inyección de dependencias (DI) de forma ligera e idiomática en Kotlin Multiplatform.
- **Estado:** Pendiente de resolver coordenadas en Gradle para añadir la dependencia correcta al build.

### Jamepad
- **Por qué:** Para la lectura de entrada de gamepad de forma nativa y evitar la emulación de eventos de ratón. Clave para la interfaz 10-foot UI.
- **Estado:** Pendiente de resolver coordenadas de Gradle.

---

## 🔗 3. Referencias Cruzadas
- Conocer la distribución de capas en [01_arquitectura.md](01_arquitectura.md).
- Entender el consumo de estos frameworks en los módulos en [03_modulos.md](03_modulos.md).
