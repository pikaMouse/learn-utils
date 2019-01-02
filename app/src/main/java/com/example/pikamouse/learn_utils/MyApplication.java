package com.example.pikamouse.learn_utils;

import android.app.Application;
import android.content.Context;

import com.example.pikamouse.learn_utils.test.DebugMonitor;
import com.example.pikamouse.learn_utils.test.MemoryMonitor;

/**
 * @author: jiangfeng
 * @date: 2018/12/26
 */
public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        DebugMonitor.getInstance().init(this);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
