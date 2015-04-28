package com.mindforge.graphics

public trait Numbers : Iterable<Number>

fun Number.clamp(range: DoubleRange) = when {
    this.toDouble() < range.start -> range.start
    this.toDouble() > range.end -> range.end
    else -> this
}