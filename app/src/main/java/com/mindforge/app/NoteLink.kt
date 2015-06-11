package com.mindforge.app

import org.xmind.core.ITopic

/**
 * Specifies the type of a linked node, i.e. how the hyperlink property is handled.
 * The idea behind this is to use an url format that allows compatibility with XMind:
 * Clicking the link there allows the user to navigate to the content that we represent as subnodes.
 */
enum class LinkType {
    /** Nothing happens... */
    None
    /** The hyperlink is a 'regular' link to a web site. */
    WebUrl
    /** The hyperlink references an Evernote notebook */
    Evernote

    //Later...
    /** The hyperlink references a Gmail inbox */
    //Gmail
}

fun ITopic.getLinkType(): LinkType = this.getHyperlink()?.let {
    when {
        it.isEmpty() -> LinkType.None
        Evernote.isEvernoteLink(it) -> LinkType.Evernote
        else -> LinkType.WebUrl
    }
} ?: LinkType.None

open class NoteLink (val linkType: LinkType, val url: String?) {
    open fun updateTopic(topic: ITopic) = topic.setHyperlink(url)
}