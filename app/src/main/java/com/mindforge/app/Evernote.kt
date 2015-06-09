package com.mindforge.app

import com.evernote.client.android.EvernoteSession
import com.evernote.edam.type.Notebook

object Evernote {
    val consumerKey = "stefan3291";
    val consumerSecret = "6636ba859ab683ec";
    val evernoteService = EvernoteSession.EvernoteService.SANDBOX;
    val linkPrefix = "https://sandbox.evernote.com/Home.action#b="
    fun isEvernoteLink(url: String) = url.startsWith(linkPrefix)
    fun extractNotebookGuid(url: String) = url.removePrefix(linkPrefix).split("&".toRegex()).toTypedArray().first()
}

fun Notebook.getWebUrl() = "${Evernote.linkPrefix}${getGuid()}&sh=1&"