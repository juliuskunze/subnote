package com.mindforge.graphics.interaction

trait KeyCombination {
    val keys: Iterable<Key>
    val meaning: Command?
}