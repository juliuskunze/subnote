package com.mindforge.app

import android.app.Activity
import android.os.Bundle
import net.pureal.android.backend.GlScreen
import net.pureal.shell.Shell
import com.mindforge.graphics.observableIterable
import net.pureal.android.backend.GlFont
import com.mindforge.graphics.interaction.*
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.view.WindowManager

class PurealActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screen = GlScreen(this) {
            Shell(it, observableIterable(listOf(it.pointerKeys)), it.keyboard, GlFont(getResources()!!));
        }
        setContentView(screen);
        //getWindow()!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}