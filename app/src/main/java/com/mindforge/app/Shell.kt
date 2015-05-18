package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.*
import org.xmind.core.Core
import org.xmind.core.ITopic
import org.xmind.core.IWorkbook
import org.xmind.core.event.CoreEvent
import org.xmind.core.internal.dom.TopicImpl
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

    val lineHeight = 40
    val indent = lineHeight
    val infinity = 5000000

    private var activeNote by Delegates.observed<TopicImpl?>(null, { old, new ->
        old?.dispatchIsActiveChanged()
        new?.dispatchIsActiveChanged()

        onActiveTopicChanged(new)
    })

    var draggableSize = vector(100, lineHeight)
    val draggable = Draggable(coloredElement(rectangle(draggableSize), Fills.solid(Colors.red)), dragLocation = draggableSize / 2)

    class DropInfo(val newParent: TopicImpl, val newChildIndex: Int)

    fun updateByDragLocation() {
        val dropInfoIfChanged = rootTopicElement.dropInfoIfChanged(draggable.dragLocation)
        if(dropInfoIfChanged != null) {
            dragDropInfo = dropInfoIfChanged
        }
    }

    var dragDropInfo: DropInfo? by Delegates.observed<DropInfo?>(null, { old, new ->
        if(old?.newParent !== new?.newParent || old?.newChildIndex != new?.newChildIndex) {
            old?.newParent?.dispatchDragDropPreviewChanged()
            new?.newParent?.dispatchDragDropPreviewChanged()
        }
    })

    fun startDragDrop(dragged: TopicImpl, dragLocation: Vector2, pointerKey: PointerKey) {
        draggable.dragLocation = dragLocation
        dragged.setFolded(true)

        updateByDragLocation()

        val draggedObserver = draggable.moved addObserver {
            updateByDragLocation()
        }

        draggable.dropped addObserver {
            draggedObserver.stop()
            stop()

            val d = dragDropInfo!!
            if(dragged !== d.newParent) {
                val oldParent = dragged.getParent()
                val oldIndex = dragged.getIndex()
                oldParent.remove(dragged)
                val oldSelfCorrectedNewChildIndex = if(oldParent == d.newParent && oldIndex < d.newChildIndex)
                    d.newChildIndex - 1 else
                    d.newChildIndex
                d.newParent.add(dragged, oldSelfCorrectedNewChildIndex)

                dragDropInfo == null
            }
        }

        draggable.registerDragOnMove(pointerKey)
    }

    val rootTopicElement = TopicElement(workbook.getPrimarySheet().getRootTopic() as TopicImpl)
    private val mainElements = observableArrayListOf(
            transformedElement(draggable),
            transformedElement(rootTopicElement)
    )
    val mainContent = Scrollable(composed(mainElements))

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

                val parentIfHas = getParent()
                if (parentIfHas != null) {
                    parentIfHas.add(newNote, getIndex() + 1)
                } else {
                    add(newNote)
                }
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
                val parentIfHas = getParent()
                if (parentIfHas != null) {
                    parentIfHas.remove(this)
                    activeNote = parentIfHas as TopicImpl
                } else {
                    getAllChildren().forEach { remove(it) }
                }
            }
        }

        screen.content = mainContent

        registerInputs()
    }



    inner class TopicElement(topic: TopicImpl) : Composed<ITopic> {
        override val content = topic
        override val changed = trigger<Unit>()
        override val elements = ObservableArrayList<TransformedElement<*>>()
        private val stackables = ObservableArrayList<Stackable>()
        private var stackable = Stackable(this, zeroVector2)
        private var toStop = {}
        private var mainButtonContent: TextElementImpl by Delegates.notNull()
        private var mainButtonContentHeight: Double by Delegates.notNull()
        private var childStack: Stack by Delegates.notNull()

        init {
            initElementsAndStackable()

            val eventTypes = listOf(Core.TopicAdd, Core.TopicRemove, Core.TopicFolded, Core.TopicHyperlink, Core.TopicNotes, CoreEventTypeExtensions.dragDropPreviewChanged)
            eventTypes.forEach { content.registerCoreEventListener(it) { initElementsAndStackable() } }

            content.registerCoreEventListener(CoreEventTypeExtensions.isActiveChanged) {
                mainButtonContent.fill = mainColor()
            }

            content.registerCoreEventListener(Core.TitleText) {
                mainButtonContent.content = text()
            }

        }

        private fun mainColor() = Fills.solid(if (activeNote == content) Colors.red else Colors.black)
        private fun text() = content.getTitleText()

        private fun initElementsAndStackable() {
            toStop()

            val mainLineStack = mainLine()

            stackables.clearAndAddAll(subElements())
            childStack = verticalStack(observableIterable(stackables))

            val stacks = listOf(mainLineStack, childStack)

            elements.clearAndAddAll(listOf(
                    transformedElement(mainLineStack),
                    transformedElement(childStack, childStackTransform()))
            )

            val observer = stackables.mapObservable { it.sizeChanged }.startKeepingAllObserved { updateStackableSize() }

            toStop = {
                stacks.forEach { it.removeObservers() }
                observer.stop()
            }

            updateStackableSize()
        }

        private fun subElements(): List<Stackable> {
            val d = dragDropInfo
            val dragDropGap = if (d == null) null else if (d.newParent != content) null else {
                val s = TextElementImpl(" ", fill = mainColor(), font = defaultFont, lineHeight = lineHeight)
                Stackable(s, s.shape.size())
            }

            val unfoldedSubTopics = if (content.isFolded()) listOf() else content.getAllChildren()
            val unfoldedTopicStackables = unfoldedSubTopics.map { TopicElement(it as TopicImpl) }.map { it.stackable }

            return if (dragDropGap == null) unfoldedTopicStackables else {
                val list = unfoldedTopicStackables.toArrayList()
                list.add(d!!.newChildIndex, dragDropGap)
                list
            }
        }

        private fun mainLine(): Stack {
            mainButtonContent = TextElementImpl(text(), fill = mainColor(), font = defaultFont, lineHeight = lineHeight)
            mainButtonContentHeight = mainButtonContent.shape.size().y.toDouble()

            val topic = content

            val mainButton = Stackable(textRectangleButton(mainButtonContent, onLongPressed = {
                vibrate()
                startDrag(it)
            }) {
                activeNote = topic
            }, mainButtonContent.shape.size())

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

            val mainStack = horizontalStack(observableIterable(listOf(mainButton, linkButtonIfHas, collapseButtonIfHas).filterNotNull()))
            return mainStack
        }

        private fun updateStackableSize() {
            stackable.size = stackableSize()
        }

        // TODO: remove height Schlemian
        // TODO: width is random, should be related to the content:

        private fun stackableSize() = vector(infinity, mainButtonContentHeight + childStackHeight())
        private fun childStackHeight() = stackables.map { it.size.y.toDouble() }.sum()
        private fun childStackTransform() = Transforms2.translation(vector(indent, -mainButtonContentHeight))

        private fun mainLineHeightSlice() = rectangle(vector(infinity, mainButtonContentHeight)).topLeftAtOrigin().transformed(Transforms2.translation(vector(-infinity/2, 0)))
        private fun totalHeightSlice() = rectangle(stackableSize()).topLeftAtOrigin().transformed(Transforms2.translation(vector(-infinity/2, 0)))
        private fun childrenShape() = rectangle(vector(infinity, childStackHeight())).topLeftAtOrigin().transformed(Transforms2.translation(vector(indent, lineHeight)))
        private fun childrenHalfPlane() = object : Shape {
            override fun contains(location: Vector2) = location.y.toDouble() > indent
        }

        private fun startDrag(pointerKey: PointerKey) {
            val transform = rootTopicElement.totalTransform(this)
            val v = transform.matrix.column(2)
            val dragLocation = vector(v.x, v.y) + draggableSize / 2
            val pointerKeyRelativeToRoot = pointerKey.relativeTo(transform.inverse())

            this@Shell.startDragDrop(dragged = content, dragLocation = dragLocation, pointerKey = pointerKeyRelativeToRoot)
        }

        fun dropInfoIfChanged(dropLocation: Vector2): DropInfo? {
            fun dropHereAsFirst() = DropInfo(newParent = content, newChildIndex = 0)
            fun dropHereAsLast() = DropInfo(newParent = content, newChildIndex = content.getAllChildren().count())
            fun dropBelow(topic: TopicImpl) = DropInfo(newParent = topic.getParent() as TopicImpl, newChildIndex = topic.getIndex() + 1)
            fun locationRelativeTo(child: TransformedElement<*>) = childStackTransform().before(child.transform)(dropLocation)
            fun placeHolderUnchanged() = null

            val isInMainLineHeight = mainLineHeightSlice().contains(dropLocation)
            val isInChildHalfPlane = childrenHalfPlane().contains(dropLocation)
            val childInHeight = childStack.elements.filter {
                val element = it.element
                element is TopicElement && element.totalHeightSlice().contains(locationRelativeTo(it))
            }.singleOrNull()

            return if(isInChildHalfPlane && isInMainLineHeight) dropHereAsFirst()
            else if(childInHeight == null && childrenShape().contains(dropLocation)) placeHolderUnchanged()
            else if(childInHeight == null) dropHereAsLast()
            else if(isInChildHalfPlane) (childInHeight.element as TopicElement).dropInfoIfChanged(locationRelativeTo(childInHeight))
            else dropBelow((childInHeight.element as TopicElement).content)
        }
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
    dispatchEvent(CoreEventTypeExtensions.isActiveChanged)
}

fun TopicImpl.dispatchDragDropPreviewChanged() {
    dispatchEvent(CoreEventTypeExtensions.dragDropPreviewChanged)
}

fun TopicImpl.dispatchEvent(type: String) {
    getCoreEventSupport().dispatch(this, CoreEvent(this, type, null))
}

fun ITopic.add(child: ITopic, index: Int) {
    add(child, index, ITopic.ATTACHED)
}

private object CoreEventTypeExtensions {
    val isActiveChanged = "isActive"
    val dragDropPreviewChanged = "dragDropPreview"
}
