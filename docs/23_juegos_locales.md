# 23. Juegos locales de PC

## 1. Objetivo y arquitectura

El módulo `localGames` incorpora juegos instalados bajo `Platforms.LOCALGAME`.
La capa de datos detecta y ejecuta aplicaciones; entidades y casos de uso definen
las operaciones de biblioteca; los ViewModels publican el estado a Compose.
Se sigue [Arquitectura](01_arquitectura.md) e [Inyección Koin](07_inyeccion_dependencias_koin.md).

## 2. Detección y selección

Linux lee la sección Desktop Entry de archivos `.desktop` en aplicaciones del
usuario, sistema y exportaciones Flatpak. Solo admite Type=Application y categoría
Game; omite Hidden/NoDisplay y emuladores/launchers ya integrados. Respeta la
prioridad del usuario y elimina duplicados. Exec se convierte en argumentos sin
interpolar una shell; se eliminan marcadores de archivos y se expanden nombre,
icono y ruta cuando procede. Flatpak conserva los argumentos del acceso directo.
Los identificadores son estables y los iconos se resuelven cuando existe un archivo.

Windows consulta GameUX, desinstalación marcada como juego y accesos directos de
las carpetas Games/Juegos del menú Inicio. Solo admite destinos ejecutables o
accesos directos resolubles; no ejecuta comandos de desinstalación ni adivina un
juego a partir de cualquier software instalado. La detección puede omitir juegos
sin metadatos de categoría o acceso directo. La validación Windows requiere un
host Windows.

## 3. Persistencia

`enabledLocalGames` guarda identificadores en `AppConfig`. Las modificaciones
locales leen el documento completo y reemplazan el archivo mediante movimiento
atómico, preservando campos ajenos y sin sobrescribir una configuración ilegible.
Véase [Persistencia](08_persistencia_configuracion.md). Deshabilitar no borra juegos.

## 4. Interfaz y mando

Configuración añade Juegos Locales de PC y ADD Local Game. El diálogo muestra
nombre, icono, comando y estado; A alterna, arriba/abajo recorre con scroll y B
vuelve. Durante su apertura la configuración deja de recoger eventos del mando.
Carga, error y lista vacía se muestran explícitamente. Las preferencias se guardan
al alternar y la biblioteca se recarga al terminar la configuración.
Véase [Mando y foco](12_manejo_gamepad_y_foco.md).

## 5. Ejecución y cierre

El juego se lanza con ProcessBuilder y argumentos, conservando su directorio de
trabajo. La interfaz permanece suspendida durante su ejecución. START + BACK
cierra exclusivamente el árbol iniciado por GlobalConsole. La finalización
natural devuelve el control a la biblioteca y restaura el foco de su ventana.
Los programas que delegan en otra instancia ajena pueden terminar el lanzador
antes que el juego; no se cierran procesos ajenos por nombre como alternativa.

## 6. Desarrollo y verificación

Documentación previa; TDD de casos de uso con repositorio falso y fixtures de
detección/persistencia mediante repositorio real. Casos: habilitar instalado,
rechazar desconocido, evitar duplicados, deshabilitar sin borrar, conservar
preferencias, lanzar y cerrar, y propagar fallos. Pruebas en verde antes de UI.
La comprobación gráfica y de procesos complementa las pruebas de dominio.

@author Daniel Figueroa Vidal
@since 2026-10-11
