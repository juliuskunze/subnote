package com.mindforge.graphics.math

import com.mindforge.graphics.Vector2
import com.mindforge.graphics

interface Ellipse : Shape {
    val size: Vector2
    val boundRectangle: Rectangle get() = rectangle(size)

    override fun contains(location: Vector2): Boolean {
        return if (boundRectangle.halfSize.x == 0) location.x == 0 && Math.abs(location.y.toDouble()) <= boundRectangle.halfSize.y.toDouble()
        else {
            val transformedLocation = graphics.vector((location.x.toDouble() / boundRectangle.halfSize.x.toDouble()) * boundRectangle.halfSize.y.toDouble(), location.y.toDouble())

            return transformedLocation.lengthSquared.toDouble() <= boundRectangle.halfSize.y.toDouble() * boundRectangle.halfSize.y.toDouble()
        }
    }
}

fun ellipse(size: Vector2) = object : Ellipse {
    override val size = size
}