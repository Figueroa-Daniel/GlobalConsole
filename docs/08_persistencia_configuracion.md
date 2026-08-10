# 08. Persistencia de Configuración con JSON

Este documento describe el diseño e implementación del sistema de persistencia de rutas de emuladores en GlobalConsole, sustituyendo el uso de variables globales por un mecanismo robusto basado en un archivo `config.json`.

---

## 📍 1. Problema que Resuelve

Anteriormente, la ruta de los juegos de PCSX2 se almacenaba en la variable global `ROUTE_PCSX2_GAMES` (`SettingsPlatforms.kt`), que se pierde al cerrar la aplicación. Esto obliga al usuario a configurar la ruta en cada sesión.

---

## 🏗️ 2. Arquitectura del Módulo

Se extiende Clean Architecture con un nuevo módulo `settings` que sigue las mismas capas del resto del proyecto:

```
settings/
├── domain/
│   ├── SettingsRepository.kt        ← Contrato (interfaz)
│   └── usecase/
│       ├── SaveEmulatorPathUseCase.kt
│       └── GetEmulatorPathUseCase.kt
└── data/
    └── SettingsRepositoryImpl.kt    ← Implementación con JSON
```

### Regla de Dependencias
La capa de dominio no conoce ningún detalle de implementación. `SettingsRepositoryImpl` depende de `SettingsRepository` (dominio), nunca al revés.

---

## 💾 3. Formato del Archivo `config.json`

El archivo se genera automáticamente en el directorio de trabajo de la app (`./config.json`).

```json
{
  "emulatorPaths": {
    "pcsx2": "/home/usuario/ISOs/PS2"
  }
}
```

La clave es el `emulatorId` (String), el valor es la ruta absoluta (String). Este diseño permite añadir rutas de nuevos emuladores sin cambiar la estructura del archivo.

---

## 🔄 4. Flujo de Lectura y Escritura

```
[SetupPathDialog] → onGuardar()
        ↓
[SettingsViewModel.savePath("pcsx2", "/ruta")]
        ↓
[SaveEmulatorPathUseCase] → valida ruta → SettingsRepository.saveEmulatorPath()
        ↓
[SettingsRepositoryImpl] → serializa Map → escribe config.json
```

```
[App.start / SetupPathDialog.open]
        ↓
[SettingsViewModel.loadCurrentPath("pcsx2")]
        ↓
[GetEmulatorPathUseCase] → SettingsRepository.getEmulatorPath()
        ↓
[SettingsRepositoryImpl] → lee config.json → deserializa → retorna String?
```

```
[GameP2FileSystemAdapter.getGamesInSystemFile()]
        ↓
[GetEmulatorPathUseCase("pcsx2")]
        ↓
[SettingsRepositoryImpl] → retorna ruta del config.json
```

---

## 🧪 5. Tests

Los tests cubren exclusivamente los UseCases (TDD, según reglas del proyecto):

| Test | Cobertura |
|------|-----------|
| `SaveEmulatorPathUseCaseTest` | Ruta vacía → excepción; ruta válida → persiste |
| `GetEmulatorPathUseCaseTest` | Sin configurar → null; configurada → valor correcto |

Se usa `FakeSettingsRepository` (implementación en memoria) para aislar los tests del disco.

---

## 🔗 6. Referencias Cruzadas
- Arquitectura Clean: [01_arquitectura.md](01_arquitectura.md)
- Tecnologías (kotlinx.serialization): [02_tecnologias.md](02_tecnologias.md)
- Módulos del proyecto: [03_modulos.md](03_modulos.md)
- Contexto IA: [05_contexto_ia.md](05_contexto_ia.md)

@author Daniel Figueroa Vidal
@since 2026-08-10
