package org.example.globalconsole.presesentation.view.components

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Utilidades de recorte de imagen para la funcionalidad de selección de carátulas.
 *
 * Proporciona la lógica de recorte de imagen pura usando la API de AWT/JVM (`BufferedImage`),
 * sin dependencias externas. Se utiliza junto con [ImageCropperDialog] para guardar la región
 * seleccionada por el usuario en disco.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-08-18
 */
object ImageCropperUtils {

    /**
     * Recorta una imagen de origen según la transformación visual aplicada por el usuario
     * (offset y escala) y guarda el resultado en disco como `cover_cropped.png`.
     *
     * El algoritmo traduce las coordenadas del espacio visual (píxeles en pantalla del cuadro
     * de previsualización) al espacio real de la imagen fuente para extraer la región correcta.
     *
     * @param sourcePath Ruta absoluta al archivo de imagen fuente.
     * @param offsetX Desplazamiento horizontal de la imagen aplicado por el usuario (en px de pantalla).
     * @param offsetY Desplazamiento vertical de la imagen aplicado por el usuario (en px de pantalla).
     * @param scale Escala visual aplicada por el usuario (1.0f = sin zoom).
     * @param previewSizePx Tamaño en píxeles del cuadro cuadrado de previsualización en pantalla.
     * @param outputDir Directorio donde se guardará el archivo de salida.
     * @return `true` si el recorte y guardado fueron exitosos, `false` en caso contrario.
     * @throws IllegalArgumentException si el archivo fuente no existe o no es una imagen válida.
     * @author Daniel Figueroa Vidal
     * @since 2026-08-18
     */
    fun cropAndSaveImage(
        sourcePath: String,
        offsetX: Float,
        offsetY: Float,
        scale: Float,
        previewSizePx: Int,
        outputDir: String
    ): Boolean {
        return try {
            val sourceFile = File(sourcePath)
            require(sourceFile.exists()) { "El archivo fuente no existe: $sourcePath" }

            val sourceImage: BufferedImage = ImageIO.read(sourceFile)
                ?: throw IllegalArgumentException("No se pudo leer la imagen: $sourcePath")

            val srcWidth = sourceImage.width.toFloat()
            val srcHeight = sourceImage.height.toFloat()

            // Escala de la imagen renderizada dentro del preview (imagen ajustada al cuadro)
            val baseScale = minOf(previewSizePx / srcWidth, previewSizePx / srcHeight)
            val renderedWidth = srcWidth * baseScale * scale
            val renderedHeight = srcHeight * baseScale * scale

            // Punto de inicio del render (centrado + offset del usuario)
            val renderStartX = (previewSizePx - renderedWidth) / 2f + offsetX
            val renderStartY = (previewSizePx - renderedHeight) / 2f + offsetY

            // Coordenadas en píxeles de la imagen real que corresponden al cuadro de preview
            val srcX = (-renderStartX / (baseScale * scale)).toInt().coerceIn(0, sourceImage.width - 1)
            val srcY = (-renderStartY / (baseScale * scale)).toInt().coerceIn(0, sourceImage.height - 1)
            val srcSize = (previewSizePx / (baseScale * scale)).toInt()
            val clampedSrcSize = srcSize.coerceIn(1, minOf(sourceImage.width - srcX, sourceImage.height - srcY))

            // Recortar y escalar al tamaño final cuadrado (512x512)
            val outputSize = 512
            val cropped = sourceImage.getSubimage(srcX, srcY, clampedSrcSize, clampedSrcSize)
            val output = BufferedImage(outputSize, outputSize, BufferedImage.TYPE_INT_RGB)
            val g2d = output.createGraphics()
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            g2d.drawImage(cropped, 0, 0, outputSize, outputSize, null)
            g2d.dispose()

            val outputFile = File(outputDir, "cover_cropped.png")
            ImageIO.write(output, "png", outputFile)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
