package com.xmind.app

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import java.io.File
import kotlinx.android.synthetic.activity_main.*

public class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val xMindFileToText = XMindFileToText(tempDirectory = getCacheDir())
        val s = xMindFileToText(File("/storage/emulated/0/documents/Projects.xmind"))

        //mainTextView.setVerticalScrollBarEnabled(true)
        mainTextView.setText(s)
        //mainTextView.setMovementMethod(ScrollingMovementMethod())
        //alert(s)
    }

    private fun alert(s: String) {
        val alert = AlertDialog.Builder(this)
        alert.setMessage(s)
        alert.setTitle("App Title")
        alert.setPositiveButton("OK", null)
        alert.setCancelable(true)
        alert.create().show()

        alert.setPositiveButton("Ok",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                    }
                })
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
