package com.mindforge.graphics.android

import com.mindforge.graphics.Matrix3
import com.mindforge.graphics.Transform2
import com.mindforge.graphics.Transforms2
import com.mindforge.graphics.Vector2
import kotlin.properties.Delegates

class GlMatrix(val values: FloatArray) : Matrix3 {

    constructor(m: Matrix3) : this(floatArrayOf(m.a.toFloat(), m.d.toFloat(), 0f, m.g.toFloat(),
            m.b.toFloat(), m.e.toFloat(), 0f, m.h.toFloat(),
            0f, 0f, 1f, 0f,
            m.c.toFloat(), m.f.toFloat(), 0f, m.i.toFloat())
    )

    override val a: Float get() = values[0]
    override val b: Float get() = values[4]
    override val c: Float get() = values[12]
    override val d: Float get() = values[1]
    override val e: Float get() = values[5]
    override val f: Float get() = values[13]
    override val g: Float get() = values[3]
    override val h: Float get() = values[7]
    override val i: Float get() = values[15]

    private val inverse by Delegates.lazy {
        val mInv = FloatArray(4 * 4)
        if (android.opengl.Matrix.invertM(mInv, 0, values, 0)) GlMatrix(mInv)
        else null
    }

    override fun inverse() = inverse
    override fun times(other: Matrix3): GlMatrix {
        val result = FloatArray(4 * 4)
        val lhs = this.values
        val rhs = glMatrix(other).values
        android.opengl.Matrix.multiplyMM(result, 0, lhs, 0, rhs, 0)
        return GlMatrix(result)
    }

}

fun glMatrix(original: Matrix3): GlMatrix = when (original) {
    is GlMatrix -> original
    else -> GlMatrix(original)
}

class GlTransform(override val matrix: GlMatrix) : Transform2 {
    override fun before(other: Transform2): GlTransform = GlTransform(glMatrix(other.matrix) * matrix)
    override fun at(location: Vector2): GlTransform = glTransform(Transforms2.translation(-location)).before(this).before(Transforms2.translation(location))
    override fun inverse(): GlTransform = GlTransform(matrix.inverse()!!)
}

fun glTransform(original: Transform2): GlTransform = when (original) {
    is GlTransform -> original
    else -> GlTransform(GlMatrix(original.matrix))
}