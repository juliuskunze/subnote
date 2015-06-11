package com.mindforge.app

import com.mindforge.graphics.*
import com.mindforge.graphics.interaction.*
import com.mindforge.graphics.math.Rectangle
import com.mindforge.graphics.math.Shape
import com.mindforge.graphics.math.rectangle
import org.xmind.core.Core
import org.xmind.core.ITopic
import org.xmind.core.IWorkbook
import org.xmind.core.event.CoreEvent
import org.xmind.core.internal.dom.TopicImpl
import java.util.HashMap
import kotlin.properties.Delegates

class Shell(val screen: Screen,
            val pointers: ObservableIterable<PointerKeys>,
            val keys: ObservableIterable<Key>,
            val defaultFont: Font,
            val workbook: IWorkbook,
            val onOpenHyperlink: (String) -> Unit,
            val textChanged: Observable<String>,
            val noteLinkChanged: Observable<NoteLink>,
            val onActiveTopicChanged: (ITopic?) -> Unit,
            val newNote: Trigger<String>,
            val newSubnote: Trigger<String>,
            val removeNote: Trigger<Unit>,
            val vibrate: () -> Unit
) {
    val topicElementCache = HashMap<ITopic, TopicElement>()

    fun cachedElementOrAdd(topic: TopicImpl) = topicElementCache.getOrPut(topic, { TopicElement(topic) })

    fun lineHeight(nestingLevel: Int) = when (nestingLevel) {
        0 -> 72
        1 -> 52
        else -> 40
    }

    val indent = 40

    val infinity = 5000000

    private var activeNote by Delegates.observed<TopicImpl>(workbook.getPrimarySheet().getRootTopic() as TopicImpl, { old, new ->
        old.dispatchIsActiveChanged()
        new.dispatchIsActiveChanged()

        onActiveTopicChanged(new)
    })

    val draggedElements = observableArrayListOf<TransformedElement<*>>()
    val draggable = Draggable(composed(draggedElements))

    class DropInfo(val dragged: TopicImpl, val newParent: TopicImpl, val newSubIndex: Int)

    fun updateByDragLocation(dragged: TopicImpl) {
        val dropInfoIfChanged = rootElement.dropInfoIfChanged(dragged, draggable.dragLocation)
        if (dropInfoIfChanged != null) {
            dropInfo = dropInfoIfChanged
        }
    }

    var dropInfo: DropInfo? by Delegates.observed<DropInfo?>(null, { old, new ->
        if(old?.newParent !== new?.newParent || old?.newSubIndex != new?.newSubIndex) {
            old?.newParent?.dispatchDragDropPreviewChanged()
            new?.newParent?.dispatchDragDropPreviewChanged()
        }
    })

    fun startDrag(dragged: TopicImpl, dragLocation: Vector2, pointerKey: PointerKey) {
        draggable.dragLocation = dragLocation
        dragged.setFolded(true)

        updateByDragLocation(dragged)

        val draggedObserver = draggable.moved addObserver {
            updateByDragLocation(dragged)
        }

        draggable.dropped addObserver {
            stop()
            draggedObserver.stop()

            performDrop(dragged)
        }

        draggable.startDrag(pointerKey)
    }

    private fun performDrop(dragged: TopicImpl) {
        draggedElements.clear()
        val d = dropInfo!!
        dropInfo = null
        if (dragged !== d.newParent) {
            val oldParent = dragged.getParent()
            val oldIndex = dragged.getIndex()
            oldParent.remove(dragged)
            val correctedNewChildIndex = if (oldParent == d.newParent && oldIndex < d.newSubIndex)
                d.newSubIndex - 1 else
                d.newSubIndex
            d.newParent.add(dragged, correctedNewChildIndex)

            dragged.dispatchDropped()
        }
    }

    var root = workbook.getPrimarySheet().getRootTopic() as TopicImpl
    val rootElement : TopicElement get() = cachedElementOrAdd(root)

    fun changeDisplayedRootTopicElement(newRoot: TopicImpl) {
        root = newRoot
        setMainContent()

        fun refresh(t: TopicElement) {
            t.initElementsAndStackable()
            t.childElementsIfUnfolded().forEach { refresh(it) }
        }

        refresh(cachedElementOrAdd(newRoot))
    }

    val ancestorsLineHeight = lineHeight(1)

    private fun ancestorHeadlineElements() = root.ancestors().flatMap {
        fun stackable(text: String = it.getTitleText(), color: Color = Colors.black) : Stackable {
            val textElement = TextElementImpl(text, fill = Fills.solid(color), font = defaultFont, lineHeight = ancestorsLineHeight)
            return Stackable(textRectangleButton(textElement) {
                changeDisplayedRootTopicElement(it as TopicImpl)
            }, shape = textElement.shape.boxWithBorder())
        }

        listOf(stackable(), stackable(" > ", Colors.gray))
    }

    private fun parentHeadline() = composed(observableArrayListOf<TransformedElement<*>>(
            transformedElement(horizontalStack(observableIterable(ancestorHeadlineElements())), Transforms2.translation(vector(0, -defaultFont.shape(" ", ancestorsLineHeight).boxWithBorder().size.y.toDouble())))
    ))

    private fun mainContent() = composed(
            observableArrayListOf(
                    transformedElement(parentHeadline(), Transforms2.translation(-screen.shape.halfSize.xComponent() + screen.shape.halfSize.yComponent())),
                    transformedElement(Scrollable(
                            composed(observableArrayListOf(
                                    transformedElement(draggable),
                                    transformedElement(rootElement)
                            )))
                    )
            )
    )


    private fun initializeNewNote(newNote: ITopic, text: String) {
        newNote.setTitleText(text)
        newNote.getParent().setFolded(false)

        activeNote = newNote as TopicImpl
    }

    init {
        textChanged addObserver {
            activeNote.setTitleText(it)
        }

        noteLinkChanged addObserver {
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
                initializeNewNote(newNote, text = it)
        }

        newSubnote addObserver {
            val newNote = workbook.createTopic()
            activeNote.add(newNote)

                initializeNewNote(newNote, text = it)
        }

        removeNote addObserver {
            val parentIfHas = activeNote.getParent()
            if (parentIfHas != null) {
                parentIfHas.remove(activeNote)
                activeNote = parentIfHas as TopicImpl
            } else {
                activeNote.getAllChildren().forEach { activeNote.remove(it) }
            }
        }

        setMainContent()

        registerInputs()
    }

    private fun setMainContent() {
        screen.content = mainContent()
    }

    inner class TopicElement(topic: TopicImpl) : Composed<ITopic> {
        override val content = topic
        override val changed = trigger<Unit>()
        override val elements = ObservableArrayList<TransformedElement<*>>()
        private var stackable = Stackable(this, rectangle(zeroVector2))
        private val subStackables = ObservableArrayList<Stackable>()
        private var toStop = {}

        inner class MainLine(
                val buttonContent: TextElementImpl,
                val stack: Stack
        ) {
            fun height() = buttonContent.shape.boxWithBorder().size.y.toDouble()
            fun transformedStack() = transformedElement(stack, Transforms2.translation(vector(0, -height())))
            fun heightSlice() = rectangle(vector(infinity, height()), quadrant = 4).transformed(Transforms2.translation(vector(-infinity / 2, 0)))
        }

        private var mainLine: MainLine by Delegates.notNull()
        private var subStack: Stack by Delegates.notNull()

        init {
            initElementsAndStackable()

            val eventTypes = listOf(Core.TopicAdd, Core.TopicRemove, Core.TopicFolded, Core.TopicHyperlink, Core.TopicNotes, CoreEventTypeExtensions.dragDropPreviewChanged, CoreEventTypeExtensions.dropped)
            eventTypes.forEach { content.registerCoreEventListener(it) { initElementsAndStackable() } }

            content.registerCoreEventListener(CoreEventTypeExtensions.isActiveChanged) {
                mainLine.buttonContent.fill = mainColor()
            }

            content.registerCoreEventListener(Core.TitleText) {
                mainLine.buttonContent.content = text()
                updateStackableShape()
            }
        }

        private fun mainColor() = Fills.solid(if (activeNote == content) Colors.red else Colors.black)
        private fun text() = content.getTitleText()

        fun initElementsAndStackable() {
            toStop()

            mainLine = mainLine()
            subStackables.clearAndAddAll(subElements())
            subStack = verticalStack(observableIterable(subStackables), align = false)

            elements.clearAndAddAll(listOf(
                    mainLine.transformedStack(),
                    transformedElement(subStack, subStackTransform()))
            )

            val observer = subStackables.mapObservable { it.shapeChanged }.startKeepingAllObserved { updateStackableShape() }

            toStop = {
                listOf(mainLine.stack, subStack).forEach { it.removeObservers() }
                observer.stop()
            }

            updateStackableShape()
        }

        val nestingLevel: Int get() = content.getPath().toTopicList().count() - root.getPath().toTopicList().count()
        val lineHeight : Int get() = this@Shell.lineHeight(nestingLevel)
        val subLineHeight: Int get() = this@Shell.lineHeight(nestingLevel + 1)

        fun childElementsIfUnfolded() = (if (content.isFolded()) kotlin.listOf() else content.getAllChildren()).
                map { cachedElementOrAdd(it as TopicImpl) }

        private fun subElements(): List<Stackable> {
            val dropPlaceholderIfHas = dropPlaceHolderIfHas()

            val childTopicsIfUnfolded = childElementsIfUnfolded().map { it.stackable }

            return if (dropPlaceholderIfHas == null || content.isFolded()) childTopicsIfUnfolded else {
                val list = childTopicsIfUnfolded.toArrayList()
                list.add(dropInfo!!.newSubIndex, dropPlaceholderIfHas)
                list
            }
        }

        private fun dropPlaceHolderIfHas(): Stackable? {
            val d = dropInfo
            return if (d == null) null else if (d.newParent != content) null else {
                val textShape = TextElementImpl(d.dragged.getTitleText(), fill = mainColor(), font = defaultFont, lineHeight = subLineHeight).shape
                Stackable(coloredElement(textShape.box(), fill = Fills.solid(Colors.gray(0.8))), textShape.boxWithBorder())
            }
        }

        private fun mainLine(): MainLine {
            val mainButtonContent = TextElementImpl(text(), fill = mainColor(), font = defaultFont, lineHeight = lineHeight)

            val topic = content

            var mainButton: Stackable? = null
            mainButton = Stackable(textRectangleButton(mainButtonContent, onLongPressed = {
                vibrate()

                val draggedMainLine = mainLine()
                val stack = draggedMainLine.stack
                draggedElements.add(transformedElement(stack, Transforms2.translation(-draggedMainLine.buttonContent.shape.size() / 2)))
                val elementToRoot = rootElement.totalTransform(mainButton!!.element).inverse()
                val pointerKeyRelativeToRoot = it.transformed(elementToRoot)

                this@Shell.startDrag(dragged = content, dragLocation = pointerKeyRelativeToRoot.pointer.location, pointerKey = pointerKeyRelativeToRoot)
            }, onDoubleClick = {
                changeDisplayedRootTopicElement(content)
            }) {
                activeNote = topic
            }, mainButtonContent.shape.boxWithBorder())

            val linkButtonIfHas = if (topic.getHyperlink() == null) null else {
                val linkButtonTextElement = TextElementImpl("Link", fill = Fills.solid(Colors.blue), font = defaultFont, lineHeight = lineHeight)

                val element = textRectangleButton(linkButtonTextElement) {
                    onOpenHyperlink(topic.getHyperlink())
                }

                Stackable(element, linkButtonTextElement.shape.boxWithBorder())
            }

            val d = dropInfo

            val isDropLocation = d != null && d.newParent == this.content
            val collapseButtonIfHas = if (topic.getAllChildren().any() || isDropLocation) {
                val isDropLocationAndFolded = isDropLocation && content.isFolded()
                val color = if (isDropLocationAndFolded) Colors.red else Colors.gray
                val element = TextElementImpl(if (topic.isFolded()) " + " else " - ", fill = Fills.solid(color), font = defaultFont, lineHeight = lineHeight)
                val button = textRectangleButton(element) {
                    topic.setFolded(!topic.isFolded())
                }

                Stackable(button, element.shape.boxWithBorder())
            } else null

            val mainStack = horizontalStack(observableIterable(listOf(mainButton, linkButtonIfHas, collapseButtonIfHas).filterNotNull()))
            return MainLine(buttonContent = mainButtonContent, stack = mainStack)
        }

        private fun updateStackableShape() {
            stackable.shape = stackableShape()
        }

        // TODO remove height Schlemian:
        private fun stackableShape() = rectangle(vector(infinity, mainLine.height() + subStack.length()), quadrant = 4)

        private fun subStackTransform() = Transforms2.translation(vector(indent, -mainLine.height()))
        private fun totalHeightSlice() = stackableShape().transformed(Transforms2.translation(vector(-infinity / 2, 0)))

        private fun subStackShape() = rectangle(vector(infinity, subStack.length()), quadrant = 4)
        private fun halfPlaneWhereDropOnSubCreatesSubSubnote() = object : Shape {
            override fun contains(location: Vector2) = location.x.toDouble() > 3 * indent
        }

        fun dropInfoIfChanged(dragged: TopicImpl, dropLocation: Vector2): DropInfo? {
            fun dropAsFirstChild() = DropInfo(dragged = dragged, newParent = content, newSubIndex = 0)
            fun dropHereAsLastChild() = DropInfo(dragged = dragged, newParent = content, newSubIndex = content.getAllChildren().count())
            fun dropAboveAsSibling(topic: TopicImpl) = DropInfo(dragged = dragged, newParent = topic.getParent() as TopicImpl, newSubIndex = topic.getIndex() + 1)
            fun locationRelativeTo(child: TransformedElement<*>) = (subStackTransform() before child.transform).inverse()(dropLocation)
            fun placeHolderUnchanged() = null

            val isInMainLineHeight = mainLine.heightSlice().contains(dropLocation)
            val isInChildHalfPlane = halfPlaneWhereDropOnSubCreatesSubSubnote().contains(dropLocation)
            val childInHeight = subStack.elements.filter {
                val element = it.element
                element is TopicElement && element.totalHeightSlice().contains(locationRelativeTo(it))
            }.singleOrNull()

            return if(isInChildHalfPlane && isInMainLineHeight) dropAsFirstChild()
            else if(childInHeight == null && subStackShape().contains(dropLocation)) placeHolderUnchanged()
            else if(childInHeight == null) dropHereAsLastChild()
            else if (isInChildHalfPlane) (childInHeight.element as TopicElement).dropInfoIfChanged(dragged, locationRelativeTo(childInHeight))
            else dropAboveAsSibling((childInHeight.element as TopicElement).content)
        }
    }

    fun registerInputs() {
        pointers mapObservable { it.pressed } startKeepingAllObserved { pk ->
            loop@ for (it in screen.elementsAt(pk.pointer.location)) {
                val element = it.element
                val pointerKey = pointerKey(pk.pointer transformed it.transform, pk.key)
                when (element) {
                    is PointersElement<*> -> {
                        element.onPointerKeyPressed(pointerKey)

                        break@loop
                    }
                }
            }
        }
        pointers mapObservable { it.pointer.moved } startKeepingAllObserved { p ->
            loop@ for (it in screen.elementsAt(p.location)) {
                val element = it.element
                val pointer = p transformed it.transform
                when (element) {
                    is PointersElement<*> -> {
                        element.onPointerMoved(pointer)
                        break@loop
                    }
                }
            }
        }
        pointers mapObservable { it.released } startKeepingAllObserved { pk ->
            loop@ for (it in screen.elementsAt(pk.pointer.location)) {
                val element = it.element
                val pointerKey = pointerKey(pk.pointer transformed it.transform, pk.key)
                when (element) {
                    is PointersElement<*> -> {
                        element.onPointerKeyReleased(pointerKey)
                        break@loop
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

fun TextShape.boxWithBorder(): Rectangle {
    val box = box()
    val newSize = box.size + vector(0, 0.5 * this.lineHeight.toDouble())
    return rectangle(newSize).translated(box.center)
}

fun TopicImpl.dispatchIsActiveChanged() {
    dispatchEvent(CoreEventTypeExtensions.isActiveChanged)
}

fun TopicImpl.dispatchDragDropPreviewChanged() {
    dispatchEvent(CoreEventTypeExtensions.dragDropPreviewChanged)
}

fun TopicImpl.dispatchDropped() {
    dispatchEvent(CoreEventTypeExtensions.dragDropPreviewChanged)
}

fun TopicImpl.dispatchEvent(type: String) {
    getCoreEventSupport().dispatch(this, CoreEvent(this, type, null))
}

fun ITopic.add(child: ITopic, index: Int) {
    add(child, index, ITopic.ATTACHED)
}

fun ITopic.childrenRecursively(): List<ITopic> = getAllChildren().flatMap { listOf(it) + it.childrenRecursively() }

fun ITopic.ancestors(): List<ITopic> {
    val parent = getParent()
    return if (parent != null) parent.ancestors() + listOf(parent) else emptyList()
}

private object CoreEventTypeExtensions {
    val isActiveChanged = "isActive"
    val dragDropPreviewChanged = "dragDropPreview"
    val dropped = "dropped"
}
