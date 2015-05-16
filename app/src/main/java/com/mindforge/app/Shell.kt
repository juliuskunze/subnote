package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import org.xmind.core.Core
import org.xmind.core.ITopic
import org.xmind.core.IWorkbook
import org.xmind.core.event.CoreEvent
import org.xmind.core.internal.dom.TopicImpl
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class Shell(val screen: Screen,
            val pointers: ObservableIterable<PointerKeys>,
            val keys: ObservableIterable<Key>,
            val defaultFont: Font,
            val workbook: IWorkbook,
            val onOpenHyperlink: (String) -> Unit,
            val textChanged: Observable<String>,
            val nodeLinkChanged: Observable<NodeLink>,
            val onActiveTopicChanged: (ITopic?) -> Unit,
            val newNote: Trigger<Unit>,
            val newSubnote: Trigger<Unit>,
            val removeNode: Trigger<Unit>,
            val vibrate: ()-> Unit
) {
    private var activeTopicLoc: TopicImpl? = null

    private var activeNote: TopicImpl?
        get() = activeTopicLoc
        set(it: TopicImpl?) {
            val old = activeTopicLoc
            activeTopicLoc = it

            old?.dispatchIsActiveChanged()
            it?.dispatchIsActiveChanged()

            onActiveTopicChanged(it)
        }

    fun content(): Composed<*> = Scrollable(composed(listOf(
            //transformedElement(Draggable(coloredElement(rectangle(vector(200, 200)), Fills.solid(Colors.red)))),
            //transformedElement(Draggable(coloredElement(rectangle(vector(300, 100)), Fills.solid(Colors.green)))),
            //transformedElement(Draggable(coloredElement(rectangle(vector(100, 300)), Fills.solid(Colors.blue)))),
            transformedElement(TopicElement(workbook.getPrimarySheet().getRootTopic() as TopicImpl))
    )))

    private fun withActiveNoteIfHas(action: ITopic.() -> Unit) {
        val topic = activeNote
        if (topic == null) return

        topic.action()
    }

    private fun initializeNewNote(newNote: ITopic) {
        newNote.setTitleText("new note")
        newNote.getParent().setFolded(false)

        activeNote = newNote as TopicImpl
    }

    init {
        textChanged addObserver {
            withActiveNoteIfHas {
                setTitleText(it)
            }
        }

        nodeLinkChanged addObserver {
            withActiveNoteIfHas {
                setHyperlink(it.url)
                it.updateTopic(this)
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

                activeNote = parent as TopicImpl
            }
        }

        screen.content = content()

        registerInputs()
    }

    inner class TopicElement(topic: TopicImpl) : Composed<ITopic> {
        override val content = topic
        override val changed = trigger<Unit>()
        override val elements = ObservableArrayList<TransformedElement<*>>()
        private val subElements = ObservableArrayList<TopicElement>()
        private var stackable = Stackable(this, zeroVector2)
        private var toStop = {}
        private var mainButtonContent: TextElementImpl by Delegates.notNull()
        private var mainButtonContentHeight: Double by Delegates.notNull()

        init {
            initElementsAndStackable()

            val eventTypes = listOf(Core.TopicAdd, Core.TopicRemove, Core.TopicFolded, Core.TopicHyperlink, Core.TopicNotes)
            eventTypes.forEach { content.registerCoreEventListener(it) { initElementsAndStackable() } }

            content.registerCoreEventListener(IsActiveChangedCoreEventType) {
                mainButtonContent.fill = mainColor()
            }

            content.registerCoreEventListener(Core.TitleText) {
                mainButtonContent.content = text()
            }

        }

        private fun mainColor() = Fills.solid(if (activeNote == content) Colors.red else Colors.black)
        private fun text() = content.getTitleText()

        private fun initElementsAndStackable() {
            val topic = content
            val lineHeight = 40
            mainButtonContent = TextElementImpl(text(), fill = mainColor(), font = defaultFont, lineHeight = lineHeight)

            val mainButton = Stackable(textRectangleButton(mainButtonContent, onLongPressed = {
                vibrate()
                onOpenHyperlink("http://juliuskunze.com")
            }) {
                activeNote = topic
            }, mainButtonContent.shape.size())

            val unfoldedSubTopics = if (topic.isFolded()) listOf() else topic.getAllChildren()
            val linkButtonIfHas = if (topic.getHyperlink() == null) null else {
                val linkButtonTextElement = TextElementImpl("Link", fill = Fills.solid(Colors.blue), font = defaultFont, lineHeight = lineHeight)

                val element = textRectangleButton(linkButtonTextElement) {
                    onOpenHyperlink(topic.getHyperlink())
                }

                Stackable(element, linkButtonTextElement.shape.size())
            }
            val collapseButtonIfHas = if (topic.getAllChildren().any()) {
                val element = TextElementImpl(if (topic.isFolded()) " + " else " - ", fill = Fills.solid(Colors.gray), font = defaultFont, lineHeight = lineHeight)
                val button = textRectangleButton(element) {
                    topic.setFolded(!topic.isFolded())
                }

                Stackable(button, element.shape.size())
            } else null

            toStop()
            subElements.clearAndAddAll(unfoldedSubTopics.map { TopicElement(it as TopicImpl) })

            val indent = lineHeight

            val mainStack = horizontalStack(observableIterable(listOf(mainButton, linkButtonIfHas, collapseButtonIfHas).filterNotNull()))
            val childStack = verticalStack(observableIterable(subElements.map { it.stackable }))
            val stacks = listOf(mainStack, childStack)

            mainButtonContentHeight = mainButtonContent.shape.size().y.toDouble()

            elements.clearAndAddAll(listOf(
                    transformedElement(mainStack),
                    transformedElement(childStack, Transforms2.translation(vector(indent, -mainButtonContentHeight))))
            )

            val observer = subElements.mapObservable { it.stackable.sizeChanged }.startKeepingAllObserved { updateStackableSize() }

            toStop = {
                stacks.forEach { it.removeObservers() }
                observer.stop()
            }

            updateStackableSize()
        }

        private fun updateStackableSize() {
            // TODO: remove height Schlemian
            val newSize = stackableSize()
            stackable.size = newSize
        }

        private fun stackableSize() = vector(0, mainButtonContentHeight + subElements.map { it.stackable.size.y.toDouble() }.sum())
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

fun TopicImpl.dispatchIsActiveChanged() {
    getCoreEventSupport().dispatch(this, CoreEvent(this, IsActiveChangedCoreEventType, null))
}

private val IsActiveChangedCoreEventType = "isActive"