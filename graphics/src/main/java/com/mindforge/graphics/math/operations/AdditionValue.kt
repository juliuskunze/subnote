package com.mindforge.graphics.math.operations

import com.mindforge.graphics.math.*
import com.mindforge.graphics.Constructor2

public trait AdditionValue : RealBinaryOperation {

    public companion object : Constructor2<AdditionValue, Real, Real> {
        override fun invoke(a: Real, b: Real): AdditionValue = object : AdditionValue, Calculatable() {
            override val subReals: Array<Real> = array(a, b)
        }
    }

    private val constructor: Constructor2<AdditionValue, Real, Real> get() = AdditionValue

    final override val priority: Int
        get() = 0

    override val description: String
        get() = "Addition"

    final override val operationSign: String
        get() = "+"

    final override val isOrderDependent: Boolean
        get() = false


    override fun approximate(): InternalReal {
        return value1.approximate() tryPlus value2.approximate()
    }


    override fun calculate(): Real {
        val s1: Real = value1.calculate()
        val s2: Real = value2.calculate()
        if (s1 is RealPrimitive && s2 is RealPrimitive) return real(s1.value + s2.value)

        // return this if no simplification is possible
        return this
    }

    override fun minus(): AdditionValue = AdditionValue.invoke(-value1, -value2)

    override val isPositive: Boolean get() = value1.isPositive || value2.isPositive
}