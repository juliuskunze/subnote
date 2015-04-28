package com.mindforge.graphics


public trait Constructor<out V> {
    fun invoke(): V
}

public trait Constructor1<out V, T> {
    fun invoke(it: T): V
}

public trait Constructor2<out V, T1, T2> {
    fun invoke(a: T1, b: T2): V
}

public trait Constructor3<out V, T1, T2, T3> {
    fun invoke(a: T1, b: T2, c: T3): V
}

public trait Constructor4<out V, T1, T2, T3, T4> {
    fun invoke(a: T1, b: T2, c: T3, d: T4): V
}

public trait ConstructorVar<out V, T> {
    fun invoke(vararg a: T): V
}