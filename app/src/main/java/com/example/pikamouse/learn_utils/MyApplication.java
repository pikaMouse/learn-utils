package com.example.pikamouse.learn_utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.example.pikamouse.learn_utils.tools.monitor.InstrumentMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.AllInfoMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.ChartMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;

import java.lang.ref.SoftReference;

/**
 * @author: jiangfeng
 * @date: 2018/12/26
 */
public class MyApplication extends Application {

    private static Context sContext;
    public static SoftReference<Activity> mActivityRef;


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        MonitorManager.getInstance()
                .add(MonitorManager.MONITOR_INSTRUMENT_CLASS, new InstrumentMonitor())
                .add(MonitorManager.MONITOR_CHART_CLASS, new ChartMonitor())
                .add(MonitorManager.MONITOR_ALL_INFO_CLASS, new AllInfoMonitor())
                .init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivityRef = new SoftReference<>(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    public static Context getAppContext() {
        return sContext;
    }
}
