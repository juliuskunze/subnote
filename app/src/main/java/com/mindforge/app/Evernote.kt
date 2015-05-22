package com.mindforge.app

import com.evernote.client.android.AsyncNoteStoreClient
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.OnClientCallback
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.notestore.NoteList
import com.evernote.edam.type.NoteSortOrder
import com.evernote.edam.type.Notebook
import org.xmind.core.ITopic

object Evernote {
    val consumerKey = "stefan3291";
    val consumerSecret = "6636ba859ab683ec";
    val evernoteService = EvernoteSession.EvernoteService.SANDBOX;
    val linkPrefix = "https://sandbox.evernote.com/Home.action#b="
    fun isEvernoteLink(url: String) = url.startsWith(linkPrefix)
    fun extractNotebookGuid(url: String) = url.trimLeading(linkPrefix).split("&").first()
}

fun Notebook.getWebUrl() = "${Evernote.linkPrefix}${getGuid()}&sh=1&"

class EvernoteLink(val notebook: Notebook, val noteStore: AsyncNoteStoreClient) : NodeLink(LinkType.Evernote, notebook.getWebUrl()) {
    override fun updateTopic(topic: ITopic) {
        super.updateTopic(topic)
        topic.setTitleText(notebook.getName())
        topic.getAllChildren().forEach { topic.remove(it) }
        val filter = NoteFilter()
        filter.setNotebookGuid(notebook.getGuid())
        filter.setOrder(NoteSortOrder.CREATED.getValue())
        filter.setAscending(true)
        noteStore.findNotes(filter, 0, 1024, object : OnClientCallback<NoteList>() {
            override fun onSuccess(noteList: NoteList) {
                noteList.getNotes().forEach { note ->
                    topic.getOwnedWorkbook().createTopic().let { child ->
                        child.setTitleText("loading...")
                        topic.add(child)
                        noteStore.getNoteContent(note.getGuid(), object : OnClientCallback<String>() {
                            override fun onSuccess(content: String) {
                                note.setContent(content)
                                child.setTitleText(note.plainContent())
                            }
                            override fun onException(ex: Exception) {
                                ex.printStackTrace()
                            }
                        })
                    }
                }
            }

            override fun onException(ex: Exception) {
                ex.printStackTrace()
            }
        })
    }
}