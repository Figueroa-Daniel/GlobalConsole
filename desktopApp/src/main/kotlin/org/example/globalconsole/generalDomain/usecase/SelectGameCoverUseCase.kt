package org.example.globalconsole.generalDomain.usecase

/**
 * Asocia una imagen al juego evitando carátulas genéricas compartidas accidentalmente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-12
 */
class SelectGameCoverUseCase {
    /**
     * Prioriza recorte individual, nombre coincidente y carátula genérica no ambigua.
     * @param gameName Nombre del archivo de juego sin extensión.
     * @param imageNames Nombres de imágenes existentes en su carpeta.
     * @param gamesInFolder Cantidad de juegos en la misma carpeta.
     * @return Nombre seleccionado o null si no hay asociación segura.
     * @throws Nothing No lanza excepciones.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-12
     */
    operator fun invoke(gameName: String, imageNames: List<String>, gamesInFolder: Int): String? {
        val images = imageNames.sorted()
        images.firstOrNull { it.equals("$gameName.cover.png", true) }?.let { return it }
        images.firstOrNull { it.substringBeforeLast('.').equals(gameName, true) }?.let { return it }
        if (gamesInFolder != 1) return null
        for (name in listOf("cover_cropped", "cover", "folder")) {
            images.firstOrNull { it.substringBeforeLast('.').equals(name, true) }?.let { return it }
        }
        return images.singleOrNull()
    }
}
