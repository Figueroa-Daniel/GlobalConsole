# 11. Recortador de Imagen de Carátulas

Este documento describe la funcionalidad de recorte interactivo de imágenes implementada en GlobalConsole. Permite al usuario seleccionar visualmente qué porción de una imagen se usará como carátula para cada juego, de manera similar a como lo hace Instagram al subir una foto.

---

## 🎯 1. Objetivo y Motivación

Las imágenes de carátula que el usuario coloca en la carpeta de cada juego pueden tener **proporciones y orientaciones variadas**: capturas de pantalla, portadas escaneadas, imágenes en paisaje, etc. La interfaz Metro de GlobalConsole utiliza tiles cuadrados (`aspectRatio(1f)`) y un recorte automático centrado (`ContentScale.Crop`).

El problema es que el recorte automático puede cortar partes importantes de la imagen (el título del juego, la cara del personaje principal, etc.). Para solucionarlo, se implementa un **diálogo de recorte interactivo** que le da el control al usuario.

---

## ⚙️ 2. Decisiones Técnicas

### Sin dependencias externas nuevas
El recortador se implementa usando exclusivamente:
- **Compose Multiplatform** (ya en el proyecto): `pointerInput`, `graphicsLayer`, `Canvas` para la UI interactiva.
- **`java.awt.image.BufferedImage`** (disponible en JVM/Desktop): para el recorte real del bitmap en disco.

**Por qué no Krop:** La librería Krop (de `tamimattafi`) es una alternativa, pero su soporte para Desktop (JVM) no está garantizado en la versión actual de KMP del proyecto. Implementarlo desde cero garantiza el control total del código y cero dependencias nuevas.

---

## 🧩 3. Arquitectura de la Funcionalidad

La funcionalidad se divide en dos componentes en la capa de **Presentación**:

### 3.1 `ImageCropperUtils.kt` (Lógica de recorte, JVM)
Contiene la función pura `cropAndSaveImage(...)`:
- Recibe la ruta de la imagen fuente, el offset (desplazamiento) y la escala visual aplicada por el usuario.
- Carga el `BufferedImage` del archivo.
- Calcula las coordenadas de recorte reales en píxeles de la imagen original.
- Guarda el resultado como `cover_cropped.png` en la misma carpeta de la ISO/ROM.

### 3.2 `ImageCropperDialog.kt` (UI Composable)
Componente `@Composable` en forma de diálogo de pantalla completa:
- Muestra la imagen con transformaciones aplicadas en tiempo real via `graphicsLayer`.
- **Detecta drag** (`detectDragGestures`) para desplazar la imagen.
- **Detecta zoom** (`detectTransformGestures` o scroll del ratón) para escalar.
- Renderiza una **máscara semitransparente** con un recuadro cuadrado central que indica la zona de recorte.
- Ofrece botones **Confirmar** y **Cancelar** en estilo Metro.

### 3.3 Integración en `HomeScreen.kt`
- Se añade un estado `cropTargetGame: Game?` en la composición de `HomeScreen`.
- El diálogo se activa mediante **click secundario (botón derecho del ratón)** sobre cualquier `GameTile`.
- Al confirmar, se llama a `cropAndSaveImage(...)` y se fuerza una recarga del `HomeViewModel` para reflejar la nueva carátula.

---

## 🔄 4. Flujo de Datos

```
Usuario (click derecho en GameTile)
  └─> HomeScreen detecta el evento y almacena `cropTargetGame`
        └─> Se muestra ImageCropperDialog con la ruta de imagen del juego
              └─> El usuario ajusta zoom y posición
                    └─> Pulsa Confirmar
                          └─> ImageCropperUtils.cropAndSaveImage(...)
                                └─> Guarda cover_cropped.png en la carpeta de la ISO
                                      └─> HomeViewModel.refresh() recarga la lista
                                            └─> GameTile muestra la nueva carátula
```

---

## 📁 5. Archivos Involucrados

| Archivo | Capa | Acción |
|---|---|---|
| `ImageCropperUtils.kt` | Presentation | NUEVO |
| `ImageCropperDialog.kt` | Presentation | NUEVO |
| `HomeScreen.kt` | Presentation | MODIFICADO |
| `GameTile.kt` | Presentation | MODIFICADO (onClick secundario) |

---

## 🔗 6. Referencias Cruzadas

- Contexto de la carga de imágenes en [10_manejo_imagenes_y_caratulas.md](10_manejo_imagenes_y_caratulas.md).
- Stack tecnológico en [02_tecnologias.md](02_tecnologias.md).
- Arquitectura de capas en [01_arquitectura.md](01_arquitectura.md).
