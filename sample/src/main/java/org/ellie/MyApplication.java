package org.ellie;

import android.app.Application;

import org.ellie.library.OkHttpRequest;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpRequest.getInstance().addLogInterceptor(true);
    }
}
