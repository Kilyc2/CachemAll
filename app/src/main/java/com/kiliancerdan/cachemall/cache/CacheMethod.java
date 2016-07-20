package com.kiliancerdan.cachemall.cache;

import java.io.File;

public abstract class CacheMethod {

    protected static final String LOG_CACHE = "CACHE";
    protected String urlVideo = "";

    public void setUrlVideo(String url) {
        this.urlVideo = url;
    }

    public abstract void setCacheDirectory(File cacheDirectory);

    public abstract boolean cacheFile();

    public abstract String getVideoPath();

    public void clearCache(File dir) {
        File[] directory = dir.listFiles();
        if (directory != null) {
            for (File file : directory) {
                file.delete();
            }
        }
    }
}
