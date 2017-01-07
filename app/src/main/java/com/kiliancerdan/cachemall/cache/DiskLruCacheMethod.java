package com.kiliancerdan.cachemall.cache;

import android.util.Log;

import com.google.common.io.ByteStreams;
import com.jakewharton.disklrucache.DiskLruCache;
import com.kiliancerdan.cachemall.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DiskLruCacheMethod extends CacheMethod {

    private static final String LOG_CACHE = "CACHE";
    private static final String KEY = "key1";
    private static final int POSITION = 0;

    private DiskLruCache cache;

    private static DiskLruCacheMethod cacheMethod = new DiskLruCacheMethod();
    private DiskLruCacheMethod() { }

    public static DiskLruCacheMethod getInstance() {
        return cacheMethod;
    }

    @Override
    public void setCacheDirectory(File cacheDirectory) {
        final int hundredMB = 1024 * 1024 * 100;
        final int version = 1;
        final int oneFile = 1;
        try {
            cache = DiskLruCache.open(cacheDirectory, version, oneFile, hundredMB);
        } catch (IOException e) {
            Log.d(LOG_CACHE, String.format("Failed while opening the cache: %s", e.getMessage()));
        }
    }

    @Override
    public boolean cacheFile() {
        if (isAlreadyCached()) {
            return true;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean isCached = false;
        try {
            DiskLruCache.Editor editor = cache.edit(KEY);
            if (editor != null) {
                URL url = new URL(urlVideo);
                inputStream = url.openStream();
                byte[] buffer = ByteStreams.toByteArray(inputStream);
                outputStream = editor.newOutputStream(POSITION);
                outputStream.write(buffer);
                editor.commit();
                isCached = true;
            }
        } catch (IllegalStateException|IOException e) {
            Log.d(LOG_CACHE, String.format("Failed while reading bytes from %s : %s", urlVideo, e.getMessage()));
        } finally {
            StreamUtils.closeStream(inputStream);
            StreamUtils.closeStream(outputStream);
        }
        return isCached;
    }

    @Override
    public String getVideoPath() {
        final String nameTemporalFile = "TempFile.tpm";
        InputStream inputStream = null;
        String path = "";
        try {
            DiskLruCache.Snapshot snapshot = cache.get(KEY);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(POSITION);
                path = cache.getDirectory().getPath().concat(nameTemporalFile);
                StreamUtils.convertInputStreamToFile(inputStream, path);
                inputStream.close();
            }
        } catch (IOException e) {
            Log.d(LOG_CACHE, String.format("Failed while getting video : %s", e.getMessage()));
            path = "";
        } finally {
            StreamUtils.closeStream(inputStream);
        }
        return path;
    }

    private boolean isAlreadyCached() {
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = cache.get(KEY);
        } catch (IOException e) {
            snapshot = null;
        }
        return snapshot != null;
    }
}
