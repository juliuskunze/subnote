package com.mindforge.graphics.math

import com.mindforge.graphics.Vector2
import com.mindforge.graphics.zeroVector2

trait Rectangle : Shape {
    val size: Vector2
    val center: Vector2
    val halfSize: Vector2 get() = size / 2

    override fun contains(location: Vector2) = Math.abs(location.x.toDouble()) <= halfSize.x.toDouble() && Math.abs(location.y.toDouble()) <= halfSize.y.toDouble()

    final fun translated(offset: Vector2) = rectangle(size, center + offset)
}

fun rectangle(size: Vector2, center: Vector2 = zeroVector2): Rectangle = object : Rectangle {
    override val size = size
    override val center = center
}

fun rectangle(size: Vector2, quadrant: Int) = rectangle(size, (size / 2).let {
    when (quadrant) {
        1 -> it
        2 -> it.mirrorX()
        3 -> -it
        4 -> it.mirrorY()
        else -> throw IllegalArgumentException("Quadrant must be 1, 2, 3 or 4.")
    }
})