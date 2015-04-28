package com.mindforge.graphics.android

import java.util.HashMap
import android.view.KeyEvent
import com.mindforge.graphics.ObservableIterable
import com.mindforge.graphics.Vector2
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.trigger
import com.mindforge.graphics.zeroVector2

class AndroidKey(val command: Command) : Key {
    override val definition: KeyDefinition = keyDefinition(command)
    override var isPressed: Boolean = false
    override val pressed = trigger<Key>()
    override val released = trigger<Key>()

    fun press() {
        isPressed = true
        pressed(this)
    }

    fun release() {
        isPressed = false
        released(this)
    }
}

class AndroidKeyboard : ObservableIterable<Key> {
    private val keyMap = HashMap<Command, AndroidKey>()
    override val added = trigger<Key>()
    override val removed = trigger<Key>()
    override fun iterator() = keyMap.values().iterator()

    fun get(event: KeyEvent): AndroidKey? {
        val command = when {
            event.isPrintingKey() -> Commands.Keyboard.character(event.getUnicodeChar().toChar())
            else -> when (event.getKeyCode()) {
                KeyEvent.KEYCODE_SPACE-> Commands.Keyboard.space
                KeyEvent.KEYCODE_TAB-> Commands.Keyboard.tab
                KeyEvent.KEYCODE_ENTER-> Commands.Keyboard.enter
                KeyEvent.KEYCODE_DEL -> Commands.Keyboard.backspace
                KeyEvent.KEYCODE_BACK -> Commands.Navigation.backward
                KeyEvent.KEYCODE_DPAD_UP -> Commands.Navigation.up
                KeyEvent.KEYCODE_DPAD_DOWN -> Commands.Navigation.down
                KeyEvent.KEYCODE_DPAD_LEFT -> Commands.Navigation.left
                KeyEvent.KEYCODE_DPAD_RIGHT -> Commands.Navigation.right
                KeyEvent.KEYCODE_PAGE_UP -> Commands.Navigation.pageUp
                KeyEvent.KEYCODE_PAGE_DOWN -> Commands.Navigation.pageUp
                else -> return null
            }
        }
        val foundKey = keyMap[command]
        if (foundKey != null) return foundKey
        val newKey = AndroidKey(command)
        keyMap.put(command, newKey)
        added(newKey)
        return newKey

    }
}

class AndroidPointer : Pointer {
    override val moved = trigger<Pointer>()
    override var location: Vector2 = zeroVector2

    fun move(location: Vector2) {
        this.location = location
        moved(this)
    }
}