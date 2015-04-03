package com.xmind.app

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
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
        val xMindFileToText = XMindFileToText(tempDirectory = getCacheDir())
        val s = xMindFileToText(File("/storage/emulated/0/documents/Projects.xmind"))
        val a = Drive.DriveApi.newOpenFileActivityBuilder()
        val i = a.build(GoogleApiClient.Builder(this).build())

        mainTextView.setText(s)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
