package com.mindforge.app

import android.os.AsyncTask
import com.evernote.auth.EvernoteAuth
import com.evernote.auth.EvernoteService
import com.evernote.clients.ClientFactory
import com.evernote.edam.error.EDAMErrorCode
import com.evernote.edam.error.EDAMSystemException
import com.evernote.edam.error.EDAMUserException
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.type.*
import com.evernote.thrift.transport.TTransportException
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xmind.core.IWorkbook
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import org.xml.sax.InputSource
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory

fun NodeList.toList() = this.getLength().indices.map { this.item(it) }

fun Note.plainContent() : String {
    val document = xmlDocument(this.getContent())

    return getTitle() + " " + document.getLastChild().getChildNodes().toList().map {it.getTextContent()}.join("\n")
}

fun xmlDocument(xml: String): Document {
    val xmlStream = InputSource()
    xmlStream.setCharacterStream(StringReader(xml))

    return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlStream)
}

class EvernoteAsyncImporter(val workbookBuilder: WorkbookBuilderImpl, val onReady: (IWorkbook) -> Unit) : AsyncTask<String, Unit, IWorkbook?>() {
    private var exception: Exception? = null

    protected override fun doInBackground(vararg urls: String) =
            try {
                EvernoteImporter(workbookBuilder).mindMap()
            } catch (e: Exception) {
                exception = e
                null
            }

    protected override fun onPostExecute(feed: IWorkbook?) {
        if(exception != null) throw exception!!
        onReady(feed!!)
    }
}

public class EvernoteImporter(val workbookBuilder: WorkbookBuilderImpl) {
    private val token = "S=s1:U=90c9c:E=1547ab98900:C=14d23085c20:P=1cd:A=en-devtoken:V=2:H=d01340fe78ef6a5f5f74c1b2d56d30fd" //System.getenv("AUTH_TOKEN") ?:

    private var newNoteGuid: String? = null
    val factory = ClientFactory(EvernoteAuth(EvernoteService.SANDBOX, token))
    private val userStore = factory.createUserStoreClient()

    init {
        val versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)", com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR, com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR)

