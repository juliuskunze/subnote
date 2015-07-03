package com.mindforge.graphics.interaction

import com.mindforge.graphics.Element
import com.mindforge.graphics.Observable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

fun scheduleDelayed(delayInMs : Long, action: () -> Unit) : ScheduledFuture<*> =
    Executors.newSingleThreadScheduledExecutor().schedule(action, delayInMs, TimeUnit.MILLISECONDS)


interface PointersElement<T> : Element<T> {
    fun onPointerKeyPressed(pointerKey: PointerKey) {}
    fun onPointerKeyReleased(pointerKey: PointerKey) {}
    fun onPointerMoved(pointer: Pointer) {}
    fun onPointerEntered(pointer: Pointer) {}
    fun onPointerLeft(pointer: Pointer) {}
}

interface LongClickable<T> : PointersElement<T> {
    val longClickCanceled: Observable<PointerKey>
    val longClick: Observable<PointerKey>
}