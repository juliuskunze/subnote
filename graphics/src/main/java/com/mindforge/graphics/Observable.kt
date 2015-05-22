package com.mindforge.graphics

trait Observable<T> {
    fun addObserver(action: Observer.(T) -> Unit): Observer {
        val observer = object : ObserverAction<T> {
            override fun invoke(value: T) = this.action(value)

            override fun stop() {
                if (!observers.remove(this)) {
                    throw IllegalStateException("Stop can only be called once.")
                }
            }
        }
        observers.add(observer)

        if (observers.count() > 16) {
            throw IllegalStateException("Too many observers.")
        }

        return observer
    }

    protected val observers: MutableSet<ObserverAction<T>>
    protected fun notifyObservers(info: T) {
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

    override fun addObserver(action: Observer.(T) -> Unit) = object : ObserverAction<T> {
        val innerObservers = observables.map { it addObserver { invoke(transform(it)) } }
        override fun invoke(value: T) = this.action(value)
        override fun stop() {
            innerObservers.forEach { it.stop() }
        }
    }

    override protected final val observers = hashSetOf<ObserverAction<T>>()
}

fun observable<T, I>(observable: Observable<I>, transform: (I) -> T): Observable<T> = observable(listOf(observable), transform)

fun observable<T>(observables: Iterable<Observable<T>>): Observable<T> = observable(observables) { it }

fun observable<T>(vararg observables: Observable<T>): Observable<T> = observable(object : Iterable<Observable<T>> {
    override fun iterator() = observables.iterator()
})
