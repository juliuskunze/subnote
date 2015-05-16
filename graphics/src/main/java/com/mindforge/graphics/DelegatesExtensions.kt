package com.mindforge.graphics

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

fun Delegates.observing<T>(initial: T, trigger: Trigger<Unit>) = object : ReadWriteProperty<Any?, T> {
    private var value = initial
    public override fun get(thisRef: Any?, desc: PropertyMetadata) = value
    public override fun set(thisRef: Any?, desc: PropertyMetadata, value: T) {
        this.value = value

        trigger()
    }
}