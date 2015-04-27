package com.mindforge.graphics.math.simplifier

import com.mindforge.graphics.math.*
import com.mindforge.graphics.math.operations.*
import com.mindforge.graphics.replaceElements


fun rotateBasicOperations(it: Real): Real {
    class pnRealArray(val p: List<Real>, val n: List<Real>)
    class mdRealArray(val m: List<Real>, val d: List<Real>)

    // Inner fun
    fun rotateInnerAdd(it: Real): Real {
        if (it !is AdditionValue && it !is SubtractionValue) return it
        fun pn_add(it: Real): pnRealArray {
            if (it.isZero) return pnRealArray(listOf(), listOf())
            if (it !is AdditionValue && it !is SubtractionValue)
                return if (it.isPositive) pnRealArray(listOf(rotateBasicOperations(it)), listOf())
                else pnRealArray(listOf(), listOf(rotateBasicOperations(-it)))
            val r1 = pn_add(it.subReals[0])
            val r2 = pn_add(it.subReals[1])
            if (it is AdditionValue) return pnRealArray((r1.p + r2.p), (r1.n + r2.n))
            return pnRealArray((r1.p + r2.n), (r1.n + r2.p))
        }
        val pn = pn_add(it)
        val p = pn.p filter { !it.isZero }
        val n = pn.n filter { !it.isZero }
        var pos: Real = if (!p.isEmpty()) p[0] else real(0)
        var i: Int = 1
        while (p.size > i) {
            pos = pos + p[i]
            i++
        }
        if (n.isEmpty()) return pos
        var neg: Real = n[0]
        i = 1
        while (n.size > i) {
            neg = neg + n[i]
            i++
        }
        return pos - neg
    }
    // Inner Fun
    fun rotateInnerMul(it: Real): Real {
        if (it !is MultiplicationValue && it !is DivisionValue) return it
        fun md_add(it: Real): mdRealArray {
            if (it is RealPrimitive && it.value equals 1) return mdRealArray(listOf(), listOf())
            if (it !is MultiplicationValue && it !is DivisionValue) return mdRealArray(listOf(rotateBasicOperations(it)), listOf())
            val r1 = md_add(it.subReals[0])
            val r2 = md_add(it.subReals[1])
            if (it is MultiplicationValue) return mdRealArray((r1.m + r2.m), (r1.d + r2.d))
            return mdRealArray((r1.m + r2.d), (r1.d + r2.m))
        }
        val md = md_add(it)
        val m = if (md.m.isEmpty() || md.m any { it.isZero }) listOf(real(0)) else md.m
        val d = if (md.d any { it.isZero }) listOf(real(0)) else md.d
        var mul: Real = if (m.isEmpty()) real(1) else m[0]
        var i: Int = 1
        while (m.size > i) {
            mul = mul * m[i]
            i++
        }
        if (d.isEmpty()) return mul
        var div: Real = d[0]
        i = 1
        while (d.size > i) {
            div = div * d[i]
            i++
        }
        return mul / div
    }

    // Code starts here
    when (it) {
        is RealBinaryOperation -> {
            if (it is AdditionValue || it is SubtractionValue) return rotateInnerAdd(it)
            if (it is MultiplicationValue || it is DivisionValue) return rotateInnerMul(it)
        }
    }
    return it.replaceSubReals(it.subReals replaceElements { rotateBasicOperations(it) })
}

fun Real.rotate(): Real = rotateBasicOperations(this)