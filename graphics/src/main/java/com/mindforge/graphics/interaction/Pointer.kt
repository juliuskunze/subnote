package com.mindforge.graphics.interaction

import com.mindforge.graphics.*

trait Pointer {
    val moved: Observable<Pointer>
    val location: Vector2

    fun relativeTo(transform: Transform2): Pointer = object : Pointer {
        override val moved: Observable<Pointer> = observable(this@Pointer.moved) { this }
        override val location: Vector2 get() = transform.inverse()(this@Pointer.location)
    }
}
trait PointerKey {
    val pointer: Pointer
    val key: Key
}

fun pointerKey(pointer: Pointer, key: Key): PointerKey = object : PointerKey {
    override val pointer = pointer
    override val key = key
}

trait PointerKeys {
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

trait Scroll {
    val scrolled: Observable<Number>
}