package com.mindforge.graphics

trait Matrix3 : Iterable<Number> {
    val a: Number
    val b: Number
    val c: Number
    val d: Number
    val e: Number
    val f: Number
    val g: Number
    val h: Number
    val i: Number

    fun times(other: Matrix3): Matrix3 = matrix3 { x, y -> other.column(y) * row(x) }
    fun times(other: Vector3) = vector3 { index -> other * row(index) }
    fun times(other: Number): Matrix3 = matrix3 { x, y -> other.toDouble() * get(x, y).toDouble() }
    fun div(other: Number) = times(1.0 / other.toDouble())

    fun get(x: Int, y: Int): Number = when (x) {
        0 -> when (y) {0 -> a; 1 -> b; 2 -> c else -> throw IllegalArgumentException()
        }
        1 -> when (y) {0 -> d; 1 -> e; 2 -> f else -> throw IllegalArgumentException()
        }
        2 -> when (y) {0 -> g; 1 -> h; 2 -> i else -> throw IllegalArgumentException()
        }
        else -> throw IllegalArgumentException()
    }

    val determinant: Number get() =
    (a.toDouble() * e.toDouble() * i.toDouble() + b.toDouble() * f.toDouble() * g.toDouble() + c.toDouble() * d.toDouble() * h.toDouble()) -
            (c.toDouble() * e.toDouble() * g.toDouble() + a.toDouble() * f.toDouble() * h.toDouble() + b.toDouble() * d.toDouble() * i.toDouble())

    val isInvertible: Boolean get() = determinant != 0.0

    fun transpose(): Matrix3 = matrix3 { x, y -> this[y, x] }
    fun inverse() = if (isInvertible) adjugate() / determinant else null
    fun adjugate(): Matrix3 = matrix3 { x, y -> subMatrix(y, x).determinant.toDouble() * if ((x + y) mod 2 == 0) 1 else -1 }

    fun row(x: Int) = vector(get(x, 0), get(x, 1), get(x, 2))
    fun column(y: Int) = vector(get(0, y), get(1, y), get(2, y))

    fun subMatrix(exceptX: Int, exceptY: Int) = matrix2 { x, y -> this[(0..2).filter { it != exceptX }[x], (0..2).filter { it != exceptY }[y]] }

    override fun iterator() = listOf(a, b, c, d, e, f, g, h, i).iterator()

    override fun equals(other: Any?) = other is Matrix3 && (a.toDouble() == other.a.toDouble() && b.toDouble() == other.b.toDouble() && c.toDouble() == other.c.toDouble() && d.toDouble() == other.d.toDouble() && e.toDouble() == other.e.toDouble() && f.toDouble() == other.f.toDouble() && g.toDouble() == other.g.toDouble() && h.toDouble() == other.h.toDouble() && i.toDouble() == other.i.toDouble())

    override fun toString() = "matrix(${a.toDouble()}, ${b.toDouble()}, ${c.toDouble()}, ${d.toDouble()}, ${e.toDouble()}, ${f.toDouble()}, ${g.toDouble()}, ${h.toDouble()}, ${i.toDouble()})"
}

fun matrix(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number, h: Number, i: Number) = object : Matrix3 {
    override val a = a
    override val b = b
    override val c = c
    override val d = d
    override val e = e
    override val f = f
    override val g = g
    override val h = h
    override val i = i
}

fun matrix3(get: (Int, Int) -> Number) = matrix(
        get(0, 0), get(0, 1), get(0, 2),
        get(1, 0), get(1, 1), get(1, 2),
        get(2, 0), get(2, 1), get(2, 2))

val identityMatrix3 = matrix(1, 0, 0, 0, 1, 0, 0, 0, 1)