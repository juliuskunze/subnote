package com.mindforge.graphics.math

import java.math.BigInteger
import java.lang.Math.*
import kotlin.math.*
import com.mindforge.graphics.*


public trait BasicReal : InternalReal {
    public companion object : Constructor1<InternalReal, Any?>, Constructor2<BasicReal, BigInteger, Long> {
        fun getLowestExponent(o1: BasicReal, o2: BasicReal): Long = min(o1.exponent, o2.exponent)
        fun exponentialFactor(exp: Long): BigInteger = BigInteger.TEN.pow(abs(exp.toInt()))

        override fun invoke(it: Any?): BasicReal {
            return when (it) {
                is BasicReal -> it.minimize()
                is String -> BasicReal.fromString(it.extractInnerString().removeWhitespace().toUpperCase())
                is Long -> basicReal(BigInteger(it), 0).minimize()
                is Int -> basicReal(it.toLong())
                is Short -> basicReal(it.toLong())
                is Byte -> basicReal(it.toLong())
                is BigInteger -> basicReal(it, 0).minimize()
                is RealPrimitive -> basicReal(it.value)
                is Number -> basicReal(it.toString())
                else -> throw IllegalArgumentException("Cannot create a BasicReal of given '{$it}'")
            }
        }

        override fun invoke(num: BigInteger, exp: Long): BasicReal = object : BasicReal, Calculatable() {
            override val number: BigInteger = num
            override val exponent: Long = exp
        }

        fun fromString(s: String): BasicReal {
            var str: String = s
            var estr: String

            // with regex - remove illegal characters and whitespace
            if (str.matches("[^0-9\\.\\-\\+E\\s]")) throw IllegalArgumentException("There are forbidden characters in the expression")

            // get sign of number
            var sgnstr = ""
            if (str.first() == '-') {
                sgnstr = "-"
                str = str.substring(1)
            }


            // look if we have exponent
            var epos: Int = str.indexOf("E")
            var exp: Long = 0
            if (epos != -1) {
                estr = str.substring(epos + 1)
                exp = estr.toLong()
                str = str.substring(0, epos)
            }
            // now remove the "dot of the number"
            epos = str.indexOf(".")
            if (epos != -1) {
                exp -= (str.length - epos - 1) // reduce by number of characters after '.'
                str = str.replaceAll("[\\.]", "") // remove dot
            }
            // remove leading zeros
            while (str.first() == '0' && str.length > 1) {
                str = str.substring(1)
            }
            // remove tailing zeros
            while (str.last() == '0' && str.length > 1) {
                str = str.substring(0, str.length - 1)
                exp++
            }
            return basicReal(BigInteger (sgnstr + str), exp)
        }
    }

    /*********** CONVERSIONS **************/
    final override fun toDouble(): Double = number.doubleValue() * doubleExponentialFactor()

    final override fun toFloat(): Float = number.floatValue() * doubleExponentialFactor().toFloat()

    final override fun toLong(): Long = number.longValue() * doubleExponentialFactor().toLong()

    final override fun toInt(): Int = toLong().toInt()
    final override fun toShort(): Short = toLong().toShort()
    final override fun toByte(): Byte = toLong().toByte()
    final override fun toChar(): Char = toLong().toChar()

    override fun toString(): String {
        return "BasicReal(\"${toMathematicalString()}\")"
    }

    override fun toMathematicalString(): String {
        if (number == BigInteger.ZERO) return "0"
        var s: String = number.toString()
        val s_begin = when {
            s.first() == '-' -> 1
            else -> 0
        }
        if (exponent + s.length in -2..7) {
            if (exponent >= 0) return (number * exponentialFactor()).toString()
            var dotpos: Int = s.length + exponent.toInt()
            while (dotpos < s_begin) {
                dotpos++
                s = s.substring(0, s_begin) + "0" + s.substring(s_begin, s.length)
            }
            return s.substring(0, dotpos) + "." + s.substring(dotpos, s.length)
        }
        var dotpos = 1 + s_begin
        var exp = exponent + s.length - dotpos
        s = when {
            dotpos != s.length -> s.substring(0, dotpos) + "." + s.substring(dotpos, s.length)
            else -> s
        }
        val esgn: String = when {
            exp >= 0 -> "+"
            else -> "-"
        }
        return s + "E" + esgn + abs(exp).toString()
    }

