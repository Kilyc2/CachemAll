package com.kiliancerdan.cachemall;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

public class Proxy {
    private HttpProxyCacheServer httpProxyCacheServer;
    private static Proxy ourInstance;

    public static Proxy getInstance() {
        return ourInstance;
    }

    private Proxy(Context context) {
        httpProxyCacheServer = new HttpProxyCacheServer.Builder(context)
                .cacheDirectory(context.getCacheDir())
                .maxCacheFilesCount(2)
                .build();
    }

    public HttpProxyCacheServer getHttpProxyCacheServer() {
        return httpProxyCacheServer;
    }

    public static void initialize(Context context) {
        ourInstance = new Proxy(context);
    }
}
