package com.mindforge.graphics

trait ObservableIterable<T> : Iterable<T> {
    val added: Observable<T>
    val removed: Observable<T>

    val addedAt: Observable<IndexedValue<T>>
    val removedAt: Observable<IndexedValue<T>>

    fun mapObservable<O>(transform: (T) -> O): ObservableIterable<O> = object : ObservableIterable<O> {
        override val added = observable(this@ObservableIterable.added, transform)
        override val removed = observable(this@ObservableIterable.removed, transform)

        override val addedAt = observable(this@ObservableIterable.addedAt) { IndexedValue(it.index, transform(it.value)) }
        override val removedAt = observable(this@ObservableIterable.removedAt) { IndexedValue(it.index, transform(it.value)) }

        override fun iterator() = (this@ObservableIterable map transform).iterator()
    }
}

fun <T> ObservableIterable<Observable<T>>.startKeepingAllObserved(observer: (T) -> Unit): Observer {
    val observersByElement = hashMapOf(*(this.map { it to (it addObserver { observer(it) } ) }.copyToArray()))

    val o1 = added addObserver { observersByElement.put(it, it addObserver { observer(it) }) }
    val o2 = removed addObserver { observersByElement.remove(it)!!.stop() }

    return object : Observer {
        override fun stop() {
            observersByElement.values().forEach { it.stop() }
            o1.stop()
            o2.stop()
        }
    }
}

fun observableIterable<T>(elements: Iterable<T>) = object : ObservableIterable<T> {
    override val added = observable<T>()
    override val removed = observable<T>()
    override val addedAt = observable<IndexedValue<T>>()
    override val removedAt = observable<IndexedValue<T>>()

    override fun iterator() = elements.iterator()
}