package com.mindforge.graphics

trait Trigger<T> : Observable<T> {
    final fun invoke(info: T) {
        notifyObservers(info)
    }
}

fun trigger<T>() = object : Trigger<T> {
    override val observers = hashSetOf<(T) -> Unit>()
}

fun Trigger<Unit>.invoke() {
    invoke(Unit)
}