package com.mindforge.graphics.android

import android.opengl.GLES20
import com.mindforge.graphics.Vector2
import com.mindforge.graphics.math.BoundedShape
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

fun glShape(original: Shape): GlShape {
    val glShape: GlShape = when (original) {
        is GlShape -> original
        is TransformedShape -> GlTransformedShape(original)
        is Rectangle -> GlRectangle(original)
        else -> throw UnsupportedOperationException("No OpenGL implementation for shape '${original}'.")
    }
    return when (original) {
        is BoundedShape -> GlBoundedShape(original, glShape)
        else -> glShape
    }
}

class GlBoundedShape(override val original: BoundedShape, val glShape: GlShape) : GlShape(original), BoundedShape by original {
    override val vertexCoordinates: FloatArray get() = glShape.vertexCoordinates
    override val textureCoordinates: FloatArray get() = glShape.textureCoordinates
    override val textureName: Int? get() = glShape.textureName
    override val drawOrder: ShortArray get() = glShape.drawOrder
    override val glVertexMode: Int get() = glShape.glVertexMode
}

class GlTransformedShape(override val original: TransformedShape) : GlShape(original) {
    val glOriginal = glShape(original.original)
    override val vertexCoordinates: FloatArray get() {
        val originalCoordinates = glOriginal.vertexCoordinates
        val result = FloatArray(originalCoordinates.size)
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
}

class GlRectangle(override val original: Rectangle) : GlShape(original) {
    override val vertexCoordinates: FloatArray get() {
        val x = original.size.x.toFloat() / 2
        val y = original.size.y.toFloat() / 2
        return floatArray(
                +x, +y, // 0 top right
                -x, +y, // 1 top left
                -x, -y, // 2 bottom left
                +x, -y // 3 bottom right
        )
    }
    override val textureCoordinates: FloatArray = floatArray(
            1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 0f
    )
    override val textureName = null
    override val drawOrder = shortArray(0, 1, 2, 3)
    override val glVertexMode: Int = GLES20.GL_TRIANGLE_FAN
}