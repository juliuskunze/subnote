package com.mindforge.graphics

trait Observable<T> {
    protected val observers: MutableSet<(T) -> Unit>
    final fun addObserver(observer: (T) -> Unit) {
        observers.add(observer)
    }
    final fun removeObserver(observer: (T) -> Unit) {
        observers.remove(observer)
    }
    final protected fun notifyObservers(info: T) {
        for (observer in observers) {
            observer(info)
        }
    }
}

fun observable<T, I>(observables: Iterable<Observable<I>>, transform: (I) -> T) = object : Observable<T> {
    override val observers = hashSetOf<(T) -> Unit>()

    init {
        for (observable in observables) {
            observable addObserver { notifyObservers(transform(it)) }
        }
    }
}

fun observable<T, I>(observable: Observable<I>, transform: (I) -> T): Observable<T> = observable(listOf(observable), transform)

fun observable<T>(observables: Iterable<Observable<T>>): Observable<T> = observable(observables) { it }

fun observable<T>(vararg observables: Observable<T>): Observable<T> = observable(object : Iterable<Observable<T>> {
    override fun iterator() = observables.iterator()
})
