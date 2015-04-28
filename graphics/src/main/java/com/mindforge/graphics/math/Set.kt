package com.mindforge.graphics.math

import com.mindforge.graphics.math.sets.*

public trait Set {
    override fun toString(): String
    fun contains(other: Set): Boolean {
        if (other is SetUnion) {
            return contains(other.subset1) && contains(other.subset2)
        }
        if (other is SetIntersection) {

        }
        if (other is EmptySet) return true
        return false
    }
    fun contains(other: Number): Boolean
    fun hasCommonElementsWith(other: Set): Boolean

    override fun equals(other: Any?) = other is Set && this in other && other in this
}