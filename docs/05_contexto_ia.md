# 05. Contexto Continuo para la IA

Este documento actúa como memoria continua y estado del proyecto para las sesiones de trabajo de la IA en GlobalConsole.

---

## 📍 1. Estado Actual

### Implementado
- **Estructura del Proyecto:** Módulo multiplataforma `shared` y ejecutable JVM de escritorio `desktopApp`.
- **Lanzador PCSX2 (Capa de Datos):**
  - `GameP2FileSystemAdapter`: Escaneo local de ISOs de PS2 en base a un directorio raíz y eliminación física de archivos en Windows y Linux.
  - `GamePCSX2Adapter`: Lanzamiento del comando nativo a pantalla completa y sin interfaz gráfica (`-fullscreen` y `-nogui`) en Windows y Linux (mediante Flatpak).
  - `GameP2Repository` e implementación `GameP2RepositoryImpl`: Repositorio con sistema de caché en memoria y mapeo a entidades de dominio.
- **Entidades de Dominio:** `Game`, `Platforms` (enum) y `GameP2`.
- **Estructura de Reglas de Agente:** Configuración unificada bajo la carpeta `.agents/AGENTS.md`.

---

## 📋 2. Próximos Pasos (Pendientes)

1. **Inyección de Dependencias (Koin):**
   - Configurar Koin Multiplatform en `shared` y `desktopApp`.
   - Registrar `GameP2FileSystemAdapter`, `GamePCSX2Adapter` y `GameP2RepositoryImpl`.
2. **Soporte de Gamepad Nativo (Jamepad):**
   - Añadir la librería Jamepad.
   - Desarrollar un lector de gamepad que emita eventos o flujos asíncronos (`Flow`) de los botones pulsados.
3. **Navegación y Foco de UI:**
   - Crear una interfaz gráfica Compose de pantalla completa tipo "10-foot UI".
   - Implementar control de foco nativo de Compose para manejar la rejilla de juegos únicamente con botones del mando (D-Pad, sticks, A/B).
4. **Desarrollo de Casos de Uso (UseCases) y TDD:**
   - Crear `GetGamesUseCase` y `ExecuteGameUseCase` bajo la arquitectura Clean Architecture.
   - Escribir tests unitarios con `kotlin.test` antes de codificar la lógica de producción.

---

## 🔗 3. Referencias Cruzadas
- Arquitectura Clean: [01_arquitectura.md](01_arquitectura.md).
- Tecnologías: [02_tecnologias.md](02_tecnologias.md).
- Módulos del proyecto: [03_modulos.md](03_modulos.md).
