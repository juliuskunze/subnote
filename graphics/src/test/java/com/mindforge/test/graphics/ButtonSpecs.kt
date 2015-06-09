package com.mindforge.test.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.rectangle

enum class State {
    neverClicked,
    clicked
}

class ButtonSpecs : Spek() {init {
    given("a button") {
        val b = coloredButton(shape = rectangle(size = vector(0.04, 0.01)), fill = Fills.solid(Colors.black))


        var s = State.neverClicked

        on("appeding a handler and invoking the button") {
            b.content addObserver { s = State.clicked }
            b.content.invoke()

            it("should be clicked") {
                shouldEqual(s, State.clicked)
            }
        }
    }
}
}

