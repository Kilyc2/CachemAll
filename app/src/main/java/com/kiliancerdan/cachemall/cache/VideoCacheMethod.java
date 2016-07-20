package com.kiliancerdan.cachemall.cache;

import android.util.Log;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.common.io.ByteStreams;
import com.kiliancerdan.cachemall.Proxy;
import com.kiliancerdan.cachemall.utils.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class VideoCacheMethod extends CacheMethod implements CacheListener {

    private Proxy proxy = Proxy.getInstance();

    private static VideoCacheMethod cacheMethod = new VideoCacheMethod();
    private VideoCacheMethod() { }

    public static VideoCacheMethod getInstance() {
        return cacheMethod;
    }

    @Override
    public void setCacheDirectory(File cacheDirectory) {
    }

    @Override
    public boolean cacheFile() {
        boolean isCached = false;
        HttpProxyCacheServer proxyCache = proxy.getHttpProxyCacheServer();
        proxyCache.registerCacheListener(this, urlVideo);
        InputStream inputStream = null;
        try {
            URL url = new URL(proxyCache.getProxyUrl(urlVideo));
            inputStream = url.openStream();
            ByteStreams.toByteArray(inputStream);
            isCached = true;
        } catch(Exception e) {
            Log.d(LOG_CACHE, String.format("Failed while reading bytes from %s : %s", urlVideo, e.getMessage()));
        } finally {
            StreamUtils.closeStream(inputStream);
        }
        return isCached;
    }

    @Override
    public String getVideoPath() {
        if (urlVideo.isEmpty()) {
            return "";
        }
        HttpProxyCacheServer proxyCacheServer = proxy.getHttpProxyCacheServer();
        return proxyCacheServer.getProxyUrl(urlVideo);
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if (percentsAvailable % 10 == 0) {
            Log.d(LOG_CACHE, String.format("onCacheAvailable. percents: %d, file: %s, url: %s",
                    percentsAvailable, cacheFile, url));
        }
    }
}
