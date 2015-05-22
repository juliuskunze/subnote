package com.mindforge.graphics.android

import android.content.res.Resources
import java.util.HashMap
import java.io.InputStreamReader
import android.opengl.GLES20
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import com.mindforge.app.R
import com.mindforge.graphics.*
import com.mindforge.graphics.math.Shape
import java.util.ArrayList

//TODO: "override val original" instad of "val originalTextElement  (runtime compiler barf)
class GlTextElement(val originalTextElement: TextElement, screen: GlScreen) : TextElement, GlColoredElement(originalTextElement, screen) {
    override val shader = screen.fontShader
    override val shape: GlGlyphs get() = super<GlColoredElement>.shape as GlGlyphs
    override val font: GlFont get() = glFont(originalTextElement.font)
    override val lineHeight: Float get() = originalTextElement.lineHeight.toFloat()
    override val fill: Fill get() = originalTextElement.fill
    override val content: String get() = originalTextElement.content
}

class GlFont(resources: Resources) : Font {

    class Page(val id: Int, val file: String)
    class Glyph(val id: Int, val x: Int, val y: Int, val width: Int, val height: Int,
                val xOffset: Int, val yOffset: Int, val xAdvance: Int, val page: Int)

    val pages = HashMap<Int, Page>()
    val glyphs = HashMap<Char, Glyph>()
    val kernings = HashMap<Int, HashMap<Int, Int>>()

    init {
        val stream = resources.openRawResource(R.raw.roboto_regular)
        try {
            val reader = InputStreamReader(stream, "UTF-8")
            reader.forEachLine {
                val entries = (it.split(' ') filter { it.any() }).toList()
                if (entries.any()) {
                    val tag = entries[0]
                    val values = (entries drop 1 map {
                        val strings = it.split("=")
                        Pair(strings.first(), strings.last())
                    }).toMap()
                    // TODO: use "when" instead (causes compiler barf right now)
                    val id = values["id"]?.toInt() ?: 0
                    if (tag == ("page")) {
                        pages.put(id, Page(id, values["file"] ?: ""))
                    } else if (tag == ("char")) {
                        glyphs.put(id.toChar(), Glyph(id,
                                values["x"]?.toInt() ?: 0, values["y"]?.toInt() ?: 0,
                                values["width"]?.toInt() ?: 0, values["height"]?.toInt() ?: 0,
                                values["xoffset"]?.toInt() ?: 0, values["yoffset"]?.toInt() ?: 0,
                                values["xadvance"]?.toInt() ?: 0, values["page"]?.toInt() ?: 0))
                    } else if (tag == ("kerning")) {
                        val first = values["first"]?.toInt() ?: 0
                        val second = values["second"]?.toInt() ?: 0
                        val amount = values["amount"]?.toInt() ?: 0
                        val map = kernings.getOrPut(first) { -> HashMap<Int, Int>() }
                        map.put(second, amount)
                    }
                }
            }
        } finally {
            stream.close()
        }
    }

    private val textureNames: IntArray = IntArray(1);
    val textureName: Int get() = textureNames[0]

    init {
        GLES20.glGenTextures(textureNames.size, textureNames, 0);
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.roboto_regular)!!;

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // We are done using the bitmap so we should recycle it.
        bmp.recycle();

    }

    override fun shape(text: String, lineHeight: Number): TextShape {
        return GlGlyphs(this, text, lineHeight.toFloat())
    }
}

fun glFont(original: Font): GlFont {
    return when (original) {
        is GlFont -> original
        else -> throw UnsupportedOperationException("No OpenGL implementation for font '${original}'.")
    }
}

class GlGlyphs(val font: GlFont, override val text: String, override val lineHeight: Float) : GlShape(), TextShape {
    override val textureName: Int = font.textureName
    override val vertexCoordinates = FloatArray(text.length() * 2 * 4)
    override val textureCoordinates = FloatArray(text.length() * 2 * 4)
    override val drawOrder = ShortArray(text.length() * 6)
    override val glVertexMode: Int = GLES20.GL_TRIANGLES

    override val lines = ArrayList<LineShape>()

    //TODO: read these from texture file
    val templateSize = 40
    val textureSize = 1024
    val templateBaseline = 6.0f

    override val baseline = templateBaseline * lineHeight / templateSize

    //TODO (YAGNI?)
    override val leading = 0

    init {
        var ic = 0
        var it = 0
        var n: Short = 0

        fun addVertex(x: Float, y: Float, tx: Float, ty: Float): Short {
            vertexCoordinates[ic++] = x * lineHeight / templateSize
            vertexCoordinates[ic++] = y * lineHeight / templateSize
            textureCoordinates[it++] = tx / textureSize
            textureCoordinates[it++] = ty / textureSize
            return n++
        }

        var id = 0
        fun addQuad(x: Float, y: Float, w: Float, h: Float, tx: Float, ty: Float) {
            var bl = addVertex(x, y, tx, ty + h)
            var br = addVertex(x + w, y, tx + w, ty + h)
            var tr = addVertex(x + w, y + h, tx + w, ty)
            var tl = addVertex(x, y + h, tx, ty)

            drawOrder[id++] = bl
            drawOrder[id++] = br
            drawOrder[id++] = tl
            drawOrder[id++] = tr
            drawOrder[id++] = tl
            drawOrder[id++] = br

        }

        fun addLine(width: Float) {
            lines.add(object : LineShape {
                override val width = width * lineHeight / templateSize
                override fun contains(location: Vector2) =
                        location.x.toFloat () < width && (location.y.toFloat () + baseline) < lineHeight.toFloat()
            })
        }

        var cx = 0f
        var cy = templateSize.toFloat()
        for (char in text) {
            val glyph = font.glyphs[char] ?: font.glyphs[' ']!!
            val x = cx + glyph.xOffset
            val y = cy - glyph.height - glyph.yOffset
            val w = glyph.width.toFloat()
            val h = glyph.height.toFloat()
            val tx = glyph.x.toFloat()
            val ty = glyph.y.toFloat()
            addQuad(x, y, w, h, tx, ty)
            cx += glyph.xAdvance
            if (char == 10.toChar()) {
                addLine(cx)
                cx = 0f
                cy -= templateSize
                //TODO: add leading (YAGNI?)
            }
        }
        addLine(cx)
    }

    private val box = glShape(super<TextShape>.box())
    //TODO why override?
    //override fun box() = box

    // TODO: should technically test for the exact text shape, but YAGNI
    override fun contains(location: Vector2) = box.contains(location)

}