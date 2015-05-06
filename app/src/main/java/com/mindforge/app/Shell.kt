package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.rectangle
import org.xmind.core.ITopic
import org.xmind.core.IWorkbook

class Shell(val screen: Screen,
            val pointers: ObservableIterable<PointerKeys>,
            val keys: ObservableIterable<Key>,
            val defaultFont: Font,
            val workbook: IWorkbook,
            val onOpenHyperlink: (String) -> Unit,
            val textChanged: Observable<String>,
            val onActiveTopicChanged: (ITopic?) -> Unit,
            val addSubnode: Trigger<Unit>,
            val removeNode: Trigger<Unit>) {
    var scroll = Scrollable(mindMap())

    private var activeTopicLoc: ITopic? = null

    private var activeTopic: ITopic?
        get() = activeTopicLoc
        set(it: ITopic?) {
            activeTopicLoc = it
            onActiveTopicChanged(it)
        }

    fun mindMap(): Composed<*> = composed(listOf(
            transformedElement(Draggable(coloredElement(rectangle(vector(200, 200)), Fills.solid(Colors.red)))),
            transformedElement(Draggable(coloredElement(rectangle(vector(300, 100)), Fills.solid(Colors.green)))),
            transformedElement(Draggable(coloredElement(rectangle(vector(100, 300)), Fills.solid(Colors.blue)))),
            transformedElement(topicElement(workbook.getPrimarySheet().getRootTopic()).element)
    ))

    fun render() {
        //TODO: rebuild only the part that changed?
        val oldScrollPos = scroll.scrollPosition
        scroll = Scrollable(mindMap())
        scroll.scrollPosition = oldScrollPos
        screen.content = scroll
    }

    init {
        textChanged addObserver fun(it: String) {
            val topic = activeTopic
            if (topic == null) return

            topic.setTitleText(it)

            render()
        }

        addSubnode addObserver fun(it: Unit) {
            val topic = activeTopic
            if (topic == null) return

            val newTopic = workbook.createTopic()
            newTopic.setTitleText("new note")
            topic.add(newTopic)

            render()
        }

        removeNode addObserver fun(it: Unit) {
            val topic = activeTopic
            if (topic == null) return

            val parent = topic.getParent()
            parent.remove(topic)

            activeTopic == parent

            render()
        }

        render()
        registerInputs()
    }

    fun topicElement(topic: ITopic): Stackable {
        val text = topic.getTitleText()
        val lineHeight = 40
        val mainButtonContent = textElement(text, fill = Fills.solid(if(activeTopic == topic) Colors.red else Colors.black), font = defaultFont, lineHeight = lineHeight)
        val mainButton = textRectangleButton(mainButtonContent) {
            activeTopic = topic
        }

        val subTopics = topic.getAllChildren()
        val unfoldedSubTopics = if (topic.isFolded()) listOf() else subTopics
        val linkButtonIfHas = if (topic.getHyperlink() == null) null else {
            val linkButtonTextElement = textElement("Link", fill = Fills.solid(Colors.blue), font = defaultFont, lineHeight = lineHeight)

            val element = textRectangleButton(linkButtonTextElement) {
                onOpenHyperlink(topic.getHyperlink())
            }

            Stackable(element, linkButtonTextElement.shape.size())
        }
        val collapseButtonIfHas = if (subTopics.any()) {
            val element = textElement(if(topic.isFolded()) " + " else " - ", fill = Fills.solid(Colors.gray), font = defaultFont, lineHeight = lineHeight)
            val button = textRectangleButton(element) {
                topic.setFolded(!topic.isFolded())
                render()
            }

            Stackable(button, element.shape.size())
        } else null
        val subElements = unfoldedSubTopics.map { topicElement(it) }

        val mainStack = horizontalStack(
                listOf(Stackable(mainButton, mainButtonContent.shape.size())) +
                        (if (linkButtonIfHas == null) listOf() else listOf(linkButtonIfHas)) +
                        (if (collapseButtonIfHas == null) listOf() else listOf(collapseButtonIfHas))
        )

        val transformedElements = mainStack.toArrayList()
        var height: Double = mainButtonContent.shape.size().y.toDouble()

        for (e in subElements) {
            val indent = lineHeight
            transformedElements.add(transformedElement(e.element, Transforms2.translation(vector(indent, -height))))
            height += e.size.y.toDouble()
        }

        return Stackable(composed(transformedElements), vector(0, height))
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

