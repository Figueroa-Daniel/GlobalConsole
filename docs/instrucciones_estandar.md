# Commits
- Todo commit debe ser escrito en español
- Todo mensaje de commit debe iniciar obligatoriamente con el prefijo `[GC]`.
- Los mensajes deben ser breves y concisos.
- Si el commit incluye múltiples funcionalidades, estas deben ser numeradas.
- Utiliza exclusivamente los siguientes prefijos según la naturaleza del cambio:
    - `Fix`: Corrección de errores (Bugfix).
    - `feat`: Implementación de nuevas características.
    - `docs`: Cambios exclusivos en documentación.
    - `style`: Cambios de estilo (formato, espacios, etc.).
    - `refactor`: Refactorización de código existente.
    - `test`: Adición o modificación de pruebas unitarias.

# Cabeceras de Clases
- Utilizar el estándar **KDocs** para toda documentación de clases.
- La descripción debe ser breve, indicando explícitamente qué realiza la clase y cuál es su propósito.
- Incluir obligatoriamente el siguiente metadato:
    - `@author Daniel Figueroa Vidal`

# Cabeceras de Funciones
- Utilizar el estándar **KDocs** para toda documentación de funciones.
- La longitud de la descripción debe ser proporcional a la complejidad de la función (breve para lógica simple, detallada para lógica compleja).
- Incluir obligatoriamente los siguientes metadatos:
    - `@author Daniel Figueroa Vidal`
    - `@param` (para cada parámetro de entrada).
    - `@return` (especificando el valor de retorno).
    - `@throws` (especificando las excepciones lanzadas).
    - Fecha de creación (formato: `@since YYYY-MM-DD`).

# Comentarios en Línea
- Priorizar la claridad del código y la suficiencia de las cabeceras (KDocs) para explicar la lógica.
- Restringir el uso de comentarios en línea exclusivamente a casos de alta complejidad técnica donde la intención no sea evidente por sí misma.
- Mantener el código libre de comentarios redundantes o triviales.

# Nomenclatura de Ramas
- Las ramas deben seguir estrictamente uno de los siguientes formatos:
    - `feature/nombre`
    - `bugfix/nombre`
    - `hotfix/nombre`
