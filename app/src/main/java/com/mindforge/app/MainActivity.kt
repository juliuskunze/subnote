package com.mindforge.app

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewConfiguration
import com.android.vending.billing.IInAppBillingService
import com.evernote.client.android.EvernoteSession
import com.evernote.client.android.OnClientCallback
import com.evernote.edam.notestore.NoteFilter
import com.evernote.edam.notestore.NoteList
import com.evernote.edam.type.NoteSortOrder
import com.evernote.edam.type.Notebook
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveApi
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveId
import com.mindforge.graphics.android.GlFont
import com.mindforge.graphics.android.GlScreen
import com.mindforge.graphics.invoke
import com.mindforge.graphics.observableIterable
import com.mindforge.graphics.trigger
import kotlinx.android.synthetic.activity_main.mindMapLayout
import org.jetbrains.anko.browse
import org.jetbrains.anko.toast
import org.jetbrains.anko.vibrator
import org.json.JSONException
import org.json.JSONObject
import org.xmind.core.Core
import org.xmind.core.ITopic
import org.xmind.core.IWorkbook
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import org.xmind.core.internal.dom.WorkbookImpl
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.properties.Delegates

/*
Outside of MainActivity because screen rotation destroys the current MainActivity and creates a new one: http://developer.android.com/training/basics/activity-lifecycle/recreating.html
*/
object ApplicationState {
    var initialized = false
    var workbook: IWorkbook by Delegates.notNull()
}

public class MainActivity : Activity() {
    var analytics : GoogleAnalytics by Delegates.notNull()
    var tracker : Tracker by Delegates.notNull()
    val donationService : DonationService by Delegates.lazy { DonationService(this, IntentCode.donate) }

    val localWorkbookFile: File get() = File(getFilesDir(), "MindForge.xmind")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = GoogleAnalytics.getInstance(this)
        analytics.setLocalDispatchPeriod(1800)
        tracker = analytics.newTracker("UA-63277540-1")
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.setScreenName(javaClass.getSimpleName());

        setContentView(R.layout.activity_main)

        enableOverflowMenuButtonEvenIfHardwareMenuButtonExists()