        if (!versionOk) {
            throw UnsupportedOperationException("Incompatible Evernote client protocol version")
        }
    }

    private val noteStore = factory.createNoteStoreClient()

    fun mindMap(): IWorkbook {
        val workbook = workbookBuilder.createWorkbook()
        val sheet = workbook.getPrimarySheet()
        val rootTopic = sheet.getRootTopic()
        rootTopic.setTitleText("From Evernote")

        for (notebook in noteStore.listNotebooks()) {
            val filter = NoteFilter()
            filter.setNotebookGuid(notebook.getGuid())
            filter.setOrder(NoteSortOrder.CREATED.getValue())
            filter.setAscending(true)

            val notes = noteStore.findNotes(filter, 0, 100).getNotes()

            val notebookNode = workbook.createTopic()

            rootTopic.add(notebookNode)
            notebookNode.setTitleText(notebook.getName())

            for (note in notes) {
                //https://discussion.evernote.com/topic/8940-null-notegetcontent/
                note.setContent(noteStore.getNoteContent(note.getGuid()))

                val noteNode = workbook.createTopic()
                notebookNode.add(noteNode)
                noteNode.setTitleText(note.plainContent())
            }
        }

        return workbook
    }

    /**
     * Create a new note containing a little text and the Evernote icon.
     */
    private fun createNote() {
        // To create a new note, simply create a new Note object and fill in
        // attributes such as the note's title.
        val note = Note()
        note.setTitle("Test note from EDAMDemo.java")

        val fileName = "enlogo.png"
        val mimeType = "image/png"

        // To include an attachment such as an image in a note, first create a
        // Resource
        // for the attachment. At a minimum, the Resource contains the binary
        // attachment
        // data, an MD5 hash of the binary data, and the attachment MIME type.
        // It can also
        // include attributes such as filename and location.
        val resource = Resource()
        resource.setData(readFileAsData(fileName))
        resource.setMime(mimeType)
        val attributes = ResourceAttributes()
        attributes.setFileName(fileName)
        resource.setAttributes(attributes)

        // Now, add the new Resource to the note's list of resources
        note.addToResources(resource)

        // To display the Resource as part of the note's content, include an
        // <en-media>
        // tag in the note's ENML content. The en-media tag identifies the
        // corresponding
        // Resource using the MD5 hash.
        val hashHex = bytesToHex(resource.getData().getBodyHash())

        // The content of an Evernote note is represented using Evernote Markup
        // Language
        // (ENML). The full ENML specification can be found in the Evernote API
        // Overview
        // at http://dev.evernote.com/documentation/cloud/chapters/ENML.php
        val content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" + "<en-note>" + "<span style=\"color:green;\">Here's the Evernote logo:</span><br/>" + "<en-media type=\"image/png\" hash=\"" + hashHex + "\"/>" + "</en-note>"
        note.setContent(content)

        // Finally, send the new note to Evernote using the createNote method
        // The new Note object that is returned will contain server-generated
        // attributes such as the new note's unique GUID.
        val createdNote = noteStore.createNote(note)
        newNoteGuid = createdNote.getGuid()

        System.out.println("Successfully created a new note with GUID: " + newNoteGuid)
        System.out.println()
    }

    /**
     * Search a user's notes and display the results.
     */
    private fun searchNotes() {
        // Searches are formatted according to the Evernote search grammar.
        // Learn more at
        // http://dev.evernote.com/documentation/cloud/chapters/Searching_notes.php

        // In this example, we search for notes that have the term "EDAMDemo" in
        // the title.
        // This should return the sample note that we created in this demo app.
        val query = "intitle:EDAMDemo"

        // To search for notes with a specific tag, we could do something like
        // this:
        // String query = "tag:tagname";

        // To search for all notes with the word "elephant" anywhere in them:
        // String query = "elephant";

        val filter = NoteFilter()
        filter.setWords(query)
        filter.setOrder(NoteSortOrder.UPDATED.getValue())
        filter.setAscending(false)

        // Find the first 50 notes matching the search
        System.out.println("Searching for notes matching query: " + query)
        val notes = noteStore.findNotes(filter, 0, 50)
        System.out.println("Found " + notes.getTotalNotes() + " matching notes")

        val iter = notes.getNotesIterator()
        while (iter.hasNext()) {
            val note = iter.next()
            System.out.println("Note: " + note.getTitle())

            // Note objects returned by findNotes() only contain note attributes
            // such as title, GUID, creation date, update date, etc. The note
            // content
            // and binary resource data are omitted, although resource metadata
            // is included.
            // To get the note content and/or binary resources, call getNote()
            // using the note's GUID.
            val fullNote = noteStore.getNote(note.getGuid(), true, true, false, false)
            System.out.println("Note contains " + fullNote.getResourcesSize() + " resources")
            System.out.println()
        }
    }

    /**
     * Update the tags assigned to a note. This method demonstrates how only
     * modified fields need to be sent in calls to updateNote.
     */
    private fun updateNoteTag() {
        // When updating a note, it is only necessary to send Evernote the
        // fields that have changed. For example, if the Note that you
        // send via updateNote does not have the resources field set, the
        // Evernote server will not change the note's existing resources.
        // If you wanted to remove all resources from a note, you would
        // set the resources field to a new List<Resource> that is empty.

        // If you are only changing attributes such as the note's title or tags,
        // you can save time and bandwidth by omitting the note content and
        // resources.

        // In this sample code, we fetch the note that we created earlier,
        // including
        // the full note content and all resources. A real application might
        // do something with the note, then update a note attribute such as a
        // tag.
        var note = noteStore.getNote(newNoteGuid, true, true, false, false)

        // Do something with the note contents or resources...

        // Now, update the note. Because we're not changing them, we unset
        // the content and resources. All we want to change is the tags.
        note.unsetContent()
        note.unsetResources()

        // We want to apply the tag "TestTag"
        note.addToTagNames("TestTag")

        // Now update the note. Because we haven't set the content or resources,
        // they won't be changed.
        noteStore.updateNote(note)
        System.out.println("Successfully added tag to existing note")

        // To prove that we didn't destroy the note, let's fetch it again and
        // verify that it still has 1 resource.
        note = noteStore.getNote(newNoteGuid, false, false, false, false)
        System.out.println("After update, note has " + note.getResourcesSize() + " resource(s)")
        System.out.println("After update, note tags are: ")
        for (tagGuid in note.getTagGuids()) {
            val tag = noteStore.getTag(tagGuid)
            System.out.println("* " + tag.getName())
        }

        System.out.println()
    }

    companion object {
        /**
         * Helper method to read the contents of a file on disk and create a new Data
         * object.
         */
        private fun readFileAsData(fileName: String): Data {
            val filePath = File(javaClass<EvernoteImporter>().getResource(javaClass<EvernoteImporter>().getCanonicalName() + ".class").getPath()).getParent() + File.separator + fileName
            // Read the full binary contents of the file
            val `in` = FileInputStream(filePath)
            val byteOut = ByteArrayOutputStream()
            val block = ByteArray(10240)
            while (true) {
                val len = `in`.read(block)

                if (len < 0) break

                byteOut.write(block, 0, len)
            }
            `in`.close()
            val body = byteOut.toByteArray()

            // Create a new Data object to contain the file contents
            val data = Data()
            data.setSize(body.size())
            data.setBodyHash(MessageDigest.getInstance("MD5").digest(body))
            data.setBody(body)

            return data
        }

        /**
         * Helper method to convert a byte array to a hexadecimal string.
         */
        public fun bytesToHex(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (hashByte in bytes) {
                val byte = hashByte.toInt()
                val intVal = if (byte < 0) byte + 256 else byte
                if (intVal < 16) {
                    sb.append('0')
                }
                sb.append(Integer.toHexString(intVal))
            }
            return sb.toString()
        }
    }
}