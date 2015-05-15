package com.mindforge.graphics

trait Trigger<T> : Observable<T> {
    final fun invoke(info: T) {
        notifyObservers(info)
    }
}

fun trigger<T>() = object : Trigger<T> {
    override protected final val observers = hashSetOf<ObserverAction<T>>()
}

fun Trigger<Unit>.invoke() {
    invoke(Unit)
}