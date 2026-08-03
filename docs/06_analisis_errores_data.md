# 06. Análisis de Errores Detectados en la Capa de Datos

Este documento detalla un error lógico identificado en la capa de datos (`data`) de GlobalConsole durante la revisión para la implementación de los Casos de Uso. De acuerdo con las directrices del proyecto, no se ha modificado el código de datos directamente, sino que se documenta aquí para su posterior corrección.

---

## 🔍 Inconsistencia en la Eliminación de Juegos (`deleteGameP2`)

### 1. Descripción del Problema
Se ha detectado que la función de eliminación física de un juego falla silenciosamente o retorna siempre `false` debido a un desajuste en los parámetros pasados entre el repositorio y el adaptador de sistema de archivos.

### 2. Ubicación del Código Afectado

* **Repositorio:** [GameP2RepositoryImpl.kt](file:///home/figue/Documentos/GlobalConsole/desktopApp/src/main/kotlin/org/example/globalconsole/juegosPcsx2/data/repositoryImpl/GameP2RepositoryImpl.kt#L52-L55)
  ```kotlin
  override suspend fun deleteGameP2(id: String): Boolean {
      val nameOfGameIso: String = getGameP2ById(id)?.name ?: return false
      return dataSourceFile.deleteGameInFile(nameOfGameIso)
  }
  ```
  *Nota:* Aquí se obtiene el **nombre** del juego (ej: `"Gran Turismo 4"`) y se envía al adaptador.

* **Adaptador:** [GameP2FileSystemAdapter.kt](file:///home/figue/Documentos/GlobalConsole/desktopApp/src/main/kotlin/org/example/globalconsole/juegosPcsx2/data/database/GameP2FileSystemAdapter.kt#L75-L90)
  ```kotlin
  private fun deleteGameForSupportedOs(id: String): Boolean {
      // ...
      pcsxFolder.walkTopDown().forEach { file ->
          if (file.isFile && file.extension.equals("iso", ignoreCase = true)) {
              currentId++
              if ("pcsx2$currentId" == id) {
                  fileToDelete = file
                  return@forEach
              }
          }
      }
      // ...
  }
  ```
  *Nota:* El adaptador compara el parámetro recibido con el formato `"pcsx2$currentId"`, el cual representa al **ID** del juego (ej: `"pcsx21"`), no al nombre.

### 3. Conclusión e Impacto
Al pasar el nombre del juego (`nameOfGameIso`) en vez del identificador (`id`), la condición `"pcsx2$currentId" == id` nunca se cumple, impidiendo que el archivo sea borrado físicamente.

---

## 🛠️ Corrección Recomendada

Modificar [GameP2RepositoryImpl.kt](file:///home/figue/Documentos/GlobalConsole/desktopApp/src/main/kotlin/org/example/globalconsole/juegosPcsx2/data/repositoryImpl/GameP2RepositoryImpl.kt#L52-L55) para que pase directamente el `id` recibido en el parámetro, en vez de extraer el nombre:

```kotlin
override suspend fun deleteGameP2(id: String): Boolean {
    // Pasar el id directamente al adaptador
    return dataSourceFile.deleteGameInFile(id)
}
```

---

## 🔗 Referencias Cruzadas
- Arquitectura Clean: [01_arquitectura.md](01_arquitectura.md)
- Contexto de la IA: [05_contexto_ia.md](05_contexto_ia.md)

@author Daniel Figueroa Vidal
@since 2026-08-03
