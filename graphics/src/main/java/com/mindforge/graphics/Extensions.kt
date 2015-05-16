package com.mindforge.graphics

import sun.plugin.dom.exception.InvalidStateException

fun <T> MutableCollection<T>.insistRemove(element : T) {
    if(!remove(element)) throw InvalidStateException("Element '$element' not found.")
}