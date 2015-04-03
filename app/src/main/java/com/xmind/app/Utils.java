package com.xmind.app;

import android.content.Context;
import android.content.res.Resources;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Utils {
    public static void createFileFromResource(final String outputFile,
                                              final Context context, final Integer[] inputRawResources)
            throws IOException {

        final OutputStream outputStream = new FileOutputStream(outputFile);

        final Resources resources = context.getResources();
        final byte[] largeBuffer = new byte[1024 * 4];
        int totalBytes = 0;
        int bytesRead = 0;

        for (Integer resource : inputRawResources) {
            final InputStream inputStream = resources.openRawResource(resource);
            while ((bytesRead = inputStream.read(largeBuffer)) > 0) {
                if (largeBuffer.length == bytesRead) {
                    outputStream.write(largeBuffer);
                } else {
                    final byte[] shortBuffer = new byte[bytesRead];
                    System.arraycopy(largeBuffer, 0, shortBuffer, 0, bytesRead);
                    outputStream.write(shortBuffer);
                }
                totalBytes += bytesRead;
            }
            inputStream.close();
        }

        outputStream.flush();
        outputStream.close();
    }
}
