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

package com.mindforge.app

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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.properties.Delegates

/**
 * Android Drive Quickstart activity. This activity takes a photo and saves it
 * in Google Drive. The user is prompted with a pre-made dialog which allows
 * them to choose the file location.
 */
public class DriveSampleActivity : Activity() {
    // Since no account name is passed, the user is prompted to choose.
    private val apiClient: GoogleApiClient by Delegates.lazy { GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(object : ConnectionCallbacks {
        override fun onConnected(connectionHint: Bundle?) {
            if (bitmap == null) {
                startCamera()
                return
            }
            saveFileToDrive()
        }

        override fun onConnectionSuspended(cause: Int) {
            log("GoogleApiClient connection suspended")
        }
    }).addOnConnectionFailedListener(object : OnConnectionFailedListener {
        override fun onConnectionFailed(result: ConnectionResult) {
            if (!result.hasResolution()) {
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this@DriveSampleActivity, 0).show()
                return
            }
            // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
            result.startResolutionForResult(this@DriveSampleActivity, IntentCode.googleClientResolution)
        }
    }).build() }

    private var bitmap: Bitmap? = null

    /**
     * Create a new file and save it to Drive.
     */
    private fun saveFileToDrive() {
        log("Creating new contents.")
        Drive.DriveApi.newDriveContents(apiClient).setResultCallback(object : ResultCallback<DriveContentsResult> {
            override fun onResult(result: DriveContentsResult) {
                if (!result.getStatus().isSuccess()) {
                    log("Failed to create new contents.")
                    return
                }

                val outputStream = result.getDriveContents().getOutputStream()
                writeBitmapToOutputStream(outputStream)

                try {
                    startIntentSenderForResult(intentSender(result), IntentCode.saveFile, null, 0, 0, 0)
                } catch (e: SendIntentException) {
                    log("Failed to launch file chooser.")
                }

            }

            private fun intentSender(result: DriveContentsResult): IntentSender? {
                val initialMetadataChangeSet = MetadataChangeSet.Builder().setMimeType("image/jpeg").setTitle("Android Photo.png").build()
                val fileChooserIntentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(initialMetadataChangeSet).setInitialDriveContents(result.getDriveContents()).build(apiClient)
                return fileChooserIntentSender
            }
        })
    }

    private fun writeBitmapToOutputStream(outputStream: OutputStream) {
        val bitmapStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream)
        try {
            outputStream.write(bitmapStream.toByteArray())
        } catch (e1: IOException) {
            log("Unable to write file contents.")
        }
    }

    override fun onResume() {
        super<Activity>.onResume()
        apiClient.connect()
    }

    override fun onPause() {
        apiClient.disconnect()
        super<Activity>.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            IntentCode.captureImage ->
                if (resultCode == Activity.RESULT_OK) {
                    bitmap = data!!.getExtras().get("data") as Bitmap
                }
            IntentCode.saveFile ->
                if (resultCode == Activity.RESULT_OK) {
                    bitmap = null
                    startCamera()
                }
        }
    }

    private fun startCamera() {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), IntentCode.captureImage)
    }


    private fun log(message: String) {
        Log.i("android-drive-quickstart", message)
    }

    private object IntentCode {
        val captureImage = 1
        val saveFile = 2
        val googleClientResolution = 3
    }
}