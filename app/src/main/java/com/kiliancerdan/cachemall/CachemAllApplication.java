package com.kiliancerdan.cachemall;

import android.app.Application;

public class CachemAllApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Proxy.initialize(this);
    }
}
