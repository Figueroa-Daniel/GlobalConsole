# 10. Manejo de Imágenes y Carátulas

Este documento describe la arquitectura y decisiones técnicas relacionadas con la gestión, carga y renderizado de imágenes (carátulas de juegos) en la interfaz gráfica de GlobalConsole.

---

## 🖼️ 1. Objetivo y Contexto

El sistema de renderizado de la UI ("10-foot UI") depende fuertemente de la presentación visual de los juegos mediante carátulas ("covers"). El desafío técnico principal es realizar la lectura del sistema de archivos o peticiones asíncronas de estas imágenes sin afectar el rendimiento de los 60/120 FPS esperados en la interfaz desarrollada con Compose Multiplatform.

---

## ⚙️ 2. Dependencia Técnica: Coil

Para la carga asíncrona de imágenes se ha integrado **Coil**.
- **Por qué Coil:** Es la librería recomendada para Compose Multiplatform. Está escrita completamente en Kotlin (basada en Coroutines) y maneja automáticamente la lectura de archivos locales, el escalado de bitmaps en hilos de fondo y la caché de memoria/disco.
- **Implementación en UI:** Se utiliza el componente `AsyncImage` dentro del módulo de Compose para inyectar la ruta absoluta o recurso de la imagen.

---

## 🔍 3. Escaneo y Entidades de Dominio

### Entidad de Juego (UI)
La entidad que representa el juego en la capa de presentación ha sido actualizada para contener el campo `coverPath` (String/URI), el cual almacena la ruta de la carátula escaneada o la carátula por defecto si el juego no dispone de una específica en el sistema.

### Adaptadores Nativos
En la capa de datos, los repositorios encargados del escaneo dinámico de los juegos (ej. integraciones de emuladores o Heroic Games Launcher) se encargan de resolver de forma asíncrona (vía `Dispatchers.IO`) la ruta de la imagen vinculada a cada título encontrado. Este mapeo ocurre en el *Data Layer* antes de que los datos lleguen al `ViewModel`.

---

## 🧩 4. Renderizado en Interfaz (GameTile)

El componente principal encargado de mostrar la carátula es `GameTile`.
- **Renderizado por defecto:** Si no se dispone de una carátula válida, el componente renderiza una imagen, gradiente o asset visual por defecto para mantener la consistencia estética.
- **Integración:** El `GameTile` reacciona al estado de foco del gamepad para destacar la carátula activa usando animaciones o bordes de selección nativos en Compose.

---

## 🔗 5. Referencias Cruzadas

- Entender el stack tecnológico utilizado en [02_tecnologias.md](02_tecnologias.md).
- Revisar la arquitectura general en [01_arquitectura.md](01_arquitectura.md).
