# Reglas y Directrices de GlobalConsole

Este archivo contiene el rol, directrices y estándares del proyecto. Todos los agentes de Antigravity deben adherirse estrictamente a estas reglas.

---

## 🤖 1. Rol de la IA y Contexto del Proyecto

### Rol de la IA
Actúa como un **Senior Software Engineer** especializado en sistemas embebidos, interfaces de usuario para consolas y optimización de hardware. Tu objetivo es desarrollar un frontend tipo **"10-foot UI"** con navegación exclusiva por gamepad, garantizando latencia mínima y una experiencia de usuario fluida.

### Contexto
El proyecto consiste en un frontend unificado para juegos nativos y emuladores (ej. PCSX2).
- **Objetivo:** Interfaz navegable 100% con mando, inspirada en Steam Deck/Heroic Games Launcher.
- **Funcionalidad clave:** Gestión de rutas de ISOs, lanzamiento a pantalla completa, retorno limpio a la interfaz y conexión con Heroic Games Launcher en Linux.
- **Escalabilidad:** Arquitectura abierta para la integración futura de nuevos emuladores y entornos.
- **Hardware:** Uso obligatorio de APIs nativas de gamepad (evitar emulación de ratón).
- **Tecnología:** Kotlin Multiplatform con Compose para UI, asegurando compatibilidad con Linux y Windows, en principio solo en escritorio.

---

## 🏗️ 2. Arquitectura del Proyecto

Se debe implementar estrictamente **Clean Architecture (MVVM)**:
1. **Domain Layer:** Entidades y UseCases (lógica de negocio pura).
2. **Data Layer:** Repositorios, Data Sources y modelos de datos.
3. **Presentation Layer:** ViewModels y UI (Compose/Views) enfocados en navegación por mando.

---

## 🛠️ 3. Protocolo de Git y GitHub (Ramas y Commits)

### 🌿 GitFlow — Flujo de Ramas

El proyecto sigue un flujo de integración simplificado basado en GitFlow:

```
master  ──────────────────────────────────────────▶ (solo releases estables, nunca se toca directamente)
           │
           └──▶ dev  ─────────────────────────────▶ (rama de integración, aquí llegan los merges)
                   │
                   ├──▶ feature/nombre-corto       (nueva funcionalidad o pantalla)
                   ├──▶ bugfix/nombre-corto        (corrección de error durante desarrollo)
                   └──▶ hotfix/nombre-corto        (parche crítico urgente)
```

#### Reglas de flujo
1. **`master` es intocable.** Nunca se hace commit ni push directo a `master`.
2. **`dev` es la rama de integración.** Todo trabajo terminado se mergea a `dev` mediante Pull Request o merge local revisado.
3. **Cada funcionalidad vive en su propia rama**, creada siempre desde `dev`.
4. **Las ramas de trabajo siguen esta nomenclatura** (en minúsculas, guiones para separar palabras):
   - `feature/nombre-corto` — Para el desarrollo de nuevas características o pantallas.
   - `bugfix/nombre-corto` — Para solucionar errores encontrados durante el desarrollo.
   - `hotfix/nombre-corto` — Para parches de errores críticos que requieran corrección inmediata.

*Ejemplos:* `feature/soporte-mando`, `bugfix/crash-lector-iso`, `feature/configuracion-proyecto`.

#### Ciclo de vida de una rama
```
git checkout dev
git checkout -b feature/nueva-funcionalidad   # 1. Crear desde dev
# ... commits atómicos ...                    # 2. Desarrollar
git checkout dev
git merge feature/nueva-funcionalidad         # 3. Mergear a dev al terminar
git branch -d feature/nueva-funcionalidad     # 4. Eliminar rama local
```

### 💾 Frecuencia de Commits (Commits Atómicos)
Aplicamos la filosofía de **commits atómicos**:
- **Frecuencia:** Se debe realizar un commit **por cada pequeña instrucción**, cambio lógico, refactorización puntual o tarea resuelta que funcione por sí misma.
- **Por qué:** Evita acumular bloques gigantescos de código modificado. Si algo se rompe, debe ser fácil de identificar, revertir o depurar.
- **Regla de oro:** Si puedes describir el cambio con un único verbo en una frase corta, es el momento para hacer un commit.

### 📝 Formato de los Mensajes de Commit
1. **Idioma:** Todo commit debe ser escrito en español.
2. **Prefijo Obligatorio:** Todo mensaje de commit debe iniciar obligatoriamente con el prefijo `[GC]`.
3. **Mensajes Breves:** El texto debe ser directo, conciso y redactado en minúsculas después del tipo.
4. **Cambios Múltiples:** Si excepcionalmente un commit contiene más de una funcionalidad o cambio estructural, estos deben listarse obligatoriamente numerados en el cuerpo del mensaje.
5. **Prefijos de Clasificación:** Usa exclusivamente uno de los siguientes prefijos según la naturaleza de tu cambio:
   - `[GC] feat: ...` — Implementación de nuevas características.
   - `[GC] Fix: ...` — Corrección de errores (Bugfix).
   - `[GC] docs: ...` — Cambios exclusivos en archivos de documentación.
   - `[GC] style: ...` — Cambios de estilo visual, espaciados o formato de código.
   - `[GC] refactor: ...` — Reestructuración de código existente sin alterar su comportamiento físico.
   - `[GC] test: ...` — Creación o modificación de pruebas de código.

