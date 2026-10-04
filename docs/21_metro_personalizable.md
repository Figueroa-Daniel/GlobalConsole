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
Los campos JSON son `accentColor` (CYAN, BLUE, VIOLET, PINK, AMBER, GREEN, RED,
CRIMSON, NAVY, COBALT, TEAL, ORANGE) y
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
La tarea `desktopApp:test` utiliza `build/test-work/test` como directorio de trabajo:
además, la prueba heredada de configuración inyecta un archivo temporal absoluto
en ambos repositorios. Su limpieza nunca apunta al archivo personal.
Revisar todas las rutas de entrada de mando y los tamaños de ventana. Registrar
por separado las comprobaciones automatizadas y las que requieran mando físico.

## 5. Revisión gráfica y regresiones

Se ejecutó la interfaz real con Compose en Xvfb, sin arrancar GLFW ni emuladores,
con biblioteca ficticia y configuración temporal. Se enviaron eventos al mismo
flujo que utiliza el mando y se inspeccionaron capturas a 1280×800 y 800×600:

| Recorrido | Resultado |
| --- | --- |
| Biblioteca → Vista → color/intensidad → cerrar | Tema actualizado y JSON persistido |
| Vista en ventana pequeña | Desplazamiento automático hasta la fila activa |
| Buscar → Y → A → X → START | Mayúsculas, escritura, borrado y confirmación correctos |
| Búsqueda sin resultados → volver a Buscar | Barra accesible sin elementos en la cuadrícula |
| Ajustes → carpeta → X → volver | Modal anidado aislado y cancelación directa |
| Biblioteca → recortador → B | Previsualización y botones visibles en 800×600 |
| Estados vacío y error | Acción principal y barra accesibles por mando |

La revisión detectó y corrigió doble foco causado por el foco nativo de Compose
y hover de un cursor inmóvil: los botones con selección lógica explícita dan
prioridad a dicha selección en modo mando; el hover pertenece al modo ratón.
Se corrigió también la navegación izquierda de Vista y el Intro del OSK.

Resultado final: 89 pruebas superadas (87 de escritorio y 2 compartidas), sin
fallos ni omisiones.

Validación reproducible: `./gradlew :desktopApp:test :shared:jvmTest --offline`.
Las nuevas pruebas automáticas se limitan a casos de uso. La prueba de integración
heredada conserva su cobertura con archivo temporal absoluto inyectado.
Pendiente fuera de este entorno: prueba física de sticks, gatillos y latencia en
el mando y televisor del usuario; no se ha lanzado ningún juego real.

## 6. Ampliación de paleta y proporciones

La segunda revisión añade rojo, carmesí, azul marino, cobalto, turquesa y naranja
sin renombrar los seis identificadores existentes. Los colores oscuros tienen
un tono original para rellenos/halos y un tono claro derivado para texto/bordes.
El texto sobre la selección cambia entre claro y oscuro según la luminancia;
esto permite usar azul marino real sin perder la legibilidad del foco.

La composición sigue una escala de espaciado de 4 px: márgenes exteriores de
24 dp en ventanas compactas y 32 dp en anchas, separación entre tarjetas de
20 dp y tarjeta mínima de 200 dp. La fórmula de columnas del mando utiliza las
mismas constantes que la cuadrícula. Los títulos de juegos suben a 16 sp, con
espacio interior de 20 dp, mientras las etiquetas de plataforma son secundarias.
La barra alinea buscador y acciones a una altura común de 48 dp. Los diálogos
mantienen márgenes exteriores e interiores consistentes; Vista tiene altura
máxima para evitar espacios vacíos excesivos en pantallas grandes y una muestra
visual del color seleccionado. No se cambian los recorridos del mando. El desplazamiento de la biblioteca solo
se activa si la tarjeta no está completamente visible; al enfocar el primer juego
se conserva el encabezado inicial para evitar saltos que rompan la composición.

Validación: prueba previa de los casos de uso para los nuevos identificadores,
persistencia de toda la paleta y revisión gráfica de rojo y azul marino en
1280×800 y 800×600, incluyendo teclado, ajustes y navegación agrupada.
Los seis nuevos rellenos dan un contraste texto/fondo entre 5,04:1 y 12,04:1;
los acentos derivados sobre el panel oscuro quedan entre 6,26:1 y 9,07:1.

## 7. Rediseño Metro neón

La tercera revisión cambia la composición, no solo el espaciado: cabecera de
biblioteca con jerarquía editorial, fondo negro con iluminación ambiental tenue,
tarjetas verticales con carátula y pie opaco separado, separadores de plataforma
con línea y paneles con cabecera luminosa. La geometría permanece rectangular.
El texto no se superpone a logos o imágenes: el pie reserva su propio espacio y
la imagen de sustitución se encuadra sobre una composición geométrica oscura.

Se añaden ELECTRIC_PURPLE (morado eléctrico), NEON_PURPLE (morado neón) y
ULTRAVIOLET (ultravioleta). El tono saturado se mantiene en rellenos, indicadores
y halos; el tono del texto se aclara solo lo necesario para conservar contraste.
El brillo ambiental y el halo responden a la intensidad guardada y se apagan con
valor cero. No hay animaciones continuas, efectos que desenfoquen texto ni nuevas
dependencias. La lógica sigue en los casos de uso existentes.

En ventanas grandes, la cabecera ofrece una introducción y recuento de elementos;
en 800×600 se reduce para dar prioridad a los juegos. La navegación conserva las
columnas calculadas a partir del ancho real y los mismos eventos aislados por
modal. Las tarjetas reservan altura estable para dos líneas del nombre.
La validación incluye TDD de persistencia de los tres morados y revisión de la
interfaz real con imágenes presentes/ausentes y distintas resoluciones.

## 8. Referencias

- [Arquitectura](01_arquitectura.md)
- [Persistencia](08_persistencia_configuracion.md)
- [Mando y foco](12_manejo_gamepad_y_foco.md)
- [Teclado](15_busqueda_y_osk.md)
- [Vista](20_persistencia_vista.md)
- [Contexto](19_contexto_ia.md)

@author Daniel Figueroa Vidal
@since 2026-10-04
