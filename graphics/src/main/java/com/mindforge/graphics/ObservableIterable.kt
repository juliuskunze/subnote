package com.mindforge.graphics

import java.util.ArrayList

trait ObservableIterable<T> : Iterable<T> {
    val added: Observable<T>
    val removed: Observable<T>

    fun mapObservable<O>(transform: (T) -> O): ObservableIterable<O> = object : ObservableIterable<O> {
        override val removed = observable(this@ObservableIterable.removed, transform)
        override val added = observable(this@ObservableIterable.added, transform)
        override fun iterator() = (this@ObservableIterable map transform).iterator()
    }
}

fun <T> ObservableIterable<Observable<T>>.startKeepingAllObserved(observer: (T) -> Unit) {
    forEach { it addObserver observer }
    added addObserver { it addObserver observer }
    removed addObserver { it removeObserver observer }
}

fun <T> ObservableIterable<Observable<T>>.stopKeepingAllObserved(observer: (T)->Unit) {
    forEach {it removeObserver observer}
    added removeObserver {it removeObserver observer}
    removed removeObserver {it removeObserver observer}
}

trait ObservableList<T> : ObservableIterable<T>, MutableList<T>

fun observableList<T>(vararg elements: T) : ObservableList<T> = ObservableArrayList<T>(elements map {it})

class ObservableArrayList<T>(elements: Iterable<T> = listOf()) : ArrayList<T>(elements map { it }), ObservableList<T> {
    override val removed = trigger<T>()
    override val added = trigger<T>()
    override fun add(e: T) : Boolean {
        super<ArrayList>.add(e)
        added(e)

        return true
    }

    override fun addAll(c: Collection<T>): Boolean {
        c.forEach { add(it) }
        return c.any()
    }

    override fun remove(o: Any?) : Boolean {
        if(!super<ArrayList>.remove(o)) {
            return false
        }

        removed(o as T)

        return true
    }

    override fun removeAll(c : Collection<Any?>): Boolean {
        var result = false

        for(index in c.indices.reversed()) {
            result = remove(c.elementAt(index)) || result
        }

        return result
    }

    override fun clear() {
        removeAll(this)
    }

    fun clearAndAddAll(newElements : Iterable<T>) {
        clear()
        addAll(newElements)
    }
}
