# 21. Metro oscuro personalizable

## 1. Objetivo y decisiones

Unificar biblioteca, estados vacíos y de error, ajustes, selector de carpetas,
recortador y teclado con superficies oscuras, tipografía legible y foco inequívoco.
El fondo permanece oscuro: el usuario elige el color de acento y la intensidad
del halo, sin alterar el contraste del texto. Se reutiliza Compose y el JSON
existente; no se añaden dependencias ni se modifica la ejecución de emuladores.

## 2. Arquitectura y persistencia

`AppearanceSettings` pertenece al dominio. Los casos de uso de lectura y guardado
normalizan valores antiguos/desconocidos y validan la intensidad (0–100).
`SettingsRepository` conserva agrupación, rutas y launchers al guardar apariencia.
`HomeViewModel` expone el estado y los errores de guardado; un tema Compose común
propaga los colores a toda la aplicación y los diálogos, con vista previa inmediata.
Los campos JSON son `accentColor` (CYAN, BLUE, VIOLET, PINK, AMBER, GREEN) y
`glowIntensity` (0–100, 45 por defecto). Un archivo anterior obtiene CYAN/45.

## 3. Interacción

Vista incluye agrupación, posición de launchers, color e intensidad. Arriba/abajo
elige la fila; izquierda/derecha cambia el valor y A avanza. B cierra. Cada fila
muestra su valor y el foco, y se desplaza a la vista al navegar con mando.
El halo es decorativo: incluso al desactivarlo permanece el borde de selección.
Se conservan los atajos del mando y el aislamiento de eventos de los modales.

El teclado añade números, ñ, acentos y signos. A e Intro confirman de la misma
forma que el ratón; X borra, Y cambia mayúsculas, START confirma y B cierra,
conservando la búsqueda reactiva. Los cambios de fila ajustan el índice de forma
sincrónica para impedir accesos fuera de rango. El foco mantiene su columna
preferida al pasar por la barra espaciadora.

## 4. Validación

TDD exclusivamente en casos de uso: valores por defecto, colores, límites,
rechazo de datos inválidos, persistencia tras recrear el repositorio y conservación
de los campos existentes. Ejecutar la suite existente y compilar el escritorio.
Revisar todas las rutas de entrada de mando y los tamaños de ventana. Registrar
por separado las comprobaciones automatizadas y las que requieran mando físico.

## 5. Referencias

- [Arquitectura](01_arquitectura.md)
- [Persistencia](08_persistencia_configuracion.md)
- [Mando y foco](12_manejo_gamepad_y_foco.md)
- [Teclado](15_busqueda_y_osk.md)
- [Vista](20_persistencia_vista.md)
- [Contexto](19_contexto_ia.md)

@author Daniel Figueroa Vidal
@since 2026-10-04
