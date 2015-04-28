package com.mindforge.graphics.math.sets

import com.mindforge.graphics.math.*

public trait EmptySet : com.mindforge.graphics.math.Set {
    companion object : EmptySet {
        val a = null
    }
    override fun contains(other: Number) = false
    override fun contains(other: com.mindforge.graphics.math.Set) = false
    override fun hasCommonElementsWith(other: com.mindforge.graphics.math.Set): Boolean = false
    override fun equals(other: Any?) = other is EmptySet

    override fun toString() = "EmptySet"
}