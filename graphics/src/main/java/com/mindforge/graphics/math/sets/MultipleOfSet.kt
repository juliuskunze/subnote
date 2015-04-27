package com.mindforge.graphics.math.sets

import com.mindforge.graphics.math.*
import com.mindforge.graphics.*

public trait MultipleOfSet : RealSet, com.mindforge.graphics.math.Set {
    public companion object :
            Constructor4<com.mindforge.graphics.math.Set, Number, Number, Number, Number>,
            Constructor3<com.mindforge.graphics.math.Set, Number, Number, Number>,
            Constructor2<com.mindforge.graphics.math.Set, Number, RealSet> {
        override fun invoke(factor: Number, offset: Number, lEnd: Number, hEnd: Number): com.mindforge.graphics.math.Set {
            val factor = factor.asCalculatable().abs()
            // to make 0 <= offset < factor
            val off = offset divideAndRemainder factor
            if (factor is Infinity) return EmptySet
            var le = lEnd.asCalculatable() + off[0]
            var he = hEnd.asCalculatable() + off[0]
            if (le > he) {
                val tmp = le
                le = he
                he = tmp
            }
            le = le.ceil() * factor + off[1]
            he = he.floor() * factor + off[1]
            return object : MultipleOfSet {
                override val factor = factor
                override val offset = off[1]
                override val lowEnd = le
                override val highEnd = he
            }
        }
        override fun invoke(factor: Number, lEnd: Number, hEnd: Number): com.mindforge.graphics.math.Set = invoke(factor, 0, lEnd, hEnd)
        override fun invoke(factor: Number, s: RealSet): com.mindforge.graphics.math.Set = invoke(factor, s.lowEnd, s.highEnd)
    }

    override fun toString(): String = "multipleOfSet(${factor}, ${lowEnd / factor}, ${highEnd / factor})"

    override fun contains(other: Number): Boolean {
        try {
            val o = (other.asCalculatable() - offset) % factor
            return super<RealSet>.contains(other) && o equals 0
        } catch(e: UnsupportedOperationException) {
            return false
        }
    }

    override fun contains(other: com.mindforge.graphics.math.Set): Boolean {
        if (other is MultipleOfSet)
            return lowEnd <= other.lowEnd && highEnd >= other.lowEnd
                    && other.factor % factor equals 0 && (other.offset - offset) % factor equals 0
        return super<com.mindforge.graphics.math.Set>.contains(other)
    }

    val factor: Calculatable
    val offset: Calculatable

    override val lowClosed: Boolean get() = true
    override val highClosed: Boolean get() = true

    override fun equals(other: Any?) = other is MultipleOfSet && factor == other.factor && lowEnd == other.lowEnd && highEnd == other.highEnd

}

val multipleOfSet = MultipleOfSet