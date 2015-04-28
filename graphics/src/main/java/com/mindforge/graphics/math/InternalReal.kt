package com.mindforge.graphics.math

import com.mindforge.graphics.*

public trait InternalReal : Calculatable {
    public companion object : Constructor1<InternalReal, Any?> {
        override fun invoke(it: Any?): InternalReal {
            when (it) {
                null -> throw IllegalArgumentException()
                is InternalReal -> return it
                is String -> {
                    // Infinity check should be done by the intReal object, if others implement one with that functionality
                    return activeEnvironment.intReal(it)
                }
                else -> return invoke(it.toString())
            }
        }
    }

    override fun toDouble(): Double
    override fun toFloat(): Float = toDouble().toFloat()
    override fun toLong(): Long
    override fun toInt(): Int = toLong().toInt()
    override fun toShort(): Short = toLong().toShort()
    override fun toByte(): Byte = toLong().toByte()
    override fun toChar(): Char = toLong().toChar()

    override fun toString(): String

    fun toMathematicalString(): String // TODO: i would like the environment to do the conversion, using a xxxRealConverter trait

    override fun equals(other: Any?): Boolean {
        try {
            return compareTo(other) == 0
        } catch (e: IllegalArgumentException) {
            return false
        }
    }

    override fun tryPlus(other: Any?): InternalReal
    override fun tryMinus(other: Any?): InternalReal
    override fun tryTimes(other: Any?): InternalReal

    fun tryDiv(other: Any?, requireExact: Boolean): InternalReal
    override fun tryDiv(other: Any?): InternalReal = tryDiv(other, env.requireExact)

    override fun tryMod(other: Any?): InternalReal
    /// div with requireExact is to be overridden
    /// they throw RuntimeException for other == 0

    override fun minus(): InternalReal
    override fun plus() = this


    override fun floor(): InternalReal
    override fun ceil(): InternalReal
    override fun round(): InternalReal
    override fun abs(): InternalReal

    fun signum(): Int

    val sign: Boolean get() = signum() < 0

    fun isInteger(): Boolean

}

val internalReal = InternalReal