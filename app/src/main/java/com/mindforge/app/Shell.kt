package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.math.shape
import org.xmind.core.ITopic
import java.util.ArrayList
import java.util.Date

class Shell(val screen: Screen, val pointers: ObservableIterable<PointerKeys>, val keys: ObservableIterable<Key>, defaultFont: Font, rootTopics: List<ITopic>) {
    private val exampleContent = object {
        fun rotatedScaledRectangles(): Composed<*> {
            fun logoRect(angle: Number) = coloredButton(
                    shape = rectangle(vector(300, 100)) transformed (Transforms2.translation(vector(-70, 60)) before Transforms2.rotation(angle) before Transforms2.scale(0.5 * angle.toDouble())),
                    fill = Fills.solid(Colors.white),
                    onClick = { println("This is da fucking Pureal logo!") })

            fun star(count: Int): List<TransformedElement<*>> = count.indices map { transformedElement(logoRect(it * 3.14159 * 2 / count - Math.PI / 2)) }

            return composed(observableIterable(star(7)))
        }

        fun someText(): Composed<*> {
            val text = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam
nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
sed diam voluptua. At vero eos et accusam et justo duo dolores et ea
rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem
ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing
elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna
aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo
dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus
est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur
sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et
dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam
et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
takimata sanctus est Lorem ipsum dolor sit amet. AYA �¶Ѽ†◊²³"""
            val t = transformedElement(
                    textElement(text, defaultFont, lineHeight = 60, fill = Fills.solid(Colors.white)),
                    object : Transform2 {
                        override val matrix: Matrix3 get() = (Transforms2.translation(vector(-1000, 400)) before Transforms2.rotation(Date().getTime() * 0.0005) before Transforms2.scale(.2 + Math.pow(1 + Math.pow(Math.sin(Date().getTime() * 0.0002), 5.0), 5.0))).matrix
                    }
            )

            //val k = transformedElement(textElement("Kotlin rocks!", font, size = 24, fill = Fills.solid(Colors.white)), Transforms2.rotation(-Math.PI / 10))
            //val h = transformedElement(textElement("like a hardcore banana", font, size = 24, fill = Fills.solid(Colors.white)), Transforms2.scale(0.5) before Transforms2.translation(vector(0,-(screen.shape as Rectangle).size.y.toDouble() / 3.0)))
            return composed(observableIterable(listOf<TransformedElement<*>>(t)))//k, h)))
        }

        private fun randomColor() = color(Math.random(), Math.random(), Math.random(), Math.random())

        fun composedWithButton(): Composed<*> {
            val size = vector(100, 100)
            fun b(x: Int, y: Int): TransformedElement<Any?> {
                var color = randomColor()
                return transformedElement(textRectangleButton(text = x.toString(), font = defaultFont, size = 100, fill = object : Fill {
                    override fun colorAt(location: Vector2) = color
                }) {
                    color = randomColor()
                }, object : Transform2 {
                    override val matrix: Matrix3 get() = (
                            Transforms2.rotation(Date().getTime() * -0.0003 + Math.sin(Date().getTime() * 0.005) / 5) before
                                    Transforms2.translation(vector(x * size.x.toDouble(), y * size.y.toDouble())) before
                                    Transforms2.rotation(Date().getTime() * 0.0005) before
                                    Transforms2.scale(.2 + Math.pow(1 + Math.pow(Math.sin(Date().getTime() * 0.0002), 5.0), 5.0))
                            ).matrix
                }
                )
            }

            val a = 2
            //transformedElement(text, Transforms2.translation(vector(0, -100)))
            return composed(observableIterable(-a..a flatMap { x -> -a..a map { y -> b(x, y) : TransformedElement<*> } }))
        }

        fun keyboardText(): Composed<*> {
            val textElement = object : TextElement {
                override val font: Font = defaultFont
                override val lineHeight: Number = 40
                override var content: String = "Hallo"
                override val fill: Fill = Fills.solid(Colors.white)
                override val changed = trigger<Unit>()
            }
            keys mapObservable { it.pressed } startKeepingAllObserved {
                textElement.content = it.definition.command.name
                textElement.changed()
            }
            return composed(observableIterable(listOf<TransformedElement<*>>(transformedElement(textElement))))
        }

        class Draggable(val element: Element<*>) : Composed<Any?>, PointersElement<Any?> {
            override val content: Any? get() = element.content
            override val changed = trigger<Unit>()
            var dragPosition = zeroVector2
            override val elements: ObservableIterable<TransformedElement<*>> = observableIterable(listOf(object : TransformedElement<Any?> {
                override val element: Element<Any?> = this@Draggable.element
                override val transform: Transform2 get() = Transforms2.translation(dragPosition)
                override val transformChanged = this@Draggable.changed
            }))

            val onDrag = { pointer: Pointer ->
                dragPosition = pointer.location
                changed()
            }

            val pointers = ArrayList<Pointer>()

            val onRelease: (Key) -> Unit = { key: Key ->
                key.released removeObserver onRelease
                pointers.forEach {
                    it.moved removeObserver onDrag
                }
                pointers.clear()
            }

            override fun onPointerKeyPressed(pointerKey: PointerKey) {
                pointers.add(pointerKey.pointer)
                pointerKey.pointer.moved addObserver onDrag
                pointerKey.key.released addObserver onRelease
            }

        }

        fun mindMap(): Composed<*> = composed(listOf(
                transformedElement(Draggable(coloredElement(rectangle(vector(200, 200)), Fills.solid(Colors.red)))),
                transformedElement(Draggable(coloredElement(rectangle(vector(300, 100)), Fills.solid(Colors.green)))),
                transformedElement(Draggable(coloredElement(rectangle(vector(100, 300)), Fills.solid(Colors.blue)))),
                transformedElement(topicElement(rootTopics.single()).element)
        ))

        class ElementWithHeight(val element: Element<*>, val height: Int)

        fun topicElement(topic: ITopic): ElementWithHeight {
            val text = topic.getTitleText()
            val fontHeight = 40
            val textElement = textRectangleButton(text, fill = Fills.solid(Colors.black), font = defaultFont, size = fontHeight) {
                topic.setFolded(!topic.isFolded())
                render()
            }

            val unfoldedSubTopics = if (topic.isFolded()) listOf() else topic.getAllChildren()

            if (unfoldedSubTopics.none()) return ElementWithHeight(textElement, fontHeight)

            val subElements = unfoldedSubTopics.map { topicElement(it) }
            val transformedSubElements = arrayListOf<TransformedElement<*>>()

            var height = fontHeight
            for (e in subElements.withIndex()) {
                transformedSubElements.add(transformedElement(e.value.element, Transforms2.translation(vector(fontHeight, -height))))

                height += e.value.height
            }

            return ElementWithHeight(composed(topic, listOf(transformedElement(textElement)) + transformedSubElements), height)
        }

        class Scrollable(val element: Element<*>) : Composed<Any?>, PointersElement<Any?> {
            override val content: Any? get() = element.content
            override val changed = trigger<Unit>()
            var scrollPosition = zeroVector2
            override val elements: ObservableIterable<TransformedElement<*>> = observableIterable(listOf(object : TransformedElement<Any?> {
                override val element: Element<Any?> = this@Scrollable.element
                override val transform: Transform2 get() = Transforms2.translation(scrollPosition)
                override val transformChanged = this@Scrollable.changed
            }, transformedElement(coloredElement(rectangle(vector(10000, 10000)), Fills.solid(Colors.white)))))
            private var lastLocation: Vector2? = null

            override fun onPointerKeyPressed(pointerKey: PointerKey) {
                lastLocation = pointerKey.pointer.location
            }

            override fun onPointerKeyReleased(pointerKey: PointerKey) {
                lastLocation = null
            }

            override fun onPointerMoved(pointer: Pointer) {
                val last = lastLocation
                val current = pointer.location
                if (last != null) {
                    scrollPosition += current - last
                    lastLocation = current
                    changed()
                }
            }

        }

        var scroll = Scrollable(mindMap())
        fun render() {
            //TODO: rebuild only the part that changed?
            //screen.content = mindMap()
            val oldScrollPos = scroll.scrollPosition
            scroll = Scrollable(mindMap())
            scroll.scrollPosition = oldScrollPos
            screen.content = scroll
        }
    }

    init {
        exampleContent.render()
        registerInputs()
    }

    fun registerInputs() {
        pointers mapObservable { it.pressed } startKeepingAllObserved { pk ->
            for (it in screen.elementsAt(pk.pointer.location)) {
                val element = it.element
                val pointerKey = pointerKey(pk.pointer relativeTo it.transform, pk.key)
                when (element) {
                    is PointersElement<*> -> {
                        element.onPointerKeyPressed(pointerKey)
                        break
                    }
                }
            }
        }
        pointers mapObservable { it.pointer.moved } startKeepingAllObserved { p ->
            for (it in screen.elementsAt(p.location)) {
                val element = it.element
                val pointer = p relativeTo it.transform
                when (element) {
                    is PointersElement<*> -> {
                        element.onPointerMoved(pointer)
                        break
                    }
                }
            }
        }
        pointers mapObservable { it.released } startKeepingAllObserved { pk ->
            for (it in screen.elementsAt(pk.pointer.location)) {
                val element = it.element
                val pointerKey = pointerKey(pk.pointer relativeTo it.transform, pk.key)
                when (element) {
                    is PointersElement<*> -> {
                        element.onPointerKeyReleased(pointerKey)
                        break
                    }
                }
            }
        }
        keys mapObservable { it.pressed } startKeepingAllObserved { k ->
            screen.content.elements forEach {
                val element = it.element
                if (element is KeysElement<*>) {
                    element.onKeyPressed(k)
                }
            }
        }
    }
}