    final fun toBigInteger(): BigInteger {
        return when {
            exponent > 0 -> number * exponentialFactor()
            else -> number / exponentialFactor()
        }
    }

    /*********** COMPARATOR FUNCTIONS ***********/
    open fun compareExponentTo(other: BasicReal): Long {
        return this.exponent - other.exponent
    }

    override fun tryCompareTo(other: Calculatable): Int {
        when (other) {
            is BasicReal -> {
                if (this.signum() == 0) return -other.signum()
                if (this.signum() != other.signum()) {
                    return this.signum() - other.signum()
                }
                return (this tryMinus other).number.compareTo(BigInteger.ZERO)
            }
            else -> throw IllegalArgumentException()
        }
    }


    fun exponentialFactor(): BigInteger = BasicReal.exponentialFactor(exponent)
    fun doubleExponentialFactor(): Double = pow(10.0, exponent.toDouble())


    /********* BASIC OPERATIONS *********/
    override fun tryPlus(other: Any?): BasicReal {
        when (other) {
            is Byte -> return this tryPlus basicInt(other)
            is Short -> return this tryPlus basicInt(other)
            is Int -> return this tryPlus basicInt(other)
            is Long -> return this tryPlus basicInt(other)
            is Double -> return this tryPlus basicReal(other)
            is Float -> return this tryPlus basicReal(other)
            is BasicReal -> {
                val minexp = getLowestExponent(this, other)
                val br1 = this.toExponent(minexp)
                val br2 = other.toExponent(minexp)
                return basicReal(br1.number + br2.number, minexp).minimize()
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun tryMinus(other: Any?): BasicReal {
        when (other) {
            is Byte -> return this tryMinus basicInt(other)
            is Short -> return this tryMinus basicInt(other)
            is Int -> return this tryMinus basicInt(other)
            is Long -> return this tryMinus basicInt(other)
            is Double -> return this tryMinus basicReal(other)
            is Float -> return this tryMinus basicReal(other)
            is BasicReal -> {
                val minexp = getLowestExponent(this, other)
                val br1 = this.toExponent(minexp)
                val br2 = other.toExponent(minexp)
                return basicReal(br1.number - br2.number, minexp).minimize()
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun tryTimes(other: Any?): BasicReal {
        when (other) {
            is Byte -> return this tryTimes basicInt(other)
            is Short -> return this tryTimes basicInt(other)
            is Int -> return this tryTimes basicInt(other)
            is Long -> return this tryTimes basicInt(other)
            is Double -> return this tryTimes basicReal(other)
            is Float -> return this tryTimes basicReal(other)
            is BasicReal -> {
                return basicReal(
                        this.number * other.number,
                        this.exponent + other.exponent
                ).minimize()
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun tryDiv(other: Any?): BasicReal = tryDiv(other, env.requireExact)
    override fun tryDiv(other: Any?, requireExact: Boolean): BasicReal {
        when (other) {
            is Byte -> return this.tryDiv(basicInt(other), requireExact)
            is Short -> return this.tryDiv(basicInt(other), requireExact)
            is Int -> return this.tryDiv(basicInt(other), requireExact)
            is Long -> return this.tryDiv(basicInt(other), requireExact)
            is Double -> return this.tryDiv(basicReal(other), requireExact)
            is Float -> return this.tryDiv(basicReal(other), requireExact)
            is BasicReal -> {
                if (other.number == BigInteger.ZERO) throw ArithmeticException("A Division by Zero is not allowed")
                val targetExp = exponent - other.exponent
                val br1 = toExponent(exponent - env.accuracy)
                val c = br1.number.divideAndRemainder(other.number)
                if (requireExact && c[1] != BigInteger.ZERO)
                    throw RuntimeException("Accurate Division is not possible")
                return basicReal(c[0], targetExp - env.accuracy).minimize()
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun tryMod(other: Any?): BasicReal {
        when (other) {
            is Byte -> return this tryMod basicInt(other)
            is Short -> return this tryMod basicInt(other)
            is Int -> return this tryMod basicInt(other)
            is Long -> return this tryMod basicInt(other)
            is Double -> return this tryMod basicReal(other)
            is Float -> return this tryMod basicReal(other)
            is BasicReal -> {
                val minexp = getLowestExponent(this, other)
                val br1 = this.toExponent(minexp)
                val br2 = other.toExponent(minexp)
                return basicReal(br1.number mod br2.number, minexp)
            }
            else -> throw IllegalArgumentException()
        }
    }

    open fun minimize(): BasicReal {
        if (this.number == BigInteger.ZERO) return basicReal(this.number, 0)
        var stepSize: Int = env.accuracy / 4
        var powers: Long = 0
        var tmp: BigInteger = this.number
        var done: Boolean = false
        do {
            var p = BigInteger.TEN.pow(stepSize)
            var c = array(tmp, BigInteger.ZERO)
            while (c[1] == BigInteger.ZERO) {
                tmp = c[0]
                powers += stepSize
                c = tmp.divideAndRemainder(p)
            }
            powers -= stepSize
            if (stepSize == 1) done = true
            else {
                stepSize /= 2
            }
        } while (!done)
        return basicReal(tmp, exponent + powers)
    }

    /**
     * This is real magic:
     *
     * number stores a big Integer ...
     * and exponent is the 10-exponent - is Long as we do not expect exponents near 2^63
     */
    val number: BigInteger
    val exponent: Long

    override fun minus(): BasicReal = basicReal(-number, exponent)

    override fun equals(other: Any?): Boolean {
        when (other) {
            null -> return false
            is Number -> return compareTo(other) == 0
            is BasicReal -> return compareTo(other) == 0
            else -> return false
        }
    }

    override fun isInteger(): Boolean = exponent >= 0

    final override fun signum(): Int = number.signum()

    final fun getLowestExponent(o1: BasicReal, o2: BasicReal) = BasicReal.getLowestExponent(o1, o2)

    final fun toExponent(exp: Long): BasicReal {
        return when {
            exp == exponent -> this
            exp > exponent -> basicReal(number / exponentialFactor(exp - exponent), exp)
            else -> basicReal(number * exponentialFactor(exp - exponent), exp)
        }
    }

    override fun floor(): BasicReal = if (isInteger()) this; else {
        if (sign) (toExponent(0) tryMinus 1).minimize(); else toExponent(0).minimize()
    }
    override fun ceil(): BasicReal = if (isInteger()) this; else {
        if (sign) toExponent(0).minimize(); else (toExponent(0) tryPlus 1).minimize()
    }
    override fun round(): BasicReal = if (isInteger()) this; else {
        if (this % basicInt(1) < basicReal(BigInteger(5), -1L)) floor(); else ceil()
    }

    override fun abs(): BasicReal = if (sign) -this; else this
}

val basicReal = BasicReal

val basicRealInf = object : Constructor1<InternalReal, Any?> {
    override fun invoke(it: Any?): InternalReal {
        when (it) {
            is String -> {
                val str = it.extractInnerString().removeWhitespace().toUpperCase()
                if ("INFINITY" in str)
                    return if ("-" in str || "NEGATIVE" in str) NegativeInfinity else Infinity
                return BasicReal.fromString(str)
            }
            is Long -> return basicReal(BigInteger(it), 0).minimize()
            is Int -> return basicReal(it.toLong())
            is Short -> return basicReal(it.toLong())
            is Byte -> return basicReal(it.toLong())
            is Number -> return invoke(it.toString())
            else -> throw IllegalArgumentException("Cannot create a BasicReal of given '{$it}'")
        }
    }
}
