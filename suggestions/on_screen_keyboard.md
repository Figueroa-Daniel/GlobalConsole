# Propuesta de Arquitectura: Teclado en Pantalla (OSK) para Mando

Actualmente, no existen librerías de componentes en `Compose Multiplatform` o `Compose for Desktop` que ofrezcan un teclado en pantalla (On-Screen Keyboard) nativo y optimizado para ser navegado exclusivamente mediante un D-Pad (cruceta) de mando.

Para lograr una experiencia de consola fluida (estilo PlayStation o Xbox) al buscar juegos, se requiere desarrollar un componente personalizado. A continuación, se detallan las posibilidades y la arquitectura recomendada.

## Alternativa 1: OSK Nativo en Compose (Recomendada)
Desarrollar un componente `@Composable` llamado `GamepadOSK` que simule un teclado virtual.

### Arquitectura sugerida:
1. **Matriz de Teclas (Grid):**
   - Una lista bidimensional `List<List<Char>>` que represente la disposición (QWERTY o ABC).
   - Variables de estado para rastrear la fila (`focusedRow`) y columna (`focusedCol`) actual.

2. **Navegación por Gamepad:**
   - Interceptar `GamepadEvent` de la misma manera que en el `GamepadFolderPicker`.
   - Modificar `focusedRow` y `focusedCol` usando la cruceta, asegurándose de hacer *wrap-around* (si se pulsa izquierda en el límite izquierdo, saltar al límite derecho).

3. **Capas (Shift / Símbolos):**
   - Teclas especiales dedicadas para alternar entre Minúsculas, Mayúsculas y Símbolos. Esto cambiaría el mapa de caracteres a renderizar.

4. **Entrada de Texto:**
   - Un botón "A" (Confirm) añadiría el carácter seleccionado al `StateFlow` del texto de búsqueda.
   - Un botón "X" o "Cuadrado" del mando podría mapearse directamente al borrado (Backspace) para mayor agilidad.
   - El botón "Start" confirmaría la búsqueda y cerraría el teclado.

**Ventajas:** Integración perfecta con la interfaz Metro, control total sobre el foco, cero dependencias externas.
**Desventajas:** Requiere implementar y mantener la lógica de la cuadrícula y el *focus state* manualmente.

## Alternativa 2: Uso del Ratón Simulado + OSK del Sistema
Dado que ya implementamos el uso del stick derecho como ratón, se podría invocar el teclado virtual nativo del Sistema Operativo (On-Screen Keyboard de Windows o `onboard` en Linux) y permitir al usuario hacer "clic" en las teclas usando el mando.

**Ventajas:** Muy fácil de implementar (solo requiere un comando de consola).
**Desventajas:** Rompe completamente la inmersión de "interfaz de consola". La experiencia de mover el ratón con el stick para escribir letra a letra es muy inferior a saltar entre teclas con la cruceta.

## Conclusión
Para mantener la coherencia de GlobalConsole y adherirse a las reglas `.agents` de priorizar la experiencia nativa con mando, **se recomienda enfáticamente la Alternativa 1**. Puede ser encapsulado como un componente reutilizable en `presesentation/view/components/GamepadOSK.kt` e inyectado como un diálogo modal cuando el usuario pulse "Buscar".
