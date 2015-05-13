package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.rectangle
import org.xmind.core.Core
import org.xmind.core.ITopic
import org.xmind.core.IWorkbook
import org.xmind.core.internal.dom.TopicImpl
import kotlin.properties.Delegates

class Shell(val screen: Screen,
            val pointers: ObservableIterable<PointerKeys>,
            val keys: ObservableIterable<Key>,
            val defaultFont: Font,
            val workbook: IWorkbook,
            val onOpenHyperlink: (String) -> Unit,
            val textChanged: Observable<String>,
            val onActiveTopicChanged: (ITopic?) -> Unit,
            val newNote: Trigger<Unit>,
            val newSubnote: Trigger<Unit>,
            val removeNode: Trigger<Unit>
) {
    private var activeTopicLoc: ITopic? = null

    private var activeNote: ITopic?
        get() = activeTopicLoc
        set(it: ITopic?) {
            activeTopicLoc = it
            onActiveTopicChanged(it)
        }

    fun content(): Composed<*> = Scrollable(composed(listOf(
            transformedElement(Draggable(coloredElement(rectangle(vector(200, 200)), Fills.solid(Colors.red)))),
            transformedElement(Draggable(coloredElement(rectangle(vector(300, 100)), Fills.solid(Colors.green)))),
            transformedElement(Draggable(coloredElement(rectangle(vector(100, 300)), Fills.solid(Colors.blue)))),
            transformedElement(TopicElement(workbook.getPrimarySheet().getRootTopic() as TopicImpl))
    )))

    fun withActiveNoteIfHas(action: ITopic.() -> Unit) {
        val topic = activeNote
        if (topic == null) return

        topic.action()
    }

    fun initializeNewNote(newNote: ITopic) {
        newNote.setTitleText("new note")
        newNote.getParent().setFolded(false)

        activeNote = newNote
    }

    init {
        textChanged addObserver {
            withActiveNoteIfHas {
                setTitleText(it)
            }
        }

        newNote addObserver {
            withActiveNoteIfHas {
                val newNote = workbook.createTopic()

                val parent = getParent()
                parent.add(newNote, getIndex() + 1, ITopic.ATTACHED)
                initializeNewNote(newNote)
            }
        }

        newSubnote addObserver {
            withActiveNoteIfHas {
                val newNote = workbook.createTopic()
                add(newNote)

                initializeNewNote(newNote)
            }
        }

        removeNode addObserver {
            withActiveNoteIfHas {
                val parent = getParent()
                parent.remove(this)

                activeNote = parent
            }
        }

        screen.content = content()

        registerInputs()
    }

    inner class TopicElement(topic: TopicImpl) : Composed<ITopic> {
        override val content = topic
        override val changed = trigger<Unit>()
        override val elements = ObservableArrayList<TransformedElement<*>>()
        var subElements : List<TopicElement> by Delegates.notNull()
        var height: Double by Delegates.notNull()

        init {
            initElementsAndHeight()

            topic.getCoreEventSupport().registerCoreEventListener(content, Core.TitleText, { initElementsAndHeight() })
        }

        private fun initElementsAndHeight() {
            val topic = content
            val text = topic.getTitleText()
            val lineHeight = 40
            val mainButtonContent = textElement(text, fill = Fills.solid(if (activeNote == topic) Colors.red else Colors.black), font = defaultFont, lineHeight = lineHeight)
            val mainButton = Stackable(textRectangleButton(mainButtonContent) {
                activeNote = topic
            }, mainButtonContent.shape.size())

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
                val element = textElement(if (topic.isFolded()) " + " else " - ", fill = Fills.solid(Colors.gray), font = defaultFont, lineHeight = lineHeight)
                val button = textRectangleButton(element) {
                    topic.setFolded(!topic.isFolded())
                    changed()
                }

                Stackable(button, element.shape.size())
            } else null
            subElements = unfoldedSubTopics.map { TopicElement(it as TopicImpl) }

            val mainStack = horizontalStack(listOf(mainButton, linkButtonIfHas, collapseButtonIfHas).filterNotNull())

            elements.clearAndAddAll(mainStack)
            var partialHeight = mainButtonContent.shape.size().y.toDouble()

            for (e in subElements) {
                val indent = lineHeight
                elements.add(transformedElement(e, Transforms2.translation(vector(indent, -partialHeight))))
                partialHeight += e.stackable().size.y.toDouble()
            }

            this.height = partialHeight
        }

        // TODO: remove height schlemian
        fun stackable() = Stackable(this, vector(0, subElements.map {it.height}.sum()))
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

