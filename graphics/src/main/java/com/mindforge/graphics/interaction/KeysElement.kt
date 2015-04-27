package com.mindforge.graphics.interaction

import com.mindforge.graphics.graphics.Element

trait KeysElement<T> : Element<T> {
    fun onKeyPressed(key: Key) {}
    fun onKeyReleased(key: Key) {}
    fun onGotKeysFocus(keys: Iterable<Key>) {}
    fun onLostKeysFocus(keys: Iterable<Key>) {}
}