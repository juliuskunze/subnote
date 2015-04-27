package com.mindforge.graphics.math

public trait MatchingVariable : Variable {
    var matchingReal: Real? protected set
    inline val hasMatch: Boolean get() = matchingReal != null
    override fun matchWithThisPattern(other: Real): Boolean {
        if (hasMatch) return matchingReal == other
        if (super.matchWithThisPattern(other)) {
            matchingReal = other
            return true
        }
        return false
    }

    fun reset() {
        matchingReal = null
    }

    override fun calculate(): Real = matchingReal?.calculate() ?: this
    override fun equals(other: Any?): Boolean {
        if (other is Variable) return other.name == name && other.requiredSet == requiredSet
        if (matchingReal != null) return matchingReal == other
        return false
    }
}