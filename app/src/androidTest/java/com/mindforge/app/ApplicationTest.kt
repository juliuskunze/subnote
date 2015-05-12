package com.mindforge.app

import android.app.Activity
import android.app.Application
import android.test.ActivityInstrumentationTestCase2
import android.test.ActivityTestCase
import android.test.ApplicationTestCase
import kotlin.properties.Delegates
import kotlin.test.assertNotNull

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
public class ApplicationTest : ActivityInstrumentationTestCase2<MainActivity>(javaClass<MainActivity>())