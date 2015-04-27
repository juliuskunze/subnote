package com.mindforge.graphics.math

import com.mindforge.graphics.InvalidateFun

public trait Infinity : InternalReal {
    public companion object : Infinity, Calculatable() {
        public val positiveInfinity: Infinity = Infinity
        override fun toDouble() = java.lang.Double.POSITIVE_INFINITY
        override fun tryCompareTo(other: Calculatable): Int {
            if (other.toDouble() == java.lang.Double.POSITIVE_INFINITY) return 0
            return 1
        }
        override fun minus() = Infinity.negativeInfinity
        override fun toString() = "Infinity"
        override fun signum() = 1

        public val negativeInfinity: Infinity = object : Infinity, Calculatable() {
            override fun toDouble() = java.lang.Double.NEGATIVE_INFINITY
            override fun tryCompareTo(other: Calculatable): Int {
                if (other.toDouble() == java.lang.Double.NEGATIVE_INFINITY) return 0
                return -1
            }
            override fun minus() = Infinity.positiveInfinity
            override fun toString() = "-Infinity"
            override fun signum() = -1
        }
    }
    // Invalidate super functions
    override fun toMathematicalString(): String = toString()

    override fun toDouble(): Double = InvalidateFun()
    override fun toLong(): Long = InvalidateFun()


    override fun tryPlus(other: Any?): InternalReal = if (-this == other) throw IllegalArgumentException() else this
    override fun tryMinus(other: Any?): InternalReal = if (this == other) throw IllegalArgumentException() else this

    override fun tryTimes(other: Any?): InternalReal
            = if (other !is Number || other == 0) throw IllegalArgumentException() else if (other.asCalculatable() >= 0) this else -this

    override fun tryDiv(other: Any?, requireExact: Boolean): InternalReal
            = if (other !is Number || other is Infinity) throw IllegalArgumentException() else if (other.asCalculatable() >= 0) this else -this

    override fun tryMod(other: Any?): InternalReal = InvalidateFun()

    override fun floor(): InternalReal = this
    override fun ceil(): InternalReal = this
    override fun round(): InternalReal = this

    override fun abs(): InternalReal = Infinity

    override fun tryCompareTo(other: Calculatable): Int
    override fun isInteger(): Boolean = false
}

val NegativeInfinity = Infinity.negativeInfinity
