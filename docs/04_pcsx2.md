# 04. Integración de PCSX2 en GlobalConsole

Este documento detalla el mecanismo utilizado en GlobalConsole para integrar y ejecutar el emulador PCSX2 en sistemas operativos Windows y Linux de forma nativa.

---

## ⚙️ 1. Flujo de Ejecución

La comunicación con emuladores externos debe hacerse a través de procesos nativos del sistema operativo (`ProcessBuilder`), controlando el ciclo de vida de la aplicación de extremo a extremo:

```
[GlobalConsole UI] ──> Lanza Proceso PCSX2 (Fullscreen & NoGUI) ──>
                     [UI de GlobalConsole queda en segundo plano]
                     ──> Espera a la terminación del Proceso ──>
                     [Retorno limpio a GlobalConsole UI]
```

### Argumentos de Lanzamiento
Para asegurar una experiencia de usuario fluida e integrada con una interfaz de tipo mando de consola (10-foot UI), el proceso se lanza con los siguientes parámetros:
- `-fullscreen`: Inicia el emulador a pantalla completa directamente.
- `-nogui`: Evita abrir la interfaz gráfica de configuración del emulador de PCSX2, lanzando directamente el archivo del juego.

---

## 🖥️ 2. Detalles por Sistema Operativo

El adaptador `GamePCSX2Adapter` detecta automáticamente el sistema operativo y aplica el comando adecuado:

### Linux (Flatpak)
- **Comando:** `flatpak run net.pcsx2.PCSX2 -fullscreen -nogui <ruta_iso>`
- **Por qué:** Flatpak es la distribución de emuladores estándar recomendada y más común en plataformas portátiles de Linux (como Steam Deck).

### Windows
- **Comando:** `<Ruta_instalacion>/pcsx2-qt.exe -fullscreen -nogui <ruta_iso>`
- **Por qué:** Ejecución nativa a través del binario de Qt de PCSX2 de Windows.

---

## 🗄️ 3. Adaptadores de Datos de PCSX2

- **GameP2FileSystemAdapter:** Escanea recursivamente el directorio de juegos configurado buscando archivos con extensión `.iso` de PS2 y generando dinámicamente los objetos `GameP2Dto`.
- **GamePCSX2Adapter:** Responsable del lanzamiento asíncrono y control de excepciones del proceso de ejecución nativo.

---

## 🔗 4. Referencias Cruzadas
- Conocer la arquitectura general de capas en [01_arquitectura.md](01_arquitectura.md).
- Consultar los detalles de tecnologías asociadas (como Coroutines para el proceso asíncrono) en [02_tecnologias.md](02_tecnologias.md).
- Revisar la ubicación de los archivos del emulador en [03_modulos.md](03_modulos.md).
