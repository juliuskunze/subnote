package com.mindforge.graphics.math.sets

import com.mindforge.graphics.math.*

public trait SetUnion : com.mindforge.graphics.math.Set {
    val subset1: com.mindforge.graphics.math.Set
    val subset2: com.mindforge.graphics.math.Set

    override fun toString(): String = "setUnion(${subset1},${subset2})"

    fun simplifySets(): com.mindforge.graphics.math.Set {
        // TODO
        return this
    }

    override fun hasCommonElementsWith(other: com.mindforge.graphics.math.Set): Boolean {
        // TODO
        return false
    }
}