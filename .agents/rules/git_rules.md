# 🛠️ Protocolo de Git y GitHub: Ramas y Commits

Este documento define el estándar obligatorio para la gestión del repositorio, el flujo de trabajo en las ramas y la frecuencia de guardado de cambios.

---

## 🌿 1. Manejo de Ramas (Branching)

Trabajamos con ramas de funcionalidad aisladas. Queda estrictamente prohibido subir cambios directamente a la rama principal (`main` o `master`).

### Formatos Obligatorios

Toda nueva rama debe seguir rigurosamente una de estas tres nomenclaturas (en minúsculas y usando guiones para separar palabras):

* `feature/nombre-corto` — Para el desarrollo de nuevas características o pantallas.
* `bugfix/nombre-corto` — Para solucionar errores encontrados durante el desarrollo.
* `hotfix/nombre-corto` — Para parches de errores críticos que requieran corrección inmediata.

*Ejemplo de uso:* `feature/soporte-mando` o `bugfix/crash-lector-iso`.

---

## 💾 2. Frecuencia de Commits (Commits Atómicos)

Aplicamos la filosofía de **commits atómicos**:

* **Frecuencia:** Se debe realizar un commit **por cada pequeña instrucción**, cambio lógico, refactorización puntual o tarea resuelta que funcione por sí misma.
* **Por qué:** Evita acumular bloques gigantescos de código modificado. Si algo se rompe, debe ser fácil de identificar, revertir o depurar commit a commit.
* **Regla de oro:** Si puedes describir el cambio con un único verbo en una frase corta, es el momento perfecto para hacer un commit.

---

## 📝 3. Formato de los Mensajes de Commit

### Reglas de Estructura

1. **Prefijo Obligatorio:** Todo mensaje de commit debe iniciar con `[GC]`.
2. **Mensajes Breves:** El texto debe ser directo, conciso y redactado en minúsculas después del tipo.
3. **Cambios Múltiples:** Si excepcionalmente un commit contiene más de una funcionalidad o cambio estructural, estos deben listarse obligatoriamente numerados en el cuerpo del mensaje.

### Prefijos de Clasificación

Usa exclusivamente uno de los siguientes prefijos según la naturaleza de tu cambio:

* `[GC] Fix: ...` — Corrección de errores (Bugfix).
* `[GC] feat: ...` — Implementación de nuevas características.
* `[GC] docs: ...` — Cambios exclusivos en archivos de documentación.
* `[GC] style: ...` — Cambios de estilo visual, espaciados o formato del código.
* `[GC] refactor: ...` — Reestructuración de código existente sin alterar su comportamiento físico.
* `[GC] test: ...` — Creación o modificación de pruebas de código.

### Ejemplos Correctos:

* `[GC] feat: añadir escaneo de carpeta de isos`
* `[GC] Fix: corregir mapeo del boton a en mando de ps4`
* `[GC] feat: integrar inicio de pcsx2`
```text
[GC] feat: integrar inicio de pcsx2

1. Creada la tarea para lanzar el proceso de Linux.
2. Añadido paso de argumento para pantalla completa de forma automática.
```
