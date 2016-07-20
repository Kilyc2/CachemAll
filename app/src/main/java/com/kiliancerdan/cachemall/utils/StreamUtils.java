package com.kiliancerdan.cachemall.utils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void convertInputStreamToFile(InputStream inputStream, String filePath) throws IOException {
        File targetFile = new File(filePath);
        byte[] buffer = ByteStreams.toByteArray(inputStream);
        Files.write(buffer, targetFile);
    }
}
