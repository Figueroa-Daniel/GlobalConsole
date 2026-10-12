# 1. Acceso rápido y configuración

## 1.1. Objetivo

Y / triángulo permite alcanzar los controles sin recorrer la biblioteca o las listas de ajustes. Solo mueve el foco: A confirma la acción. En la biblioteca enfoca Buscar; X conserva el recorte de la carátula activa, respetando ratón/mando.

## 1.2. Diálogos

En ajustes generales Y enfoca Guardar y la cruceta permite pasar a Cerrar. En Vista, Navegadores, Juegos locales y Aplicaciones enfoca Cerrar. En el selector de carpetas enfoca Seleccionar; izquierda pasa a Cancelar y arriba vuelve a la lista. En el recortador enfoca Confirmar. Los diálogos hijos mantienen la exclusividad de eventos descrita en [manejo del foco](12_manejo_gamepad_y_foco.md). El teclado virtual conserva sus controles propios.

## 1.3. Presentación

Se mantiene Compose y la paleta [Metro](21_metro_personalizable.md). La configuración separa opciones y rutas con encabezados y desplazamiento independiente; compacta las opciones repetitivas y mantiene las acciones y su ayuda siempre visibles. No cambia la persistencia: las opciones se aplican al momento y las rutas mediante Guardar. La lógica de repositorios y UseCases permanece intacta ([arquitectura](01_arquitectura.md)).

## 1.4. Validación prevista

Al ser navegación y composición visual, verificar con una ventana de prueba y eventos de mando: salto desde listas largas, confirmación explícita, cambio de foco y modales. Ejecutar la suite de UseCases existente; no introducir pruebas unitarias de UI ni lógica artificial en dominio. Revisar configuración a resolución de escritorio y reducida. Registrar resultados en [contexto](19_contexto_ia.md).
