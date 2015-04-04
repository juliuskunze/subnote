package com.xmind.app

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient

import com.google.android.gms.drive.*

import java.io.File
import kotlinx.android.synthetic.activity_main.*

public class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

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
            R.id.action_settings -> true
            R.id.open_from_drive -> {
                openFromDrive()
                true
            }
            R.id.open_from_documents -> {
                openFromDocuments()
                true
            }
            R.id.drive_example -> {
                startDriveAPIExampleActivity()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun openFromDocuments() {
        open(File("/storage/emulated/0/documents/Projects.xmind"))
    }

    private fun openFromDrive() {

    }

    private fun startDriveAPIExampleActivity() {
        startActivity(Intent(this, javaClass<DriveSampleActivity>()))
    }

    private fun open(file: File) {
        val xMindFileToText = XMindFileToText(tempDirectory = getCacheDir())
        val s = xMindFileToText(file)

        mainTextView.setText(s)
    }


}
