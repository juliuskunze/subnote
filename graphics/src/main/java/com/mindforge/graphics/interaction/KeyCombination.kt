package com.mindforge.graphics.interaction

interface KeyCombination {
    val keys: Iterable<Key>
    val meaning: Command?
}