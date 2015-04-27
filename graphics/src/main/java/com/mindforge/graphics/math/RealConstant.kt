package com.mindforge.graphics.math

import com.mindforge.graphics.*

public trait RealConstant : Symbol, RealPrimitive {
    public companion object {
        fun invoke(n: String, u: String? = null, fn: (() -> Real)): RealConstant = object : RealConstant, Calculatable() {
            override val name: String = n
            override val unit: String? = u
            override val calculation_fn = fn
        }
    }

    override val value: InternalReal get() = approximate()

    override fun toString(): String = super<Symbol>.toString()

    override fun equals(other: Any?): Boolean = super<Symbol>.equals(other) || (other is RealConstant && value == other.value)

    override fun approximate(): InternalReal {
        return calculation_fn().approximate()
    }

    protected val calculation_fn: (() -> Real)

    override val isApproximate: Boolean get() = calculation_fn().isApproximate

}

val realConstant = RealConstant
