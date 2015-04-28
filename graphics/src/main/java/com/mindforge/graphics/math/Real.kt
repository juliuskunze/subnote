package com.mindforge.graphics.math

import com.mindforge.graphics.Constructor1
import com.mindforge.graphics.Constructor2
import sun.reflect.generics.reflectiveObjects.NotImplementedException

public trait Real : Calculatable {

    public companion object : Constructor1<Real, Any?>, Constructor2<Real, Any?, Boolean> {
        override fun invoke(it: Any?): Real = invoke(it, false)
        override fun invoke(v: Any?, isApprox: Boolean): Real {
            when (v) {
                is Real -> return v
                is InternalReal -> return object : RealPrimitive, Calculatable() {
                    override val value: InternalReal = v
                    override val isApproximate: Boolean = isApprox
                }
                null -> throw IllegalArgumentException("Cannot create a real out of nothing")
                else -> return real(internalReal(v), isApprox)
            }
        }
    }

    val subReals: Array<Real> get() = array()

    fun replaceSubReals(a: Array<Real>): Real = this

    fun filterRecursive(successCond: (Real) -> Boolean): Array<Real> {
        if (successCond(this)) return array(this)
        var a: Array<Real> = array()
        throw NotImplementedException() // TODO FIX for (i in subReals.indices) a += subReals[i].filterRecursive(successCond)
        return a
    }

    fun filterRecursiveCond(continueCond: (Real) -> Boolean, successCond: (Real) -> Boolean): Array<Real> {
        if (!continueCond(this)) return array()
        if (successCond(this)) return array(this)
        var a: Array<Real> = array()
        throw NotImplementedException() // TODO FIX for (i in subReals.indices) a += subReals[i].filterRecursiveCond(continueCond, successCond)
        return a
    }

    val isApproximate: Boolean get() = false

    fun matchWithThisPattern(other: Real): Boolean = this == other // true if other matches the pattern defined by this

    final fun simplify(): Real {
        return env.simplifier.simplify(this)
    }

    fun calculate(): Real {
        return this
    }

    fun approximate(): InternalReal {
        throw UnsupportedOperationException()
    }

    override fun equals(other: Any?): Boolean {
        // TODO: this is bullshit as well (EDIT: Still bullshit, not yet an idea how this should behave)
        when (other) {
            null -> return false
            is Real -> {
                if (this is RealPrimitive && other is RealPrimitive) {
                    return this.approximate() == other.approximate()
                }
                return false
            }
            is Number -> {
                return this.approximate() == other
            }
        }
        return false
    }

    override fun tryCompareTo(other: Calculatable): Int = approximate().compareTo(other)

    override fun plus() = this
    override fun minus(): Real = env.subVal(0.toReal(), this)

    override fun plus(other: Any?) = plus(other, false) as Real
    override fun minus(other: Any?) = minus(other, false) as Real
    override fun times(other: Any?) = times(other, false) as Real
    override fun div(other: Any?) = div(other, false) as Real


    override fun tryPlus(other: Any?): Real = env.addVal(this, real(other))

    override fun tryMinus(other: Any?): Real = env.subVal(this, real(other))

    override fun tryTimes(other: Any?): Real = env.mulVal(this, real(other))

    override fun tryDiv(other: Any?): Real = env.divVal(this, real(other))

    override fun tryMod(other: Any?): Calculatable = approximate() % other

    fun invert(): Real = env.divVal(1.toReal(), this)


    override fun toDouble(): Double = approximate().toDouble()
    override fun toFloat(): Float = approximate().toFloat()
    override fun toLong(): Long = approximate().toLong()
    override fun toInt(): Int = approximate().toInt()
    override fun toShort(): Short = approximate().toShort()
    override fun toByte(): Byte = approximate().toByte()
    override fun toChar(): Char = approximate().toChar()

    override fun floor(): Calculatable = approximate().floor()
    override fun ceil(): Calculatable = approximate().ceil()
    override fun round(): Calculatable = approximate().round()

    // TODO: we want an operator for this, as we want to be able to solve abs equations
    override fun abs(): Calculatable = approximate().abs()

    val isZero: Boolean get() = false
    // Means zero in the way it is written, not considering variables
    val isPositive: Boolean get() = true
    // Means positive in the way it is written, not considering variables (if it CAN be positive)
    final val isNegative: Boolean get() = !isPositive && !isZero
    // Means negative in the way it is written, not considering variables
}

val real = Real

fun Number.toReal(): Real = real(this)