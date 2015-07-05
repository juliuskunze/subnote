package com.mindforge.graphics.interaction

import com.mindforge.graphics.Element

interface KeysElement<T> : Element<T> {
    fun onKeyPressed(key: Key) {}
    fun onKeyReleased(key: Key) {}
    fun onGotKeysFocus(keys: Iterable<Key>) {}
    fun onLostKeysFocus(keys: Iterable<Key>) {}
}