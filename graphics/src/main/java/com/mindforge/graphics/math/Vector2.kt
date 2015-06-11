package com.mindforge.graphics

trait Vector2 : Iterable<Number> {
    fun plus(other: Vector2): Vector2 = vector(x.toDouble() + other.x.toDouble(), y.toDouble() + other.y.toDouble())
    fun times(other: Number): Vector2 {
        val s = other.toDouble()
        return vector(x.toDouble() * s, y.toDouble() * s)
    }

    fun plus() = this
    fun minus() = this * -1
    fun minus(other: Vector2) = this + (-other)
    fun div(other: Number) = this * (1 / other.toDouble())
    fun get(i: Int): Number = when (i) {
        0 -> x
        1 -> y
        else -> {
            throw IllegalArgumentException()
        }
    }

    val x: Number
    val y: Number

    val lengthSquared: Number get() {
        val x = x.toDouble()
        val y = y.toDouble()
        return x * x + y * y
    }
    val length: Number get() = Math.sqrt(lengthSquared.toDouble())
    val argument: Number get() = Math.atan2(y.toDouble(), x.toDouble())

    fun times(other: Vector2): Number = x.toDouble() * other.x.toDouble() + y.toDouble() * other.y.toDouble()

    override fun iterator() = listOf(x, y).iterator()

    override fun equals(other: Any?) = other is Vector2 && (x.toDouble() == other.x.toDouble() && y.toDouble() == other.y.toDouble())

    override fun toString() = "vector(${x.toDouble()}, ${y.toDouble()})"

    fun xComponent(): Vector2 = vector(x, 0)
    fun yComponent(): Vector2 = vector(0, y)

    fun mirrorX() = yComponent() - xComponent()
    fun mirrorY() = xComponent() - yComponent()
}

fun vector(x: Number, y: Number) = object : Vector2 {
    override val x = x
    override val y = y
}

fun vector2(get: (Int) -> Number) = vector(get(0), get(1))

val zeroVector2 = vector(0, 0)