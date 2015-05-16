package com.mindforge.app

import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.EditText

fun Context.showInputDialog(title: String, message: String, defaultValue: String = "", onValueEntered: (String?) -> Unit) =
        EditText(this).let {
            it.setText(defaultValue)
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

fun Context.showSelectDialog<T : Any>(title: String, options: List<T>, onItemSelected: (T?) -> Unit) {
    var selectedItem: T? = null
    AlertDialog.Builder(this)
            .setTitle(title)
            .setSingleChoiceItems(ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, options), 0) { dialog, which ->
                selectedItem = options[which]
            }
            .setPositiveButton("OK") { dialog, which ->
                onItemSelected(selectedItem)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                onItemSelected(null)
            }
            .show()
}