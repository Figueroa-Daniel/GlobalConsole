# Sugerencia: Dispatch de ejecución de juegos por plataforma

> **Estado:** Pendiente de decisión. Documento local — no versionado en Git.

---

## Contexto del problema

Actualmente existe `ExecuteGameP2UseCase` específico de PCSX2. Cuando la pantalla principal
muestre juegos de múltiples fuentes (PCSX2, Heroic, nativos), el `HomeViewModel` necesita
saber cómo lanzar cada uno sin acoplarse a cada UseCase específico.

---

## Opciones de diseño

### Opción A — Dispatch en el ViewModel (actual, provisional)
El ViewModel recibe todos los UseCases de ejecución y hace el `when` por `platform`:

```kotlin
fun onGameSelected(game: Game) {
    viewModelScope.launch {
        when (game.platform) {
            Platforms.PCSX2      -> executeGameP2UseCase(game.id)
            Platforms.LOCALGAME  -> executeLocalGameUseCase(game.id)
            // Heroic...
        }
    }
}
```

**Pros:** Simple, sin abstracción adicional.  
**Contras:** El ViewModel crece con cada nueva plataforma. Viola el principio Open/Closed.

---

### Opción B — UseCase genérico `ExecuteGameUseCase` (recomendada)
Un único UseCase recibe un `Game` y delega internamente:

```kotlin
class ExecuteGameUseCase(
    private val executeP2: ExecuteGameP2UseCase,
    private val executeLocal: ExecuteLocalGameUseCase
) {
    suspend operator fun invoke(game: Game): Boolean = when (game.platform) {
        Platforms.PCSX2     -> executeP2(game.id)
        Platforms.LOCALGAME -> executeLocal(game.id)
    }
}
```

**Pros:** El ViewModel solo conoce un UseCase. Escala añadiendo al `when`.  
**Contras:** Requiere refactor del dominio cuando se añade plataforma.

---

### Opción C — Strategy / polimorfismo por plataforma
Cada `Game` hijo implementa o lleva inyectado su propio `executor`. Requiere cambios en la entidad.

**Pros:** Totalmente desacoplado.  
**Contras:** Complica las entidades de dominio con lógica de ejecución.

---

## Recomendación

**Opción B** es la más limpia para Clean Architecture. Implementar cuando se añada la segunda plataforma.
De momento la Opción A (dispatch en ViewModel) es suficiente y fácil de refactorizar.
