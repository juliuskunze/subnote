package com.mindforge.app

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

fun Context.showInputDialog(title: String, message: String, onValueEntered: (String?) -> Unit) =
        EditText(this).let {
            AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setView(it)
                    .setPositiveButton("OK") { dialog, which ->
                        onValueEntered(it.getText().toString())
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        onValueEntered(null)
                        dialog.cancel()
                    }
                    .show()
        }
