package com.mindforge.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.evernote.client.android.EvernoteSession
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
import kotlinx.android.synthetic.activity_main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity
import org.xmind.core.IWorkbook
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.properties.Delegates

public class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textChanged(textInput.getText().toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        newNoteButton.setOnClickListener {
            newNote()
        }

        newSubnoteButton.setOnClickListener {
            newSubnote()
        }

        removeNoteButton.setOnClickListener {
            removeNode()
        }

        linkTypeSpinner.setAdapter(ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, LinkType.values()))
        linkTypeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val linkType = (parent.getItemAtPosition(pos) as LinkType)
                when (linkType) {
                    LinkType.None -> nodeLinkChanged(NodeLink(linkType, null))
                    LinkType.WebUrl -> showInputDialog("Web URL link", "Enter the URL") {
                        if (it == null) parent.setSelection(LinkType.None.ordinal())
                        else nodeLinkChanged(NodeLink(linkType, it))
                    }
                    LinkType.Evernote -> {
                        getEvernoteSession().authenticate(this@MainActivity)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })
    }

    private val textChanged = trigger<String>()
    private val nodeLinkChanged = trigger<NodeLink>()
    private val newNote = trigger<Unit>()
    private val newSubnote = trigger<Unit>()
    private val removeNode = trigger<Unit>()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.getItemId()

        return when (id) {
            R.id.action_settings -> {
                true
            }
            R.id.open_from_drive -> {
                openFromDrive()
                true
            }
            R.id.open_from_documents -> {
                openFromDocuments()
                true
            }
            R.id.drive_example -> {
                startActivity<DriveSampleActivity>()
                true
            }
            R.id.import_from_evernote -> {
                importFromEvernote()
                true
            }
            R.id.create_new -> {
                createNew()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun createNew() {
        val workbook = workbookBuilder.createWorkbook()
        workbook.getPrimarySheet().getRootTopic().setTitleText("Title")

        open(workbook)
    }

    private fun openFromDocuments() {
        open(File("/storage/emulated/0/documents/Projects.xmind"))
    }

    private fun openFromDrive() {
        if (!driveFileOpenerApiClient.isConnected()) {
            driveFileOpenerApiClient.connect()
            // chooseFileFromDrive will be called in onConnected.
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

    private val workbookBuilder: WorkbookBuilderImpl by Delegates.lazy { AndroidWorkbookBuilder(cacheDirectory = getCacheDir())() }

    private val driveFileOpenerApiClient: GoogleApiClient by Delegates.lazy {
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
                // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
                result.startResolutionForResult(this@MainActivity, IntentCode.googleClientResolution)
            }
        }).build()
    }

    private fun chooseFileFromDrive() {
        val intentSender = Drive.DriveApi.newOpenFileActivityBuilder().build(driveFileOpenerApiClient)

        startIntentSenderForResult(intentSender, IntentCode.openFileFromDrive, null, 0, 0, 0);
    }

    fun withEvernoteSession(action: (EvernoteSession) -> Unit) = EvernoteSession.getInstance(
            this, Evernote.consumerKey, Evernote.consumerSecret, Evernote.evernoteService, true
    ).let {
        if (!it.isLoggedIn()) {
            it.authenticate(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            IntentCode.openFileFromDrive ->
                if (resultCode == Activity.RESULT_OK) {
                    val driveFile = Drive.DriveApi.getFile(driveFileOpenerApiClient, data!!.getExtras().get("response_drive_id") as DriveId)

                    driveFile.open(driveFileOpenerApiClient, DriveFile.MODE_READ_ONLY, object : DriveFile.DownloadProgressListener {
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
        }
    }

    var workbook: IWorkbook by Delegates.notNull()

    private fun open(workbook: IWorkbook) {
        this.workbook = workbook

        val screen = GlScreen(this) {
            Shell(it, observableIterable(listOf(it.touchPointerKeys)), it.keyboard, GlFont(getResources()!!), workbook,
                    onOpenHyperlink = { browse(it) },
                    onActiveTopicChanged = {
                        textInput.setText(it?.getTitleText() ?: "")
                        textInput.selectAll()
                        linkTypeSpinner.setSelection((it?.getLinkType() ?: LinkType.None).ordinal())
                    },
                    textChanged = textChanged,
                    nodeLinkChanged = nodeLinkChanged,
                    newNote = newNote,
                    newSubnote = newSubnote,
                    removeNode = removeNode)
        }

        mindMapLayout.addView(screen)
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
    }
}