# 24. Aplicaciones locales

## 1. Objetivo y arquitectura

`localApps` incorpora aplicaciones instaladas como `Platforms.LOCALAPP`, con el
mismo flujo de selección, biblioteca y ejecución que [Juegos locales](23_juegos_locales.md).
El contrato del repositorio se define en Domain; Data detecta, filtra y ejecuta;
los ViewModels publican estado y Compose controla la presentación. Véase
[Arquitectura](01_arquitectura.md) e [Inyección Koin](07_inyeccion_dependencias_koin.md).

## 2. Detección y exclusiones

Linux usa los accesos `.desktop` de usuario, sistema, Flatpak y rutas XDG. Se
comparte con juegos el lector de Desktop Entry y la resolución de iconos,
conservando prioridad, Hidden/NoDisplay, argumentos y directorio de trabajo.
Se descartan categorías Game, sus subcategorías, Emulator y WebBrowser, accesos
que lanzan juegos y las identidades conocidas de emuladores, navegadores y
launchers de juegos. Se comprueba identidad de archivo, ejecutable, argumentos
Flatpak/Snap y protocolos. Dolphin gestor de archivos debe seguir disponible;
Dolphin Emulator debe excluirse. No se excluye una aplicación solo porque maneje
HTML o su descripción mencione un navegador.

Windows recorre accesos ejecutables del menú Inicio y App Paths del Registro.
Cruza categorías, ubicaciones y ejecutables de juegos registrados, GameUX y
clientes de navegador; excluye emuladores conocidos, herramientas de
instalación/desinstalación y accesos ocultos. No trata DisplayIcon ni
UninstallString de cualquier programa como comando para ejecutarlo.
La cobertura depende de que la aplicación registre un acceso ejecutable; no se
promete descubrir binarios portátiles sin acceso ni clasificar software sin
metadatos. Windows requiere validación nativa adicional.

## 3. Persistencia y biblioteca

`enabledLocalApps` conserva identificadores estables separados de
`enabledLocalGames`. La escritura local lee el objeto completo y reemplaza
atómicamente el archivo, preservando las preferencias conocidas y campos ajenos.
Los módulos locales comparten un bloqueo por archivo para evitar perder una
selección cuando ambos escriben. Deshabilitar una aplicación no la desinstala.
Véase [Persistencia](08_persistencia_configuracion.md).

## 4. Interfaz y mando

Configuración añade Aplicaciones y el botón Añadir aplicación. El diálogo presenta
icono, nombre y estado de visibilidad, con recarga, lista vacía y errores.
Arriba/abajo mueve el foco y desplaza la lista; A alterna; B cierra. El colector
de configuración se detiene mientras está abierto el diálogo hijo, según
[Mando y foco](12_manejo_gamepad_y_foco.md). Las selecciones se guardan antes de
volver; la biblioteca recarga al cerrar configuración y agrupa estas entradas
como Aplicaciones.

## 5. Ejecución y retorno

Se comparte la gestión de procesos nativos con juegos, sin añadir una shell.
La UI se suspende mientras la aplicación está activa; se permite el ratón del
mando y el OSK existente para usar herramientas de escritorio. START + BACK
solicita el cierre del árbol iniciado. El cierre natural o solicitado retorna a
la biblioteca y recupera el foco. La cancelación libera el proceso propio.
Aplicaciones que delegan en una instancia ajena pueden finalizar su lanzador
antes que su ventana; no se cierran procesos ajenos por nombre.

## 6. Desarrollo y validación

Documentación antes del código y TDD exclusivamente mediante casos de uso.
Cobertura de contratos, exclusiones (incluida la diferencia entre Dolphin
administrador y emulador), prioridad de accesos, persistencia compartida,
ejecución/cierre/cancelación. Se mantiene la regresión de juegos locales al
extraer las utilidades comunes. Verificación gráfica aislada con eventos de
mando y procesos temporales; Los resultados y limitaciones se detallan a continuación.

