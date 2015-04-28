package com.mindforge.graphics.math

import com.mindforge.graphics.*

trait Shape {
    fun contains(location: Vector2): Boolean
    fun transformed(transform: Transform2): TransformedShape = object : TransformedShape {
        override val original = this@Shape
        override val transform = transform
    }
}

trait BoundedShape : Shape {
    val top: Number
    val right: Number
    val bottom: Number
    val left: Number
    fun scaled(factor: Number): BoundedShape = object : BoundedShape, TransformedShape by this transformed Transforms2.scale(factor) {
        override val top: Number = this@BoundedShape.top.toDouble() * factor.toDouble()
        override val right: Number = this@BoundedShape.right.toDouble() * factor.toDouble()
        override val bottom: Number = this@BoundedShape.bottom.toDouble() * factor.toDouble()
        override val left: Number = this@BoundedShape.left.toDouble() * factor.toDouble()
    }

    fun width(): Number = right.toDouble () - left.toDouble()
    fun height(): Number = top.toDouble () - bottom.toDouble()
    fun size() = vector(width(), height())
    fun center() = vector((right.toDouble() + left.toDouble())/2, (top.toDouble() + bottom.toDouble())/2)
    fun bounds() = rectangle(size()) transformed Transforms2.translation(center())
}

fun shape(contains: (Vector2) -> Boolean) = object : Shape {
    override fun contains(location: Vector2) = contains(location)
}

fun concatenatedShape(shapes: Iterable<Shape>) = shape({ location -> shapes.any({ it.contains(location) }) })

trait TransformedShape : Shape {
    val original: Shape
    val transform: Transform2
    override fun contains(location: Vector2) = original.contains(transform.inverse()(location))
}