package org.example.globalconsole.presesentation.input

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.NativeLongByReference
import com.sun.jna.Structure
import kotlin.math.abs

/**
 * Interfaz JNA para enlazar con libX11.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal interface X11Native : Library {
    fun XOpenDisplay(displayName: String?): Pointer?
    fun XCloseDisplay(display: Pointer?): Int
    fun XFree(ptr: Pointer?): Int
    fun XDefaultRootWindow(display: Pointer): NativeLong
    fun XQueryPointer(
        display: Pointer, window: NativeLong,
        root: NativeLongByReference, child: NativeLongByReference,
        rootX: IntByReference, rootY: IntByReference,
        windowX: IntByReference, windowY: IntByReference, mask: IntByReference
    ): Int

    companion object {
        val INSTANCE: X11Native? = try {
            Native.load("X11", X11Native::class.java)
        } catch (_: Throwable) {
            null
        }
    }
}

/**
 * Estructura de imagen de cursor devuelta por XFixesGetCursorImage.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
@Structure.FieldOrder("x", "y", "width", "height", "xhot", "yhot", "cursor_serial", "pixels", "atom", "name")
open class XFixesCursorImage : Structure() {
    @JvmField var x: Short = 0
    @JvmField var y: Short = 0
    @JvmField var width: Short = 0
    @JvmField var height: Short = 0
    @JvmField var xhot: Short = 0
    @JvmField var yhot: Short = 0
    @JvmField var cursor_serial: NativeLong = NativeLong()
    @JvmField var pixels: Pointer? = null
    @JvmField var atom: NativeLong = NativeLong()
    @JvmField var name: String? = null

    class ByReference : XFixesCursorImage(), Structure.ByReference
}

/**
 * Interfaz JNA para enlazar con libXfixes.
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
internal interface XFixesNative : Library {
    fun XFixesGetCursorImage(display: Pointer?): XFixesCursorImage.ByReference?

    companion object {
        val INSTANCE: XFixesNative? = try {
            Native.load("Xfixes", XFixesNative::class.java)
        } catch (_: Throwable) {
            null
        }
    }
}

/**
 * Detector del tipo de cursor del sistema bajo X11.
 * Permite identificar si el puntero del ratón está situado actualmente sobre un
 * campo de texto o elemento editable (cursor I-beam / xterm).
 *
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
class SystemCursorDetector : AutoCloseable {

    private var display: Pointer? = null

    init {
        try {
            display = X11Native.INSTANCE?.XOpenDisplay(null)
        } catch (e: Throwable) {
            println("[SystemCursorDetector] No se pudo inicializar la conexión con el servidor X11: ${e.message}")
        }
    }

    /**
     * Determina si el cursor actual del sistema corresponde a un cursor de edición de texto (I-beam/xterm).
     *
     * @return true si el cursor tiene las características o el nombre del cursor de texto, false en caso contrario.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Synchronized
    fun isTextCursor(): Boolean {
        val currentDisplay = display ?: return false
        val xfixes = XFixesNative.INSTANCE ?: return false
        val x11 = X11Native.INSTANCE ?: return false

        try {
            val cursorImg = xfixes.XFixesGetCursorImage(currentDisplay) ?: return false
            try {
                // 1. Comprobación directa por nombre de cursor (si la aplicación o tema lo reporta)
                val cursorName = cursorImg.name?.lowercase() ?: ""
                if (cursorName.contains("xterm") || cursorName.contains("text") || cursorName.contains("ibeam")) {
                    return true
                }

                // 2. Comprobación geométrica del cursor (el I-beam tiene el hotspot centrado)
                val w = cursorImg.width.toInt()
                val h = cursorImg.height.toInt()
                val xhot = cursorImg.xhot.toInt()
                val yhot = cursorImg.yhot.toInt()

                if (w > 0 && h > 0) {
                    val xCenter = w / 2
                    val yCenter = h / 2
                    val isHotspotCentered = abs(xhot - xCenter) <= 2 && abs(yhot - yCenter) <= 3

                    // En el cursor I-beam, los píxeles centrales forman una línea vertical
                    // mientras que las columnas exteriores permanecen vacías.
                    if (isHotspotCentered && cursorImg.pixels != null) {
                        // XFixes usa unsigned long por píxel, no un int de 32 bits.
                        val pixels = cursorImg.pixels!!
                        
                        var centerColFilled = 0
                        var leftColFilled = 0
                        var rightColFilled = 0

                        for (y in 0 until h) {
                            val centerPixelAlpha = (pixels.getNativeLong((y * w + xCenter).toLong() * NativeLong.SIZE).toInt() ushr 24) and 0xFF
                            val leftPixelAlpha = (pixels.getNativeLong((y * w).toLong() * NativeLong.SIZE).toInt() ushr 24) and 0xFF
                            val rightPixelAlpha = (pixels.getNativeLong((y * w + w - 1).toLong() * NativeLong.SIZE).toInt() ushr 24) and 0xFF

                            if (centerPixelAlpha > 50) centerColFilled++
                            if (leftPixelAlpha > 50) leftColFilled++
                            if (rightPixelAlpha > 50) rightColFilled++
                        }

                        // Debe tener una columna central bien definida y bordes laterales vacíos
                        if (centerColFilled >= (h / 3) && leftColFilled <= 2 && rightColFilled <= 2) {
                            return true
                        }
                    }
                }

                return false
            } finally {
                x11.XFree(cursorImg.pointer)
            }
        } catch (e: Throwable) {
            return false
        }
    }

    /**
     * Consulta el botón izquierdo global en X11, también fuera de GlobalConsole.
     * @return True mientras el botón está pulsado; false si X11 no está disponible.
     * @throws Nothing Los errores nativos se convierten en ausencia de detección.
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Synchronized
    fun isPrimaryButtonPressed(): Boolean {
        val currentDisplay = display ?: return false
        val x11 = X11Native.INSTANCE ?: return false
        return try {
            val mask = IntByReference()
            val result = x11.XQueryPointer(
                currentDisplay, x11.XDefaultRootWindow(currentDisplay),
                NativeLongByReference(), NativeLongByReference(),
                IntByReference(), IntByReference(), IntByReference(), IntByReference(), mask
            )
            result != 0 && (mask.value and (1 shl 8)) != 0
        } catch (_: Throwable) {
            false
        }
    }

    /**
     * Cierra la conexión con el servidor X11 si estaba abierta.
     *
     * @author Daniel Figueroa Vidal
     * @since 2026-10-11
     */
    @Synchronized
    override fun close() {
        try {
            display?.let {
                X11Native.INSTANCE?.XCloseDisplay(it)
            }
            display = null
        } catch (_: Throwable) {
        }
    }
}
