package com.mindforge.graphics.android

import android.opengl.GLES20
import com.mindforge.graphics.Vector2
import com.mindforge.graphics.math.Rectangle
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.TransformedShape
import com.mindforge.graphics.vector

abstract class GlShape(open val original: Shape? = null) : Shape {
    override fun contains(location: Vector2): Boolean = original?.contains(location) ?: false
    abstract val vertexCoordinates: FloatArray
    abstract val textureCoordinates: FloatArray
    abstract val textureName: Int?
    abstract val drawOrder: ShortArray
    abstract val glVertexMode: Int
}

fun glShape(original: Shape): GlShape = when (original) {
    is GlShape -> original
    is TransformedShape -> GlTransformedShape(original)
    is Rectangle -> GlRectangle(original)
    else -> GlUnknownShape(original)
}

class GlUnknownShape(original: Shape) : GlShape(original), Shape by original {
    val ex : Throwable get() = UnsupportedOperationException("No OpenGL implementation for shape '${original}'.")
    override val vertexCoordinates: FloatArray get() = throw ex
    override val textureCoordinates: FloatArray get() = throw ex
    override val textureName: Int? get() = throw ex
    override val drawOrder: ShortArray get() = throw ex
    override val glVertexMode: Int get() = throw ex
}

class GlTransformedShape(override val original: TransformedShape) : GlShape(original), TransformedShape {
    val glOriginal = glShape(original.original)
    override val vertexCoordinates: FloatArray get() {
        val originalCoordinates = glOriginal.vertexCoordinates
        val result = FloatArray(originalCoordinates.size())
        val transformed = (glOriginal.vertexCoordinates.withIndices() groupBy { it.first / 2 }).toSortedMap().values() flatMap {
            original.transform(vector(it[0].second, it[1].second)) map { it.toFloat() }
        }
        transformed.withIndex().forEach { result[it.index] = it.value }
        return result
    }
    override val textureCoordinates: FloatArray get() = glOriginal.textureCoordinates
    override val textureName: Int? get() = glOriginal.textureName
    override val drawOrder: ShortArray get() = glOriginal.drawOrder
    override val glVertexMode: Int get() = glOriginal.glVertexMode
    override val transform: GlTransform = glTransform(original.transform)
    override fun contains(location: Vector2) = glOriginal.contains(transform.inverse()(location))
}

class GlRectangle(override val original: Rectangle) : GlShape(original) {
    override val vertexCoordinates: FloatArray get() {
        val x = original.size.x.toFloat() / 2
        val y = original.size.y.toFloat() / 2
        return floatArrayOf(+x, +y, // 0 top right
                -x, +y, // 1 top left
                -x, -y, // 2 bottom left
                +x, -y // 3 bottom right
        )
    }
    override val textureCoordinates: FloatArray = floatArrayOf(1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 0f
    )
    override val textureName = null
    override val drawOrder = shortArrayOf(0, 1, 2, 3)
    override val glVertexMode: Int = GLES20.GL_TRIANGLE_FAN
}