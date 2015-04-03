package com.xmind.app

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import org.xmind.core.internal.InternalCore
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import org.xmind.core.util.ILogger
import java.io.File

class XMindFileLoader(val context : Context) {
    init {
        System.setProperty("org.xmind.core.workspace", context.getCacheDir().getAbsolutePath())

        InternalCore.getInstance().setLogger(object : ILogger {
            override fun log(p0: Throwable?) {
                if (p0 != null) throw p0
            }

            override fun log(p0: Throwable?, p1: String?) {
                println(p1)
                if (p0 != null) throw p0
            }

            override fun log(p0: String?) {
                println(p0)
            }
        })
    }

    fun load(file: File) : String {
        val builder = WorkbookBuilderImpl()
        val workbook = builder.loadFromFile(file)
        return workbook.getSheets().single().getRootTopic().getAllChildren().map { it.getTitleText() }.join(", ")
    }
}