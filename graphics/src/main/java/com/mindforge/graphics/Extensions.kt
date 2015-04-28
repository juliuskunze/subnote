package com.mindforge.graphics

fun <T> Array<T>.replaceElements(fn: (T) -> T): Array<T> {
    var t = this
    for (i in t.indices) t[i] = fn.invoke(t[i])
    return t
}

fun String.extractInnerString(c1: Char, c2: Char): String {
    val begin = this.indexOf(c1)
    val end = this.lastIndexOf(c2)
    if (begin >= end || begin == -1) return this
    return this.substring(begin + 1, end)
}

fun String.extractInnerString(c: Char) = extractInnerString(c, c)

fun String.extractInnerString(): String = extractInnerString('(', ')').extractInnerString('"')

fun String.removeWhitespace() = this filter { !it.isWhitespace() }