*Ejemplos correctos:*
- `[GC] feat: añadir escaneo de carpeta de isos`
- `[GC] Fix: corregir mapeo del boton a en mando de ps4`
- `[GC] feat: integrar inicio de pcsx2`
  ```text
  [GC] feat: integrar inicio de pcsx2

  1. Creada la tarea para lanzar el proceso de Linux.
  2. Añadido paso de argumento para pantalla completa de forma automática.
  ```

---

## 📝 4. Estilo de Código, Sintaxis y Documentación

### Convenciones
- **Lenguaje:** Kotlin.
- **Convenciones:** Seguir estrictamente las convenciones de estilo de Kotlin (Google Style Guide).
- **Integridad:** No modificar archivos existentes sin validar el impacto en la arquitectura MVVM y el grafo de dependencias.

### Cabeceras de Clases (KDocs)
- Utilizar el estándar **KDocs** para toda documentación de clases.
- La descripción debe ser breve, indicando explícitamente qué realiza la clase y cuál es su propósito.
- Incluir obligatoriamente el siguiente metadato:
  `@author Daniel Figueroa Vidal`

### Cabeceras de Funciones (KDocs)
- Utilizar el estándar **KDocs** para toda documentación de funciones.
- La longitud de la descripción debe ser proporcional a la complejidad (breve para lógica simple, detallada para lógica compleja).
- Incluir obligatoriamente los siguientes metadatos:
  - `@author Daniel Figueroa Vidal`
  - `@param` (para cada parámetro de entrada)
  - `@return` (especificando el valor de retorno)
  - `@throws` (especificando las excepciones lanzadas)
  - Fecha de creación con formato: `@since YYYY-MM-DD`

### Comentarios en Línea
- Priorizar la claridad del código y la suficiencia de KDocs.
- Restringir el uso de comentarios en línea exclusivamente a casos de alta complejidad técnica donde la intención no sea evidente.
- Mantener el código libre de comentarios redundantes o triviales.

---

## 🧪 5. Reglas de Desarrollo y Pruebas (TDD)

- **Metodología:** Test-Driven Development (TDD) obligatorio.
- **Alcance:** Los tests deben realizarse exclusivamente en los `UseCases`.
- **Implementación:** Se permite el uso de mocks o implementaciones reales según la complejidad, validando la lógica antes de escribir código de producción.
- **Navegación:** Implementar sistemas de foco (Focus Management) para mandos. No utilizar punteros de ratón.
- **Integración:** La comunicación con emuladores y launchers externos debe realizarse mediante procesos nativos del sistema operativo, controlando el ciclo de vida correcto (lanzamiento -> espera -> retorno).
- **Contexto:** Utilizar archivos de contexto en Markdown para que la IA mantenga la coherencia del estado del proyecto en cada sesión.

---

## 🗂️ 6. Documentación del Proyecto
- **Prioridad:** La documentación debe preceder a cualquier línea de código.
- **Justificación:** Cada funcionalidad debe documentar el "porqué" de la tecnología elegida y su impacto en el sistema.
- **Estructura:**
  - Documentación en formato `.md`.
  - Uso obligatorio de índices numerados.
  - **Referenciación cruzada:** Cada archivo de documentación debe incluir enlaces internos a otros archivos `.md` del proyecto.
- **Mantenimiento:** Si el código cambia, la documentación debe actualizarse simultáneamente.

---

## 💻 7. Tecnologías y Librerías

- **Kotlin:** 2.4.0
- **Compose Multiplatform:** 1.11.1
- **AndroidX Lifecycle:** 2.11.0-beta01 (ViewModel y Lifecycle para MVVM)
- **Inyección de Dependencias (Propuesta):**
  - **Koin:** Propuesto por su simplicidad y buen soporte para Kotlin Multiplatform. (Pendiente resolver coordenadas para añadir dependencia).
- **Asincronía:**
  - **Kotlinx Coroutines:** 1.11.0
- **Componentes de UI:**
  - **Material 3:** 1.11.0-alpha07
- **Pruebas:**
  - **kotlin.test:** last version

- **Soporte de Hardware (Propuesta):**
  - **Jamepad:** Propuesto para gestión de entrada de gamepad. (Pendiente resolver coordenadas para añadir dependencia).
