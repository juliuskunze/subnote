package com.mindforge.graphics.math

public abstract class Calculatable : Number(), Comparable<Any?> {
    // INHERITED STUFF

    final override fun compareTo(other: Any?): Int = compareTo(other, false)

    final fun compareTo(other: Any?, inner: Boolean): Int {
        if (other == null) throw IllegalArgumentException()
        val it = other.asCalculatable()
        try {
            return tryCompareTo(it)
        } catch (e: IllegalArgumentException) {
            try {
                return -it.tryCompareTo(this)
            } catch (e: IllegalArgumentException) {
                if (!inner) return activeEnvironment.intReal(this).compareTo(activeEnvironment.intReal(other), true)
                throw e
            }
        }
    }

    override fun equals(other: Any?): Boolean = compareTo(other) == 0

    override abstract fun toDouble(): Double
    override fun toFloat(): Float = toDouble().toFloat()
    override abstract fun toLong(): Long
    override fun toInt(): Int = toLong().toInt()
    override fun toShort(): Short = toLong().toShort()
    override fun toByte(): Byte = toLong().toByte()
    override fun toChar(): Char = toLong().toChar()

    // WHAT IS IMPORTANT
    abstract fun tryCompareTo(other: Calculatable): Int

    open fun plus(): Calculatable = this

    abstract fun minus(): Calculatable

    open fun plus(other: Any?): Calculatable = plus(other, false)
    // NOTE: plus is open so inherited classes can define its own return type: recommended implementations
    // - return plus(other, false) as T
    // - return super<Calculatable>.plus(other) as T
    // same goes with minus, times etc
    // But be careful, that the result may not always be of that type
    final fun plus(other: Any?, inner: Boolean): Calculatable {
        if (other == null) throw IllegalArgumentException()
        val it = other.asCalculatable()
        try {
            return tryPlus(it)
        } catch (e: IllegalArgumentException) {
            try {
                return it.tryPlus(this)
            } catch (e: IllegalArgumentException) {
                if (!inner) return activeEnvironment.intReal(this).plus(activeEnvironment.intReal(other), true)
                throw e
            }
        }
    }

    open fun minus(other: Any?): Calculatable = minus(other, false)

    final fun minus(other: Any?, inner: Boolean): Calculatable {
        if (other == null) throw IllegalArgumentException()
        val it = other.asCalculatable()
        try {
            return tryMinus(it)
        } catch (e: IllegalArgumentException) {
            try {
                return -it.tryPlus(this)
            } catch (e: IllegalArgumentException) {
                if (!inner) return activeEnvironment.intReal(this).minus(activeEnvironment.intReal(other), true)
                throw e
            }
        }
    }

    open fun times(other: Any?): Calculatable = times(other, false)

    final fun times(other: Any?, inner: Boolean): Calculatable {
        if (other == null) throw IllegalArgumentException()
        val it = other.asCalculatable()
        try {
            return tryTimes(it)
        } catch (e: IllegalArgumentException) {
            try {
                return it.tryTimes(this)
            } catch (e: IllegalArgumentException) {
                if (!inner) return activeEnvironment.intReal(this).times(activeEnvironment.intReal(other), true)
                throw e
            }
        }
    }

    open fun div(other: Any?): Calculatable = div(other, false)

    final fun div(other: Any?, inner: Boolean): Calculatable {
        if (other == null) throw IllegalArgumentException()
        val it = other.asCalculatable()
        try {
            return tryDiv(it)
        } catch (e: IllegalArgumentException) {
            if (!inner) return activeEnvironment.intReal(this).plus(activeEnvironment.intReal(other), true)
            return activeEnvironment.intReal(1).tryDiv(it).tryTimes(this)
        }
    }

    open fun mod(other: Any?): Calculatable = mod(other, false)

    final fun mod(other: Any?, inner: Boolean): Calculatable {
        if (other == null) throw IllegalArgumentException()
        val it = other.asCalculatable()
        try {
            return tryMod(it)
        } catch (e: IllegalArgumentException) {
            if (!inner) return activeEnvironment.intReal(this).plus(activeEnvironment.intReal(other), true)
            throw e
        }
    }

    open fun divideAndRemainder(other: Any?): Array<Calculatable> = array(this / other, this % other)
    // may be optimized by inherited classes

    abstract fun tryPlus(other: Any?): Calculatable
    abstract fun tryMinus(other: Any?): Calculatable
    abstract fun tryTimes(other: Any?): Calculatable
    abstract fun tryDiv(other: Any?): Calculatable
    abstract fun tryMod(other: Any?): Calculatable

    abstract fun floor(): Calculatable
    abstract fun ceil(): Calculatable
    abstract fun round(): Calculatable

    abstract fun abs(): Calculatable

    final val shell: MathShell
    init {
        shell = activeShell
    }
    final val env: MathEnvironment get() = shell.environment
}

fun Any.asCalculatable(): Calculatable = if (this is Calculatable) this; else activeEnvironment.intReal(this)

fun gcd(a: Number, b: Number): Calculatable {
    var a = a.asCalculatable().abs()
    var b = b.asCalculatable().abs()
    var c: Calculatable
    if (a equals 0) return b
    if (b equals 0) return a
    if (a > b) {
        c = a
        a = b
        b = c
    }
    var i = 0
    while (a > 0 && a != b && i < 2 * activeEnvironment.accuracy) {
        c = b % a
        b = a
        a = c
        i++
    }
    if (i >= 2 * activeEnvironment.accuracy) return 0.asCalculatable()
    return b
}

fun lcm(a: Number, b: Number): Calculatable {
    val a = a.asCalculatable().abs()
    val b = b.asCalculatable().abs()
    val g = gcd(a, b)
    if (g equals 0) return Infinity
    return a * b / g
}

fun Number.plus(other: Calculatable): Calculatable = other + this
fun Number.minus(other: Calculatable): Calculatable = -other + this
fun Number.times(other: Calculatable): Calculatable = other * this
fun Number.div(other: Calculatable): Calculatable = this.asCalculatable() / other
fun Number.mod(other: Calculatable): Calculatable = this.asCalculatable() % other
fun Number.divideAndRemainder(other: Calculatable): Array<Calculatable> = array(this / other, this % other)