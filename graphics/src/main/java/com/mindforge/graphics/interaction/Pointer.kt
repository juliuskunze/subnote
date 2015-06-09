package com.mindforge.graphics.interaction

import com.mindforge.graphics.*

interface Pointer {
    val moved: Observable<Pointer>
    val location: Vector2

    fun transformed(transform: Transform2): Pointer = object : Pointer {
        override val moved: Observable<Pointer> = observable(this@Pointer.moved) { this }
        override val location: Vector2 get() = transform.inverse()(this@Pointer.location)
    }
}
interface PointerKey {
    val pointer: Pointer
    val key: Key

    fun transformed(transform: Transform2) = pointerKey(pointer.transformed(transform), key)
}

fun pointerKey(pointer: Pointer, key: Key): PointerKey = object : PointerKey {
    override val pointer = pointer
    override val key = key
}

interface PointerKeys {
    val pointer: Pointer
    val keys: Iterable<Key>
    val pressed: Observable<PointerKey>
    val released: Observable<PointerKey>
}

fun pointerKeys(pointer: Pointer, keys: Iterable<Key>) = object : PointerKeys {
    override val pointer = pointer
    override val keys = keys
    override val pressed = observable((keys map { it.pressed })) { pointerKey(pointer, it) }
    override val released = observable((keys map { it.released })) { pointerKey(pointer, it) }
}

interface Scroll {
    val scrolled: Observable<Number>
}