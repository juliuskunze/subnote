package com.mindforge.graphics.math

import com.mindforge.graphics.*

interface Shape {
    fun contains(location: Vector2): Boolean
    fun transformed(transform: Transform2): TransformedShape = object : TransformedShape {
        override val original = this@Shape
        override val transform = transform
    }
}

fun shape(contains: (Vector2) -> Boolean) = object : Shape {
    override fun contains(location: Vector2) = contains(location)
}

fun concatenatedShape(shapes: Iterable<Shape>) = shape({ location -> shapes.any({ it.contains(location) }) })

interface TransformedShape : Shape {
    val original: Shape
    val transform: Transform2
    override fun contains(location: Vector2) = original.contains(transform.inverse()(location))
}