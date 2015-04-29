package com.mindforge.graphics

trait Vector3 : Iterable<Number> {
    fun plus(other: Vector3): Vector3 = vector(x.toDouble() + other.x.toDouble(), y.toDouble() + other.y.toDouble(), z.toDouble() + other.z.toDouble())
    fun times(other: Number): Vector3 {
        val s = other.toDouble()
        return vector(x.toDouble() * s, y.toDouble() * s, z.toDouble() * s)
    }

    fun plus() = this
    fun minus() = this * -1
    fun minus(other: Vector3) = this + (-other)
    fun div(other: Number) = this * (1 / other.toDouble())
    fun get(i: Int): Number = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> {
            throw IllegalArgumentException()
        }
    }
    val x: Number
    val y: Number
    val z: Number

    val lengthSquared: Number get() {
        val x = x.toDouble()
        val y = y.toDouble()
        val z = z.toDouble()
        return x * x + y * y + z * z
    }
    val length: Number get() = Math.sqrt(lengthSquared.toDouble())

    fun dotProduct(other: Vector3): Number = x.toDouble() * other.x.toDouble() + y.toDouble() * other.y.toDouble() + z.toDouble() * other.z.toDouble()

    override fun iterator() = listOf(x, y, z).iterator()

    override fun equals(other: Any?) = other is Vector3 && (x.toDouble() == other.x.toDouble() && y.toDouble() == other.y.toDouble() && z.toDouble() == other.z.toDouble())

    override fun toString() = "vector(${x.toDouble()}, ${y.toDouble()}, ${z.toDouble()})"

    fun times(other: Vector3) = this.dotProduct(other)

    fun crossProduct(other: Vector3): Vector3 = vector(
            y.toDouble() * other.z.toDouble() - z.toDouble() * other.y.toDouble(),
            z.toDouble() * other.x.toDouble() - x.toDouble() * other.z.toDouble(),
            x.toDouble() * other.y.toDouble() - y.toDouble() * other.x.toDouble())
}

fun vector(x: Number, y: Number, z: Number) = object : Vector3 {
    override val x = x
    override val y = y
    override val z = z
}

fun vector3(get: (Int) -> Number) = vector(get(0), get(1), get(2))

val zeroVector3 = vector(0, 0, 0)