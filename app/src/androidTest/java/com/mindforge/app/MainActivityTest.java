package com.mindforge.app;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

/*
This is Java and not Kotlin because I could not fix occurring JUnit runner problems.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    private MainActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startActivity(new Intent(getInstrumentation().getTargetContext(), MainActivity.class), null, null);
        activity = getActivity();
        if(BuildConfig.DEBUG) {
            // wait for debugger to make early breakpoints work:
            Thread.sleep(10000);
        }
    }

    public void testCreateNew() throws Exception {
        activity.createDefaultMindMap();
        assertEquals("Title", activity.getWorkbook().getPrimarySheet().getRootTopic().getTitleText());
    }
}