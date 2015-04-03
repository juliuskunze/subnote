/**
 * Copyright 2013 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xmind.app

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveApi.DriveContentsResult
import com.google.android.gms.drive.MetadataChangeSet

/**
 * Android Drive Quickstart activity. This activity takes a photo and saves it
 * in Google Drive. The user is prompted with a pre-made dialog which allows
 * them to choose the file location.
 */
public class DriveSampleActivity : Activity(), ConnectionCallbacks, OnConnectionFailedListener {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mBitmapToSave: Bitmap? = null

    /**
     * Create a new file and save it to Drive.
     */
    private fun saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.")
        val image = mBitmapToSave
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(object : ResultCallback<DriveContentsResult> {
            override fun onResult(result: DriveContentsResult) {
                // If the operation was not successful, we cannot do anything
                // and must
                // fail.
                if (!result.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new contents.")
                    return
                }
                // Otherwise, we can write our data to the new contents.
                Log.i(TAG, "New contents created.")
                // Get an output stream for the contents.
                val outputStream = result.getDriveContents().getOutputStream()
                // Write the bitmap data from it.
                val bitmapStream = ByteArrayOutputStream()
                image!!.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream)
                try {
                    outputStream.write(bitmapStream.toByteArray())
                } catch (e1: IOException) {
                    Log.i(TAG, "Unable to write file contents.")
                }

                // Create the initial metadata - MIME type and title.
                // Note that the user will be able to change the title later.
                val metadataChangeSet = MetadataChangeSet.Builder().setMimeType("image/jpeg").setTitle("Android Photo.png").build()
                // Create an intent for the file chooser, and start it.
                val intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(result.getDriveContents()).build(mGoogleApiClient)
                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0)
                } catch (e: SendIntentException) {
                    Log.i(TAG, "Failed to launch file chooser.")
                }

            }
        })
    }

    override fun onResume() {
        super<Activity>.onResume()
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient!!.connect()
    }

    override fun onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
        super<Activity>.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_CAPTURE_IMAGE -> // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = data!!.getExtras().get("data") as Bitmap
                }
            REQUEST_CODE_CREATOR -> // Called after a file is saved to Drive.
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.")
                    mBitmapToSave = null
                    // Just start the camera again for another photo.
                    startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE)
                }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString())
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show()
            return
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION)
        } catch (e: SendIntentException) {
            Log.e(TAG, "Exception while starting resolution activity", e)
        }

    }

    override fun onConnected(connectionHint: Bundle?) {
        Log.i(TAG, "API client connected.")
        if (mBitmapToSave == null) {
            // This activity has no UI of its own. Just start the camera.
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE)
            return
        }
        saveFileToDrive()
    }

    override fun onConnectionSuspended(cause: Int) {
        Log.i(TAG, "GoogleApiClient connection suspended")
    }

    companion object {
        private val TAG = "android-drive-quickstart"
        private val REQUEST_CODE_CAPTURE_IMAGE = 1
        private val REQUEST_CODE_CREATOR = 2
        private val REQUEST_CODE_RESOLUTION = 3
    }
}