package com.mindforge.graphics.math

import java.math.BigInteger
import kotlin.math.*
import java.lang.Math.*
import com.mindforge.graphics.Constructor1

public trait BasicInt : BasicReal {
    public companion object : Constructor1<BasicInt, Any?> {
        override fun invoke(it: Any?): BasicInt {
            return when (it) {
                is BasicInt -> basicInt(it.number)
                is BasicReal -> {
                    if (!it.isInteger()) throw IllegalArgumentException("The given BasicReal is not integer");
                    else basicInt(it.number * it.exponentialFactor())
                }
                is String -> basicInt(BigInteger(it))
                is Long -> basicInt(BigInteger(it))
                is Int -> basicInt(BigInteger(it))
                is Short -> basicInt(BigInteger(it))
                is Byte -> basicInt(BigInteger(it))
                is BigInteger -> object : BasicInt, Calculatable() {
                    override val number: BigInteger = it
                }
                else -> throw IllegalArgumentException("Cannot create a BasicInt of given '{$it}'")
            }
        }
    }

    fun toBasicReal(): BasicReal = basicReal(this) as BasicReal

    override fun minus(): BasicInt = basicInt(-number)

    final override fun isInteger(): Boolean = true

    final override val exponent: Long get() = 0


    final override fun exponentialFactor(): BigInteger = BigInteger.ONE
    final override fun doubleExponentialFactor(): Double = 1.0
}

val basicInt = BasicInt

fun BigInteger(b: Byte): BigInteger = BigInteger(b.toString())
fun BigInteger(s: Short): BigInteger = BigInteger(s.toString())
fun BigInteger(i: Int): BigInteger = BigInteger(i.toString())
fun BigInteger(l: Long): BigInteger = BigInteger(l.toString())

fun BigInteger(f: Float): BigInteger = BigInteger(f.toString())
fun BigInteger(d: Double): BigInteger = BigInteger(d.toString())


