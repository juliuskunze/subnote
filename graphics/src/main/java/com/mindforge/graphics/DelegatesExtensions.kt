package com.mindforge.graphics

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

fun Delegates.observed<T>(initial: T, changed: Trigger<Unit>) = object : ReadWriteProperty<Any?, T> {
    private var value = initial
    public override fun get(thisRef: Any?, desc: PropertyMetadata) = value
    public override fun set(thisRef: Any?, desc: PropertyMetadata, value: T) {
        this.value = value

        changed()
    }
}

fun Delegates.observed<T>(initial: T, onChanged: (old : T, new: T) -> Unit) = object : ReadWriteProperty<Any?, T> {
    private var value = initial
    public override fun get(thisRef: Any?, desc: PropertyMetadata) = value
    public override fun set(thisRef: Any?, desc: PropertyMetadata, value: T) {
        val old = this.value
        this.value = value

        onChanged(old, value)
    }
}