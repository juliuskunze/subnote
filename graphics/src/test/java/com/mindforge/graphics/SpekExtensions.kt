package com.mindforge.graphics

import org.jetbrains.spek.api.*
import com.mindforge.graphics.*
import kotlin.test.*

public fun It.shouldThrow<E : Exception>(exceptionAssertion: (E) -> Unit = {}, action: () -> Unit) {
    try {
        action()
        throw AssertionError("No exception thrown.")
    } catch(ex: E) {
        exceptionAssertion(ex)
    }
}

public fun It.shouldEqualWithError(expected: Double, actual: Double, epsilon: Double = 100 * Math.ulp(expected)) {
    val d = Math.abs(expected - actual)
    val ulps = d / epsilon
    assertTrue(d <= epsilon, if (ulps < 100000) "Too many Ulps! ${ulps}" else "Expected <${expected}> actual <${actual}>")
}

public fun It.shouldEqualWithError(expected: Numbers, actual: Numbers) {
    expected zip actual forEach { shouldEqualWithError(it.component1().toDouble(), it.component2().toDouble()) }
}