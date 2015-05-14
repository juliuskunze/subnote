package com.mindforge.graphics

trait Observable<T> {
    protected val observers: MutableSet<ObserverAction<T>>
    final fun addObserver(action: Observer.(T) -> Unit) : Observer {
        val observer = object: ObserverAction<T> {
            override fun invoke(value: T) = this.action(value)

            override fun stop() {
                if(!observers.remove(this)) throw IllegalStateException("stop can only be called once.")
            }
        }

        observers.add(observer)

        return observer
    }

    final protected fun notifyObservers(info: T) {
        for (observer in observers.toList()) {
            observer(info)
        }
    }
}

trait ObserverAction<T> : Observer {
    fun invoke(value: T)
}

trait Observer {
    fun stop()
}

fun observable<T, I>(observables: Iterable<Observable<I>>, transform: (I) -> T) = object : Observable<T> {
    override val observers = hashSetOf<ObserverAction<T>>()

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
