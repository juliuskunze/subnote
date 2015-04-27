package com.mindforge.graphics.math.sets

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*

public trait IntegerSet : MultipleOfSet {
    public companion object : IntegerSet, Constructor2<IntegerSet, Number, Number> {
        override val lowEnd: Calculatable = -Infinity
        override val highEnd: Calculatable = Infinity
        override val lowClosed: Boolean = false
        override val highClosed: Boolean = false

        public val Full: IntegerSet = IntegerSet
        public val PositiveAndZero: RealSet = invoke(0, Infinity)
        public val Positive: RealSet = invoke(1, Infinity)
        public val NegativeAndZero: RealSet = invoke(-Infinity, 0)
        public val Negative: RealSet = invoke(-Infinity, 1)

        override fun invoke(lEnd: Number, hEnd: Number): IntegerSet {
            var le = lEnd.asCalculatable()
            var he = hEnd.asCalculatable()
            if (le > he) {
                val tmp = le
                le = he
                he = tmp
            }
            return object : IntegerSet {
                override val lowEnd: Calculatable = le
                override val highEnd: Calculatable = he
            }
        }
    }

    override fun toString(): String = "integerSet(${lowEnd},${highEnd})"

    override fun contains(other: com.mindforge.graphics.math.Set): Boolean {
        // TODO
        return false
    }

    override fun hasCommonElementsWith(other: com.mindforge.graphics.math.Set): Boolean {
        // TODO
        return false
    }

    final override val factor: Calculatable get() = activeEnvironment.intReal(1)
    final override val offset: Calculatable get() = activeEnvironment.intReal(0)
}

val integerSet = IntegerSet