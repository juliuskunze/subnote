package com.mindforge.graphics.math.operations

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*

public trait SubtractionValue : RealBinaryOperation {

    public companion object : Constructor2<SubtractionValue, Real, Real> {
        override fun invoke(a: Real, b: Real): SubtractionValue = object : SubtractionValue, Calculatable() {
            override val subReals: Array<Real> = array(a, b)
        }
    }

    private val constructor: Constructor2<SubtractionValue, Real, Real> get() = SubtractionValue

    final override val priority: Int
        get() = 0

    override val description: String
        get() = "Subtraction"

    final override val operationSign: String
        get() = "-"

    final override val isOrderDependent: Boolean
        get() = true

    override fun approximate(): InternalReal {
        return value1.approximate() tryMinus value2.approximate()
    }

    override fun calculate(): Real {
        val s1: Real = value1.calculate()
        val s2: Real = value2.calculate()
        if (s1 is RealPrimitive && s2 is RealPrimitive) return real(s1.value - s2.value)

        // return this if no simplification is possible
        return this
    }

    override fun minus(): SubtractionValue = SubtractionValue.invoke(-value1, -value2)

    override val isPositive: Boolean get() = value1.isPositive || !value2.isPositive
}

