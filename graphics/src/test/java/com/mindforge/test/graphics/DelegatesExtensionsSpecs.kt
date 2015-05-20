package com.mindforge.test.graphics

import com.mindforge.graphics.observed
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldEqual
import kotlin.properties.Delegates
import kotlin.test.assertEquals

class ObservedDelegateTest {
    var called = false

    var s by Delegates.observed<String?>("A", { old, new ->
        assertEquals("A", old)
        assertEquals(null, new)

        called = true
    })
}

class DelegatesExtensionsSpecs : Spek() {init {
    given("an observed delegate with initial value A") {
        val d = ObservedDelegateTest()

        on("getting the value") {
            val v = d.s

            it("should be A") {
                assertEquals("A", v)
            }
        }

        on("changing the value from A to null") {
            d.s = null

            it("should be called") {
                assertEquals(true, d.called)
            }

            it("should be null") {
                assertEquals(null, d.s)
            }
        }
    }
}
}
