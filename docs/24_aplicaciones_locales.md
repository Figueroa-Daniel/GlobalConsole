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

Configuración añade Aplicaciones y ADD Local App. El diálogo presenta icono,
nombre, comando y estado de visibilidad, con recarga, lista vacía y errores.
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
mando y procesos temporales; registrar aquí resultados y limitaciones.

@author Daniel Figueroa Vidal
@since 2026-10-11
