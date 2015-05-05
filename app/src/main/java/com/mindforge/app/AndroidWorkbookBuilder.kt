package com.mindforge.app

import android.content.Context
import org.xmind.core.ITopic
import org.xmind.core.internal.InternalCore
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import org.xmind.core.util.ILogger
import java.io.File

class AndroidWorkbookBuilder(val cacheDirectory: File) {
    init {
        System.setProperty("org.xmind.core.workspace", cacheDirectory.getAbsolutePath())

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

    private val workbookBuilder = WorkbookBuilderImpl()

    fun invoke() = workbookBuilder
}