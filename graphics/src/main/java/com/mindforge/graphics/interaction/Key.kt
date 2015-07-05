package com.mindforge.graphics.interaction

import com.mindforge.graphics.Observable

interface Key {
    val definition: KeyDefinition
    val isPressed: Boolean
    val pressed: Observable<Key>
    val released: Observable<Key>
}