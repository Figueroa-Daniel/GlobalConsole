# 1. GlobalConsole

Frontend de escritorio para una biblioteca de juegos, emuladores y aplicaciones,
con interfaz Metro y navegación mediante mando. Desarrollado con Kotlin, Compose
Multiplatform y arquitectura MVVM con casos de uso.

## 1.1. Empezar

Consulta la [guía de usuario](docs/guias/README.md) para preparar emuladores,
carpetas, carátulas y controles. En Ajustes puedes elegir ejecutables de PCSX2,
melonDS, Dolphin, Azahar, DuckStation y RPCS3. Las carpetas de juegos y los
binarios se configuran por separado. La configuración se guarda en `config.json`
del directorio de trabajo.

## 1.2. Desarrollo

Necesitas un JDK compatible con el Gradle Wrapper del proyecto y un entorno
gráfico de escritorio. Desde la raíz:

```sh
./gradlew :desktopApp:run
./gradlew :desktopApp:test
```

En Windows usa `gradlew.bat`. La primera compilación requiere descargar las
dependencias. Las pruebas usan un directorio de trabajo aislado de la
configuración personal.

## 1.3. Documentación y límites

- [Índice técnico](docs/README.md) y [arquitectura](docs/01_arquitectura.md).
- [Controles del mando](docs/guias/02_uso_del_mando.md).
- [Emuladores y juegos](docs/guias/04_emuladores_y_juegos.md).
- [Solución de problemas y validación pendiente](docs/guias/06_solucion_de_problemas.md).
- [Reglas de contribución](.agents/AGENTS.md).

Las pruebas de comandos no sustituyen la comprobación física con cada emulador,
sistema operativo y mando. RPCS3 se integra como launcher; no escanea ROMs de PS3.
