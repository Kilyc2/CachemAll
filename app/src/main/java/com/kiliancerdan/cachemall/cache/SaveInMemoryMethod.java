package com.kiliancerdan.cachemall.cache;

import android.util.Log;

import com.kiliancerdan.cachemall.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SaveInMemoryMethod extends CacheMethod {

    private File cacheDirectory;

    private static SaveInMemoryMethod cacheMethod = new SaveInMemoryMethod();
    private SaveInMemoryMethod() { }

    public static SaveInMemoryMethod getInstance() {
        return cacheMethod;
    }

    @Override
    public void setCacheDirectory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public boolean cacheFile() {
        String filePath = cacheDirectory.getPath().concat("/").concat(getNameVideo());
        if (isAlreadyCached(filePath)) {
            return true;
        }
        InputStream inputStream = null;
        boolean isCached = false;
        try {
            URL url = new URL(urlVideo);
            inputStream = url.openStream();
            StreamUtils.convertInputStreamToFile(inputStream, filePath);
            isCached = true;
        } catch (IllegalStateException|IOException e) {
            Log.d(LOG_CACHE, String.format("Failed while reading bytes from %s : %s", urlVideo, e.getMessage()));
        } finally {
            StreamUtils.closeStream(inputStream);
        }
        return isCached;
    }

    @Override
    public String getVideoPath() {
        if (!urlVideo.isEmpty()) {
            String filePath = cacheDirectory.getPath().concat("/").concat(getNameVideo());
            if (isAlreadyCached(filePath)) {
                return filePath;
            }
        }
        return "";
    }

    private boolean isAlreadyCached(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private String getNameVideo() {
        return urlVideo.substring(urlVideo.lastIndexOf("/") + 1,
                urlVideo.length() - 4).concat(".tmp");
    }
}
