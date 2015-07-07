package net.pureal.subnote.prototree

import com.mindforge.graphics.*
import com.mindforge.graphics.math.circle
import kotlin.Pair

class Node(val children: List<Node>)

class Tree(val root: Node) {
    fun parent(child: Node, search: Node = root): Node? {
        if (child in search.children) {
            return search
        } else {
            for (it in search.children) {
                val found = parent(child, search=it)
                if (found != null) return found
            }
            return null
        }
    }

    fun siblingIndex(node: Node) = parent(node)?.children?.indexOf(node) ?: -1
}

class TreeNodeElement(val tree: Tree, val node: Node = tree.root) : Composed<Pair<Tree, Node>> {

    override val content = tree to node

    val body = coloredElement(circle(50 - (5 * tree.siblingIndex(node))), Fills.solid(color(Math.random(), Math.random(), Math.random())))

    override val elements: ObservableArrayList<TransformedElement<*>> = ObservableArrayList(
            listOf(transformedElement(body))
                    + node.children.map { node ->
                transformedElement(
                        TreeNodeElement(tree, node),
                        Transforms2.translation(vector(50, tree.siblingIndex(node) * 50))
                )
            }
    )
}