# 1. Revisión de emuladores y biblioteca

## 1.1. Hallazgos confirmados

DuckStation contiene una ruta personal; las variables globales de ejecutables no
se configuran. PCSX2 mezcla carpeta de juegos y ejecutable en Windows; RPCS3
rechaza Windows. El escáner PS1 muestra pistas BIN junto a su CUE y carece de
carátulas. Los demás escáneres eligen imágenes sin asociación al juego.
`onGameDeleted` no tiene consumidores y combina borrado físico con operaciones
sin implementar. El README raíz no existe en esta revisión.

## 1.2. Corrección prevista

Guardar rutas de ejecutables separadas de las carpetas de juegos, con claves
`executable.<id>` en la configuración existente. Un caso de uso construye los
argumentos para Linux/Windows: ruta personalizada, o Flatpak en Linux y nombre
en PATH en Windows. No se interpretan comandos de shell ni se buscan binarios
en carpetas de ROMs. Ajustes incorpora un selector de archivos navegable con
mando; ruta vacía restaura el comportamiento automático. RPCS3 sigue siendo
solo un launcher, también en Windows.

PS1 excluye únicamente los BIN/IMG referenciados por un CUE: no oculta juegos
independientes. Las carátulas priorizan el nombre del juego; las genéricas se
usan solo en carpetas con un juego. Los recortes de juegos emulados se guardan
con nombre propio para evitar que cambie la imagen de otro juego.

Retirar la acción de borrado sin consumidores del ViewModel, sin introducir
borrado físico en la interfaz. Crear README y corregir la guía del usuario.

## 1.3. Arquitectura y pruebas

Mantener [MVVM y casos de uso](01_arquitectura.md), [persistencia](08_persistencia_configuracion.md)
y [aislamiento de modales](12_manejo_gamepad_y_foco.md). Escribir primero pruebas
de casos de uso para comandos, persistencia, asociación de carátulas y escaneo
PS1 con repositorios reales sobre carpetas temporales. Verificar el selector
mediante una ventana temporal y ejecutar la suite completa. No se afirmará
validación física de Windows, mandos o emuladores que no se haya realizado.

## 1.4. Referencias

[Guía de emuladores](guias/04_emuladores_y_juegos.md), [DuckStation](17_duckstation.md),
[Carátulas](10_manejo_imagenes_y_caratulas.md), [Contexto](19_contexto_ia.md).

## 1.5. Resultado y validación

Implementadas las correcciones descritas. Nueve nuevas pruebas de casos de uso;
144 pruebas completas, cero fallos. La fase roja reprodujo el duplicado PS1 y el
éxito ficticio al borrar. Las pruebas de comandos cubren todos los emuladores,
Linux/Windows, rutas con espacios, argumentos y restauración de valores por
defecto. Un proceso Linux temporal verificó la ruta configurada y los códigos
de salida sin abrir RPCS3. La ventana Xvfb verificó selección de archivo con
mando simulado, ausencia de guardado anticipado y aislamiento entre tres modales.

No se ha comprobado hardware físico ni ejecución real en Windows o emuladores.
Se preservaron los cambios previos del usuario en configuración y GamepadManager.
