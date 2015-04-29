package com.mindforge.graphics

trait Matrix2 : Iterable<Number> {
    val a: Number
    val b: Number
    val c: Number
    val d: Number

    fun times(other: Matrix2) = matrix2 { x, y -> other.column(y) * row(x) }
    fun times(other: Vector2) = vector2 { other * row(it) }
    fun times(other: Number) = matrix2 { x, y -> other.toDouble() * get(x, y).toDouble() }
    fun div(other: Number) = times(1.0 / other.toDouble())

    fun get(x: Int, y: Int): Number = when (y) {
        0 -> when (x) {0 -> a; 1 -> b; else -> throw IllegalArgumentException()
        }
        1 -> when (x) {0 -> c; 1 -> d; else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException()
    }

    val determinant: Number get() = a.toDouble() * d.toDouble() - b.toDouble() * c.toDouble()
    fun inverse(): Matrix2 = matrix(d.toDouble(), -b.toDouble(), -c.toDouble(), a.toDouble()) times (1 / determinant.toDouble())

    fun row(y: Int) = vector(get(0, y), get(1, y))
    fun column(x: Int) = vector(get(x, 0), get(x, 1))

    override fun iterator() = listOf(a, b, c, d).iterator()

    override fun equals(other: Any?) = other is Matrix2 && (a == other.a && b == b && c == c && d == other.d)

    override fun toString() = "matrix(${a.toDouble()}, ${b.toDouble()}, ${c.toDouble()}, ${d.toDouble()})"
}

fun matrix(a: Number, b: Number, c: Number, d: Number) = object : Matrix2 {
    override val a = a;
    override val b = b
    override val c = c;
    override val d = d
}

fun matrix2(get: (Int, Int) -> Number): Matrix2 = matrix(
        get(0, 0), get(1, 0),
        get(0, 1), get(1, 1))

val identityMatrix2 = matrix(1, 0, 0, 1)