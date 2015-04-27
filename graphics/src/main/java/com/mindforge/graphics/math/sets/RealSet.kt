package com.mindforge.graphics.math.sets

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*

public trait RealSet : com.mindforge.graphics.math.Set {

    public companion object : RealSet, Constructor4<RealSet, Number, Number, Boolean, Boolean>, Constructor2<RealSet, Number, Number> {

        override fun invoke(le: Number, he: Number) = invoke(le, he, true, true)

        override fun invoke(lEnd: Number, hEnd: Number, lClosed: Boolean, hClosed: Boolean): RealSet {
            var le = lEnd.asCalculatable()
            var he = hEnd.asCalculatable()
            if (le > he) {
                val tmp = le
                le = he
                he = tmp
            }
            assert((-Infinity != le || !lClosed) && (Infinity != he || !hClosed), "A Set cannot be closed in the infinite")
            return object : RealSet {
                override val lowEnd: Calculatable = le
                override val highEnd: Calculatable = he
                override val lowClosed: Boolean = lClosed
                override val highClosed: Boolean = hClosed
            }
        }

        override val lowEnd: Calculatable = -Infinity
        override val highEnd: Calculatable = Infinity
        override val lowClosed: Boolean = false
        override val highClosed: Boolean = false

        public val Full: RealSet = RealSet
        public val PositiveAndZero: RealSet = invoke(0, Infinity, true, false)
        public val Positive: RealSet = invoke(0, Infinity, false, false)
        public val NegativeAndZero: RealSet = invoke(-Infinity, 0, false, true)
        public val Negative: RealSet = invoke(-Infinity, 0, false, false)

    }
    val lowEnd: Calculatable
    val highEnd: Calculatable

    val lowClosed: Boolean
    val highClosed: Boolean

    override fun toString(): String = "realSet(${lowEnd},${highEnd},${lowClosed},${highClosed})"

    override fun contains(other: com.mindforge.graphics.math.Set): Boolean {
        when (other) {
            is SetUnion -> return contains(other.subset1) && contains(other.subset2)
            is SetIntersection -> {
                val u = other.simplifySets()
                if (u is SetIntersection) return contains(other.superset1) || contains(other.superset2)
                return contains(u)
            }
            is RealSet -> {
                return (other.lowEnd in this || (!other.lowClosed && !lowClosed && lowEnd == other.lowEnd))
                        && (other.highEnd in this || (!other.highClosed && !highClosed && highEnd == other.highEnd))
            }
            else -> return false
        }
    }
    override fun contains(other: Number): Boolean {
        val o = other.asCalculatable()
        return (if (highClosed) o <= highEnd; else o < highEnd) && (if (lowClosed) o >= lowEnd; else o > lowEnd)
    }

    override fun hasCommonElementsWith(other: com.mindforge.graphics.math.Set): Boolean {
        // TODO
        return false
    }

    override fun equals(other: Any?): Boolean {
        return other is RealSet &&
                lowEnd == other.lowEnd && highEnd == other.highEnd && lowClosed == other.lowClosed && highClosed == other.highClosed
    }


}

val realSet = RealSet

fun openRealSet(le: Number, he: Number) = realSet(lEnd = le, hEnd = he, lClosed = false, hClosed = false)