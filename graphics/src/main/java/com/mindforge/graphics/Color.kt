package com.mindforge.graphics

interface Color {
    val r: Number
    val g: Number
    val b: Number
    val a: Number
    override fun equals(other: Any?) = if (other is Color) (r.toDouble() == other.r.toDouble() && g.toDouble() == other.g.toDouble() && b.toDouble() == other.b.toDouble() && a.toDouble() == other.a.toDouble()) else false

    fun times(factor: Number): Color {
        val f = factor.toDouble()
        return color(r = r.toDouble() * f, g = g.toDouble() * f, b = b.toDouble() * f, a = a.toDouble() * f)
    }

    fun plus(other: Color) = color(r = r.toDouble() + other.r.toDouble(), g = g.toDouble() + other.g.toDouble(), b = b.toDouble() + other.b.toDouble(), a = a.toDouble() + other.a.toDouble())

    override fun toString() = "color(r=${r.toDouble()}, g=${g.toDouble()}, b=${b.toDouble()}, a=${a.toDouble()})"
}

fun color(r: Number = 0, g: Number = 0, b: Number = 0, a: Number = 1): Color = object : Color {
    override val r = r
    override val g = g
    override val b = b
    override val a = a
}

object Colors {
    val black = color()
    val red = color(r = 1)
    val green = color(g = 1)
    val blue = color(b = 1)
    fun gray(brightness: Number) = color(brightness, brightness, brightness)
    val gray = gray(.5)
    val white = color(1, 1, 1)
    val transparent = color(a = 0)
}