package com.dhiman_da.task;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
    }
}