        if (!ApplicationState.initialized) {
            ApplicationState.initialized = true
            openFromDeviceOrCreateDefault()
        } else {
            open(ApplicationState.workbook)
        }
    }

    /*
    (Code from http://stackoverflow.com/a/13098824/1692437)
    Why?
    1. Hardware menu button did not do anything on S3 resulting in completely inaccessible menu items.
    2. Having items split up into hardware menu and action bar is confusing.
    3. Hardware menu button is dying out anyway.
    Why is this so complicated? Questionable design decision from Google to not show overflow indicator if hardware button is available.
    */
    private fun enableOverflowMenuButtonEvenIfHardwareMenuButtonExists() {
        val viewConfig = ViewConfiguration.get(this)

        val menuKeyField = javaClass<ViewConfiguration>().getDeclaredField("sHasPermanentMenuKey")
        menuKeyField.setAccessible(true)
        menuKeyField.setBoolean(viewConfig, false)
    }

    private val textChanged = trigger<String>()
    private val noteLinkChanged = trigger<NoteLink>()
    private val newNote = trigger<String>()
    private val newSubnote = trigger<String>()
    private val removeNote = trigger<Unit>()

    fun linkNote() {
        showSelectDialog("Select link type", LinkType.values().toList()) { linkType ->
            when (linkType) {
                LinkType.None -> noteLinkChanged(NoteLink(linkType, null))
                LinkType.WebUrl -> showInputDialog("Edit Web URL", currentUrl) {
                    if (it != null) {
                        noteLinkChanged(NoteLink(linkType, it))
                        currentUrl = it
                    }
                }
                LinkType.Evernote -> {
                    withAuthenticatedEvernoteSession {
                        getClientFactory().createNoteStoreClient().listNotebooks(object : OnClientCallback<List<Notebook>>() {
                            override fun onSuccess(notebooks: List<Notebook>) {
                                showSelectDialog("Select notebook", notebooks map {
                                    object {
                                        val notebook = it
                                        override fun toString() = it.getName()
                                    }
                                }) {
                                    if (it != null) {
                                        noteLinkChanged(object : NoteLink(linkType, it.notebook.getWebUrl()) {
                                            override fun updateTopic(topic: ITopic) {
                                                topic.setTitleText(it.notebook.getName())
                                                topic.getAllChildren().forEach { topic.remove(it) }
                                                val noteStore = this@withAuthenticatedEvernoteSession.getClientFactory().createNoteStoreClient()
                                                val filter = NoteFilter()
                                                filter.setNotebookGuid(it.notebook.getGuid())
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
            }
        }
    }

    fun editNote() {
        showInputDialog("Edit Note", currentText) {
            if (it != null) {
                textChanged(it)
                currentText = it
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        trackMenuAction(item)

        return when (item.getItemId()) {
            R.id.open_xmind_from_drive -> {
                openFromDrive()
                true
            }
            R.id.newNoteButton -> {
                showInputDialog("New Note", "") {
                    if (it != null && it != "") {
                        newNote(it)
                    }
                }
                true
            }
            R.id.newSubnoteButton -> {
                showInputDialog("New Subnote", "") {
                    if (it != null && it != "") {
                        newSubnote(it)
                    }
                }
                true
            }
            R.id.removeNoteButton -> {
                removeNote()
                true
            }

            R.id.linkNoteButton -> {
                linkNote()
                true
            }
            R.id.editNoteButton -> {
                editNote()
                true
            }
            R.id.giveFeedback -> {
                giveFeedback()
                true
            }

            R.id.donate -> {
                donationService.invoke()

                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun trackMenuAction(item: MenuItem) {
        //https://developer.android.com/reference/com/google/android/gms/analytics/HitBuilders.html
        tracker.send(HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("Menu")
                .setLabel(item.getTitle().toString())
                .build()
        )
    }

    private fun giveFeedback() {
        showInputDialog("Give Feedback", "") {
            if(it != null && it != "") {
                trackFeedback(it)
                toast("Feedback sent. Thank you!")
            }
        }
    }

    private fun trackFeedback(text: String) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory("Feedback")
                .setAction("Text")
                .setLabel(text)
                .build()
        )
    }

    fun createDefaultMindMap() {
        val file = File(getCacheDir(), "temp.xmind")
        getResources().openRawResource(R.raw.start).writeToFile(file)
        open(file)
    }

    private fun openFromDeviceOrCreateDefault() {
        if (localWorkbookFile.exists()) {
            try {
                open(localWorkbookFile)
            } catch (ex: Exception) {
                ex.printStackTrace()
                localWorkbookFile.delete()
                createDefaultMindMap()
            }
        } else {
            createDefaultMindMap()
        }
    }

    private fun saveToDocuments() {
        save(localWorkbookFile)
    }

    private fun openFromDrive() {
        if (!driveFileOpenerClient.isConnected()) {
            driveFileOpenerClient.connect()

            // chooseFileFromDrive() called in onConnected()
            return
        }

        chooseFileFromDrive()
    }

    fun importFromEvernote() {
        EvernoteAsyncImporter(workbookBuilder = workbookBuilder, onReady = { open(it) }).execute()
    }

    private fun open(file: File) {
        open(workbookBuilder.loadFromFile(file))
    }

    private fun save(file: File) {
        openFileOutput(file.name, Context.MODE_PRIVATE).let {
            ApplicationState.workbook.save(it)
            it.close()
        }
    }

    private val workbookBuilder: WorkbookBuilderImpl by Delegates.lazy { AndroidWorkbookBuilder(cacheDirectory = getCacheDir())() }

    private val driveFileOpenerClient: GoogleApiClient by Delegates.lazy {
        GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(connectionHint: Bundle?) {
                chooseFileFromDrive()
            }

            override fun onConnectionSuspended(cause: Int) {
                throw UnsupportedOperationException("GoogleApiClient connection suspended")
            }
        }).addOnConnectionFailedListener(object : GoogleApiClient.OnConnectionFailedListener {
            override fun onConnectionFailed(result: ConnectionResult) {
                if (!result.hasResolution()) {
                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this@MainActivity, 0).show()
                    return
                }

                try {
                    // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
                    result.startResolutionForResult(this@MainActivity, IntentCode.googleClientResolution)
                } catch (ex: Exception) {
                    Log.e("", "Failed to connect to Play Store.")
                }
            }
        }).build()
    }

    private fun chooseFileFromDrive() {
        val intentSender = Drive.DriveApi.newOpenFileActivityBuilder().build(driveFileOpenerClient)

        startIntentSenderForResult(intentSender, IntentCode.openFileFromDrive, null, 0, 0, 0);
    }

    fun withAuthenticatedEvernoteSession(action: EvernoteSession.() -> Unit) =
            EvernoteSession.getInstance(this, Evernote.consumerKey, Evernote.consumerSecret, Evernote.evernoteService, true)
                    .let { session ->
                        if (!session.isLoggedIn()) {
                            onEvernoteAuthenticated.addObserver {
                                try {
                                    session.action()
                                } finally {
                                    stop()
                                }
                            }
                            session.authenticate(this)
                        } else {
                            session.action()
                        }
                    }

    val onEvernoteAuthenticated = trigger<Unit>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            EvernoteSession.REQUEST_CODE_OAUTH ->
                if (resultCode == Activity.RESULT_OK) {
                    onEvernoteAuthenticated()
                }
            IntentCode.openFileFromDrive ->
                if (resultCode == Activity.RESULT_OK) {
                    val driveFile = Drive.DriveApi.getFile(driveFileOpenerClient, data!!.getExtras().get("response_drive_id") as DriveId)

                    driveFile.open(driveFileOpenerClient, DriveFile.MODE_READ_ONLY, object : DriveFile.DownloadProgressListener {
                        override fun onProgress(bytesDownloaded: Long, bytesExpected: Long) {
                            //TODO: mainTextView.setText("loading... " + if (bytesExpected > 0) "$bytesDownloaded / $bytesExpected bytes" else "")
                        }
                    }).setResultCallback (object : ResultCallback<DriveApi.DriveContentsResult> {
                        override fun onResult(result: DriveApi.DriveContentsResult) {
                            if (!result.getStatus().isSuccess()) throw UnsupportedOperationException()

                            val file = File(getCacheDir(), "temp.xmind")
                            result.getDriveContents().getInputStream().writeToFile(file)
                            open(file)
                        }

                    })

                }
            IntentCode.googleClientResolution ->
                if (resultCode == Activity.RESULT_OK) {
                    openFromDrive()
                }
            IntentCode.donate -> {
                if (resultCode == Activity.RESULT_OK) {
                    val purchase = donationService.purchaseInfo(data!!)

                    toast("You have bought ${purchase.productId}. Excellent choice, adventurer!")
                }
            }
        }
    }



    var currentText = ""
    var currentUrl = ""
    private fun open(workbook: IWorkbook) {
        ApplicationState.workbook = workbook

        val noteCount = workbook.getPrimarySheet().getRootTopic().getChildrenRecursively().count()

        trackOpenedMap(noteCount)

        val screen = GlScreen(this) {
            Shell(it, observableIterable(listOf(it.touchPointerKeys)), it.keyboard, GlFont(getResources()!!), workbook,
                    onOpenHyperlink = { browse(it) },
                    onActiveTopicChanged = {
                        currentText = (it?.getTitleText() ?: "")
                        currentUrl = it?.getHyperlink() ?: ""
                    },
                    textChanged = textChanged,
                    noteLinkChanged = noteLinkChanged,
                    newNote = newNote,
                    newSubnote = newSubnote,
                    removeNote = removeNote,
                    vibrate = { vibrator.vibrate(70) })
        }

        mindMapLayout.removeAllViews()
        mindMapLayout.addView(screen)

        val eventTypes = listOf(Core.TitleText, Core.TopicAdd, Core.TopicRemove, Core.TopicFolded, Core.TopicHyperlink, Core.TopicNotes)
        eventTypes.forEach {
            (workbook as WorkbookImpl).getCoreEventSupport().registerGlobalListener(it) {
                saveToDocuments()
            }
        }

    }

    private fun trackOpenedMap(noteCount: Int) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory("Content")
                .setAction("Opened Map")
                .setLabel("Note Count")
                .setValue(noteCount.toLong())
                .build()
        )
    }


    private fun InputStream.writeToFile(file: File) {
        try {
            val output = FileOutputStream(file)
            try {
                val buffer = ByteArray(1024)

                while (true) {
                    val read = read(buffer)
                    if (read == -1) break
                    output.write(buffer, 0, read)
                }
                output.flush()
            } finally {
                output.close()
            }
        } finally {
            close()
        }
    }

    private object IntentCode {
        val googleClientResolution = 0
        val openFileFromDrive = 1
        val donate = 2
    }
}
