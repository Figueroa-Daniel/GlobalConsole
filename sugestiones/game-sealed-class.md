# Sugerencia: Cambiar `Game` de `open class` a `sealed class`

> **Estado:** Pendiente de decisión. Documento local — no versionado en Git.

---

## Situación actual

```kotlin
open class Game(
    open val id: String,
    open val name: String,
    open val urlGameExecute: String,
    open val image: String?,
    open val platform: Platforms
)
```

`GameP2` hereda de `Game` con `override` en todas las propiedades.

---

## Beneficios de migrar a `sealed class` o `sealed interface`

### 1. Exhaustividad en `when` garantizada por el compilador
Con `open class`, si añades una nueva plataforma y olvidas manejarla en un `when`, el compilador
**no avisa**. Con `sealed class` el compilador exige que todos los subtipos estén cubiertos:

```kotlin
// Con sealed class — esto FALLA en compilación si falta un tipo:
when (game) {
    is GameP2       -> launchPcsx2(game)
    is GameHeroic   -> launchHeroic(game)
    // Sin else necesario, el compilador lo garantiza
}
```

### 2. Smart cast automático
Al hacer `when (game) { is GameP2 -> ... }`, dentro del bloque `game` es automáticamente
casteado a `GameP2` sin necesidad de cast manual.

### 3. Cierre del modelo en el módulo
`sealed class` solo puede ser extendida dentro del mismo módulo (o paquete en Kotlin 1.5+).
Esto evita que código externo añada subtipos inesperados, lo cual es deseable para un modelo de dominio estable.

### 4. Encaja mejor con el patrón de estados
El mismo patrón ya se usa en `HomeUiState` (sealed interface). Usar `sealed class` en las
entidades mantiene coherencia en el estilo del proyecto.

---

## Cambios necesarios para la migración

1. Cambiar `open class Game` → `sealed class Game`
2. Mover `GameP2` al mismo paquete que `Game` (restricción de `sealed` en Kotlin <2.0), o
   usar `sealed interface Game` (más flexible, `GameP2` puede estar en cualquier subpaquete)
3. Actualizar todos los `when (game)` o `when (game.platform)` para ser exhaustivos
4. Eliminar los `override` en `GameP2` si se usa `data class` con delegación

---

## Recomendación

Migrar a **`sealed interface Game`** cuando se añada la segunda fuente de plataforma.
En ese momento el modelo de herencia quedará fijo y el esfuerzo de migración será mínimo.