@author Daniel Figueroa Vidal
@since 2026-10-11

## 7. Resultados de validación (2026-10-11)

- `./gradlew test --offline --console=plain`: 120 pruebas, sin fallos ni errores.
  Los diez casos nuevos de aplicaciones cubren contratos, clasificación, detección,
  prioridad/ocultación, preferencias simultáneas y cierre/cancelación de procesos.
- TDD: clasificación y detección fallaron antes de implementar sus contratos;
  la exclusión adicional de CurseForge/Prism Launcher falló antes de corregirla.
- Pantalla virtual X11 de 1280×900: selector con 15 entradas temporales,
  activación con A, desplazamiento hasta la fila 14, B sin cerrar el padre,
  reapertura conservando selección y conexión real de Koin.
- Recorrido con configuración aislada: biblioteca → proceso temporal → cierre
  solicitado → biblioteca. Capturas revisadas para comprobar foco y legibilidad.
- Escaneo real de Linux realizado sin lanzar aplicaciones del usuario. La prueba
  gráfica usa configuración propia en `/tmp`, sin modificar la configuración real.
- Pendiente de comprobación en hardware: Windows nativo y mando físico con
  aplicaciones reales. El ratón y OSK reutilizan la lógica existente de suspensión;
  esta verificación no certifica su interacción con cada programa externo.
- Se excluyen lanzadores de juegos conocidos además de juegos, emuladores y
  navegadores. Los accesos de software sin categorías o identidad reconocible
  siguen dependiendo de los metadatos que publique su instalación.

## 8. Componentes compartidos

`systemApps/data/DesktopApplicationReader` conserva el lector y los iconos que
usaban los juegos, incluidas las mejoras de Flatpak y nombres reverse-DNS.
`NativeApplicationProcess` controla una sesión propia por adaptador.
`config/LocalSelectionStore` comparte el bloqueo entre ambos módulos locales.
No cambia la admisión previa de Bottles/Citra dentro de juegos locales; el filtro
independiente de aplicaciones sí los excluye de este nuevo catálogo.

## 9. Corrección de carga innecesaria (2026-10-11)

La implementación inicial llamaba al catálogo completo para cargar las entradas
habilitadas, incluso con selección vacía. Además, el lector compartido resolvía
iconos antes de clasificar: juegos también buscaba iconos de todas las aplicaciones.
Cada icono ausente podía recorrer múltiples temas y directorios del usuario.

Se corrige sin almacenar un catálogo obsoleto: selección vacía retorna sin escanear;
en Linux se filtran identificadores antes de abrir accesos y se resuelve el icono
solo después de aceptar la categoría. La detección completa queda para Ajustes.
Habilitar o ejecutar valida únicamente el identificador solicitado. Windows evita
la detección si la selección está vacía; con selección usa el detector nativo y
filtra sus resultados. Se mantienen precedencia XDG, Hidden y las exclusiones.

Las pruebas de casos de uso verificarán el trabajo omitido mediante un resolutor
de iconos controlado, sin depender de límites de tiempo frágiles. Se medirán además
los tiempos del catálogo real con configuración temporal. Véase
[Juegos locales](23_juegos_locales.md) y [Contexto](19_contexto_ia.md).

## 10. Validación de la corrección (2026-10-11)

Medición con el catálogo real de Linux y configuración temporal: selección vacía
0 ms, un juego seleccionado 24 ms y catálogo completo de juegos 28 ms. El catálogo
completo de aplicaciones (78 accesos) tarda unos 3 s por la búsqueda de iconos y
solo se ejecuta al abrir Ajustes, nunca al cargar la biblioteca.

Se añadió la prueba de casos de uso `discovery_resolvesReverseDnsIcons`, que
protege los iconos con nombre reverse-DNS (por ejemplo SuperTux). Véase
[Juegos locales](23_juegos_locales.md) y [Contexto](19_contexto_ia.md).
