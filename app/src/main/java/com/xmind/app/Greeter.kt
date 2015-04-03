package com.xmind.app

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import org.xmind.core.internal.InternalCore
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import org.xmind.core.util.ILogger
import java.io.File

class Greeter {
    fun greet(context: Context) {
        InternalCore.getInstance().setLogger(object : ILogger {
            override fun log(p0: Throwable?) {
                println("ex!")
            }

            override fun log(p0: Throwable?, p1: String?) {
                println("ex2!")
            }

            override fun log(p0: String?) {
                println("ex3!")
            }
        })

        val builder = WorkbookBuilderImpl()
        val file = File.createTempFile("temp", ".xmind", context.getCacheDir())

        System.setProperty("org.xmind.core.workspace", context.getCacheDir().getAbsolutePath())

        Utils.createFileFromResource(file.path, context, array(R.raw.test))

        val workbook = builder.loadFromFile(file)
        val s = workbook.getSheets().single().getRootTopic().getAllChildren().map { it.getTitleText() }.join(", ")

        val alert = AlertDialog.Builder(context)
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
}