package com.mindforge.graphics.math

import com.mindforge.graphics.Transform2
import com.mindforge.graphics.Transforms2
import com.mindforge.graphics.Vector2
import com.mindforge.graphics.zeroVector2

trait Rectangle : Shape {
    val size: Vector2
    val halfSize: Vector2 get() = size / 2

    override fun contains(location: Vector2) = Math.abs(location.x.toDouble()) <= halfSize.x.toDouble() && Math.abs(location.y.toDouble()) <= halfSize.y.toDouble()

    final fun translated(centerLocation: Vector2 = zeroVector2) = object : TranslatedRectangle {
        override val original = this@Rectangle
        override val centerLocation = centerLocation
    }
}

trait TranslatedRectangle : TransformedShape {
    override val original : Rectangle
    val centerLocation: Vector2
    override val transform : Transform2 get() = Transforms2.translation(centerLocation)

    final fun translated(translation: Vector2) : TranslatedRectangle = object : TranslatedRectangle {
        override val original = this@TranslatedRectangle.original
        override val centerLocation = this@TranslatedRectangle.centerLocation + translation
    }
}

fun rectangle(size: Vector2) = object : Rectangle {
    override val size = size
}

fun Rectangle.bottomLeftAtOrigin() = translated(size / 2)
fun Rectangle.topLeftAtOrigin() = translated((size.xComponent() - size.yComponent()) / 2)
fun Rectangle.topRightAtOrigin() = translated(-size / 2)
fun Rectangle.bottomRightAtOrigin() = translated((-size.xComponent() + size.yComponent()) / 2)