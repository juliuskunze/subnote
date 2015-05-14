package com.mindforge.test.graphics

import com.mindforge.graphics.ObservableArrayList
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldBeFalse
import org.jetbrains.spek.api.shouldBeTrue
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObservableArrayListSpecs : Spek() {init {
    given("an observable array list only containing some elements") {
        val list = ObservableArrayList(listOf('A', 'B'))

        on("clearing it") {
            list.clear()

            it("should be empty") {
                shouldBeTrue(list.none())
            }
        }
    }

    given("an observable array list only containing A") {
        val list = ObservableArrayList(listOf('A'))

        on("removing B") {
            val existed = list.remove('B')

            it("should not have existed") {
                shouldBeFalse(existed)
            }

            it("should have still 1 element") {
                assertEquals(list.single(), 'A')
            }
        }
    }

    given("an observable array list only containing A and C") {
        val list = ObservableArrayList(listOf('A', 'C'))

        on("removing a list containing A, B and C") {
            val changed = list.removeAll(listOf('A', 'B', 'C'))

            it("should be changed") {
                shouldBeTrue(changed)
            }

            it("should be empty") {
                assertTrue(list.none())
            }
        }
    }

    given("an observable array list only containing B") {
        val list = ObservableArrayList(listOf('B'))

        on("removing a list containing A and B") {
            val changed = list.removeAll(listOf('A', 'B'))

            it("should be changed") {
                shouldBeTrue(changed)
            }

            it("should be empty") {
                assertTrue(list.none())
            }
        }
    }

    given("an observable array list only containing A") {
        val list = ObservableArrayList(listOf('A'))

        on("removing A") {
            val existed = list.remove('A')

            it("should have existed") {
                shouldBeTrue(existed)
            }

            it("should be empty") {
                assertTrue(list.none())
            }
        }
    }

    given("an observable array list only containing A") {
        val list = ObservableArrayList(listOf('A'))

        on("removing all in a list containing only A") {
            val existed = list.removeAll(listOf('A'))

            it("should have existed") {
                shouldBeTrue(existed)
            }

            it("should be empty") {
                assertTrue(list.none())
            }
        }
    }
}
}