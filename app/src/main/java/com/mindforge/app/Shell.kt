package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.rectangle
import com.mindforge.graphics.math.topLeftAtOrigin
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

    private var activeNote by Delegates.observed<TopicImpl>(workbook.getPrimarySheet().getRootTopic() as TopicImpl, { old, new ->
        old.dispatchIsActiveChanged()
        new.dispatchIsActiveChanged()

        onActiveTopicChanged(new)
    })

    val draggedElements = observableArrayListOf<TransformedElement<*>>()
    val draggable = Draggable(composed(draggedElements))

    class DropInfo(val newParent: TopicImpl, val newChildIndex: Int)

    fun updateByDragLocation() {
        val dropInfoIfChanged = rootTopicElement.dropInfoIfChanged(draggable.dragLocation)
        if(dropInfoIfChanged != null) {
            dropInfo = dropInfoIfChanged
        }
    }

    var dropInfo: DropInfo? by Delegates.observed<DropInfo?>(null, { old, new ->
        if(old?.newParent !== new?.newParent || old?.newChildIndex != new?.newChildIndex) {
            old?.newParent?.dispatchDragDropPreviewChanged()
            new?.newParent?.dispatchDragDropPreviewChanged()
        }
    })

    fun startDrag(dragged: TopicImpl, dragLocation: Vector2, pointerKey: PointerKey) {
        draggable.dragLocation = dragLocation
        dragged.setFolded(true)

        updateByDragLocation()

        val draggedObserver = draggable.moved addObserver {
            updateByDragLocation()
        }

        draggable.dropped addObserver {
            draggedObserver.stop()
            stop()

            draggedElements.clear()
            val d = dropInfo!!
            dropInfo = null
            if(dragged !== d.newParent) {
                val oldParent = dragged.getParent()
                val oldIndex = dragged.getIndex()
                oldParent.remove(dragged)
                val correctedNewChildIndex = if (oldParent == d.newParent && oldIndex < d.newChildIndex)
                    d.newChildIndex - 1 else
                    d.newChildIndex
                d.newParent.add(dragged, correctedNewChildIndex)
            }
        }

        draggable.startDrag(pointerKey)
    }

    val rootTopicElement = TopicElement(workbook.getPrimarySheet().getRootTopic() as TopicImpl)
    private val mainElements = observableArrayListOf(
            transformedElement(draggable),
            transformedElement(rootTopicElement)
    )
    val mainContent = Scrollable(composed(mainElements))

    private fun initializeNewNote(newNote: ITopic) {
        newNote.setTitleText("new note")
        newNote.getParent().setFolded(false)

        activeNote = newNote as TopicImpl
    }

    init {
        textChanged addObserver {
            activeNote.setTitleText(it)
        }

        nodeLinkChanged addObserver {
            activeNote.setHyperlink(it.url)
            it.updateTopic(activeNote)
        }

        newNote addObserver {
                val newNote = workbook.createTopic()

                val parentIfHas = activeNote.getParent()
                if (parentIfHas != null) {
                    parentIfHas.add(newNote, activeNote.getIndex() + 1)
                } else {
                    activeNote.add(newNote)
                }
                initializeNewNote(newNote)
        }

        newSubnote addObserver {
                val newNote = workbook.createTopic()
                activeNote.add(newNote)

                initializeNewNote(newNote)
        }

        removeNode addObserver {
                val parentIfHas = activeNote.getParent()
                if (parentIfHas != null) {
                    parentIfHas.remove(activeNote)
                    activeNote = parentIfHas as TopicImpl
                } else {
                    activeNote.getAllChildren().forEach { activeNote.remove(it) }
                }
        }

        screen.content = mainContent

        registerInputs()
    }

    inner class TopicElement(topic: TopicImpl) : Composed<ITopic> {
        override val content = topic
        override val changed = trigger<Unit>()
        override val elements = ObservableArrayList<TransformedElement<*>>()
        private var stackable = Stackable(this, rectangle(zeroVector2).translated())
        private val subStackables = ObservableArrayList<Stackable>()
        private var toStop = {}

        inner class MainLine(
                val buttonContent: TextElementImpl,
                val stack: Stack
        ) {
            val height =  buttonContent.shape.size().y.toDouble()

            fun stackTransform() = Transforms2.translation(vector(0, -height))
            fun transformedStack() = transformedElement(stack, stackTransform())
            fun heightSlice() = rectangle(vector(infinity, height)).topLeftAtOrigin().transformed(Transforms2.translation(vector(-infinity/2, 0)))
        }

        private var mainLine: MainLine by Delegates.notNull()
        private var subStack: Stack by Delegates.notNull()

        init {
            initElementsAndStackable()

            val eventTypes = listOf(Core.TopicAdd, Core.TopicRemove, Core.TopicFolded, Core.TopicHyperlink, Core.TopicNotes, CoreEventTypeExtensions.dragDropPreviewChanged)
            eventTypes.forEach { content.registerCoreEventListener(it) { initElementsAndStackable() } }

            content.registerCoreEventListener(CoreEventTypeExtensions.isActiveChanged) {
                mainLine.buttonContent.fill = mainColor()
            }

            content.registerCoreEventListener(Core.TitleText) {
                mainLine.buttonContent.content = text()
            }

        }

        private fun mainColor() = Fills.solid(if (activeNote == content) Colors.red else Colors.black)
        private fun text() = content.getTitleText()

        private fun initElementsAndStackable() {
            toStop()

            mainLine = mainLine()
            subStackables.clearAndAddAll(subElements())
            subStack = verticalStack(observableIterable(subStackables))

            elements.clearAndAddAll(listOf(
                    mainLine.transformedStack(),
                    transformedElement(subStack, childStackTransform()))
            )

            val observer = subStackables.mapObservable { it.shapeChanged }.startKeepingAllObserved { updateStackableSize() }

            toStop = {
                listOf(mainLine.stack, subStack).forEach { it.removeObservers() }
                observer.stop()
            }

            updateStackableSize()
        }

        private fun subElements(): List<Stackable> {
            val dropPlaceholderIfHas = dropPlaceHolderIfHas()

            val childTopicsIfUnfolded = (if (content.isFolded()) listOf() else content.getAllChildren()).
                    map { TopicElement(it as TopicImpl).stackable }

            return if (dropPlaceholderIfHas == null || content.isFolded()) childTopicsIfUnfolded else {
                val list = childTopicsIfUnfolded.toArrayList()
                list.add(dropInfo!!.newChildIndex, dropPlaceholderIfHas)
                list
            }
        }

        private fun dropPlaceHolderIfHas(): Stackable? {
            val d = dropInfo
            return if (d == null) null else if (d.newParent != content) null else {
                val s = TextElementImpl(" ", fill = mainColor(), font = defaultFont, lineHeight = lineHeight)
                Stackable(s, s.shape.box())
            }

        }

        private fun mainLine(): MainLine {
            val mainButtonContent = TextElementImpl(text(), fill = mainColor(), font = defaultFont, lineHeight = lineHeight)

            val topic = content

            val mainButton = Stackable(textRectangleButton(mainButtonContent, onLongPressed = {
                vibrate()
                startDrag(it)
            }) {
                activeNote = topic
            }, mainButtonContent.shape.box())

            val linkButtonIfHas = if (topic.getHyperlink() == null) null else {
                val linkButtonTextElement = TextElementImpl("Link", fill = Fills.solid(Colors.blue), font = defaultFont, lineHeight = lineHeight)

                val element = textRectangleButton(linkButtonTextElement) {
                    onOpenHyperlink(topic.getHyperlink())
                }

                Stackable(element, linkButtonTextElement.shape.box())
            }
            val collapseButtonIfHas = if (topic.getAllChildren().any()) {
                val element = TextElementImpl(if (topic.isFolded()) " + " else " - ", fill = Fills.solid(Colors.gray), font = defaultFont, lineHeight = lineHeight)
                val button = textRectangleButton(element) {
                    topic.setFolded(!topic.isFolded())
                }

                Stackable(button, element.shape.box())
            } else null

            val mainStack = horizontalStack(observableIterable(listOf(mainButton, linkButtonIfHas, collapseButtonIfHas).filterNotNull()))
            return MainLine(buttonContent = mainButtonContent, stack = mainStack)
        }

        private fun updateStackableSize() {
            stackable.shape = stackableShape()
        }

        // TODO remove height Schlemian:
        private fun stackableShape() = rectangle(vector(infinity, mainLine.height + childStackHeight())).topLeftAtOrigin()
        private fun childStackHeight() = subStackables.map { it.shape.original.size.y.toDouble() }.sum()
        private fun childStackTransform() = Transforms2.translation(vector(indent, -mainLine.height))
        private fun totalHeightSlice() = stackableShape().transformed(Transforms2.translation(vector(-infinity/2, 0)))

        private fun childrenStackShape() = rectangle(vector(infinity, childStackHeight())).topLeftAtOrigin()
        private fun halfPlaneWhereDropOnChildCreatesChildChildnode() = object : Shape {
            override fun contains(location: Vector2) = location.x.toDouble() > 3 * indent
        }

        private fun startDrag(pointerKey: PointerKey) {
            draggedElements.add(transformedElement(mainLine().stack))

            val elementToRoot = rootTopicElement.totalTransform(this).inverse()
            val pointerKeyRelativeToRoot = pointerKey.transformed(elementToRoot)

            this@Shell.startDrag(dragged = content, dragLocation = pointerKeyRelativeToRoot.pointer.location, pointerKey = pointerKeyRelativeToRoot)
        }

        fun dropInfoIfChanged(dropLocation: Vector2): DropInfo? {
            fun dropAsFirstChild() = DropInfo(newParent = content, newChildIndex = 0)
            fun dropHereAsLastChild() = DropInfo(newParent = content, newChildIndex = content.getAllChildren().count())
            fun dropAboveAsSibling(topic: TopicImpl) = DropInfo(newParent = topic.getParent() as TopicImpl, newChildIndex = topic.getIndex() + 1)
            fun locationRelativeTo(child: TransformedElement<*>) = (childStackTransform() before child.transform).inverse()(dropLocation)
            fun placeHolderUnchanged() = null

            val isInMainLineHeight = mainLine.heightSlice().contains(dropLocation)
            val isInChildHalfPlane = halfPlaneWhereDropOnChildCreatesChildChildnode().contains(dropLocation)
            val childInHeight = subStack.elements.filter {
                val element = it.element
                element is TopicElement && element.totalHeightSlice().contains(locationRelativeTo(it))
            }.singleOrNull()

            return if(isInChildHalfPlane && isInMainLineHeight) dropAsFirstChild()
            else if(childInHeight == null && childrenStackShape().contains(dropLocation)) placeHolderUnchanged()
            else if(childInHeight == null) dropHereAsLastChild()
            else if(isInChildHalfPlane) (childInHeight.element as TopicElement).dropInfoIfChanged(locationRelativeTo(childInHeight))
            else dropAboveAsSibling((childInHeight.element as TopicElement).content)
        }
    }

    fun registerInputs() {
        pointers mapObservable { it.pressed } startKeepingAllObserved { pk ->
            for (it in screen.elementsAt(pk.pointer.location)) {
                val element = it.element
                val pointerKey = pointerKey(pk.pointer transformed it.transform, pk.key)
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
                val pointer = p transformed it.transform
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
                val pointerKey = pointerKey(pk.pointer transformed it.transform, pk.key)
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