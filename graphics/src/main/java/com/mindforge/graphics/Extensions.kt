package com.mindforge.graphics

fun <T> MutableCollection<T>.insistRemove(element : T) {
    if(!remove(element)) throw IllegalStateException("Element '$element' not found.")
}