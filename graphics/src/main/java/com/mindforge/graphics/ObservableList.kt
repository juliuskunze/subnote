package com.mindforge.graphics

import java.util.ArrayList


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
