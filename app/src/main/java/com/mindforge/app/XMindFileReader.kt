package com.mindforge.app

import android.content.Context
import org.xmind.core.ITopic
import org.xmind.core.internal.InternalCore
import org.xmind.core.internal.dom.WorkbookBuilderImpl
import org.xmind.core.util.ILogger
import java.io.File

class XMindFileReader(val cacheDirectory: File) {
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

    fun rootTopics(file: File) : List<ITopic> {
        val builder = WorkbookBuilderImpl()
        val workbook = builder.loadFromFile(file)

        return workbook.getSheets().map { it.getRootTopic() }
    }

    fun getText(file: File) = rootTopics(file).map { it.toText() }.join("\n\n")

    fun ITopic.toText(): String = getTitleText() + "\n" + getAllChildren().map { it.toText() + if(it.getHyperlink() != null) "[" + it.getHyperlink() + "]" else ""}.join("\n").indented()
}

fun String.indented(indentation: String = "\t") = this.split("\n").map { indentation + it }.join("\n")