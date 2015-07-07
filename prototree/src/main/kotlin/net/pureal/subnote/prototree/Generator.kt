package net.pureal.subnote.prototree

fun random(max: Int) = Math.random().times(max).toInt()

fun randomNode(maxDepth: Int = 8, maxWidth: Int = 8): Node = Node(
        if (maxDepth == 0) emptyList()
        else 1.rangeTo(random(maxWidth)).map { randomNode(maxDepth - 1, maxWidth) }
)