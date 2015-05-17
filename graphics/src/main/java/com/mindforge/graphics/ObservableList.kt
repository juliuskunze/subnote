package com.mindforge.graphics

import java.util.ArrayList

trait ObservableList<T> : ObservableIterable<T>, MutableList<T>

fun observableArrayListOf<T>(vararg elements: T) = ObservableArrayList<T>(elements map { it })

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
        if (!super<ArrayList>.remove(o)) {
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

    override fun addAll(index: Int, c: Collection<T>): Boolean {
        super<ArrayList>.addAll(index, c)

        for (e in c) {
            //TODO call indexed version
            added(e)
        }

        return c.any()
    }

    override fun add(index: Int, element: T): Unit {
        super<ArrayList>.add(index, element)

        //TODO call indexed version
        added(element)
    }


    override fun remove(index: Int): T {
        val o = super<ArrayList>.remove(index)

        //TODO: call indexed version:
        removed(o)

        return o
    }

    override fun retainAll(c: Collection<Any?>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun set(index: Int, element: T): T {
        throw UnsupportedOperationException()
    }
}
