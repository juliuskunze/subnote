package com.mindforge.graphics.math

public trait Variable : Symbol {
    val requiredSet: Set

    override fun matchWithThisPattern(other: Real): Boolean = other in requiredSet
}