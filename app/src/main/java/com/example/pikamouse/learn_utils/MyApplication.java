package com.example.pikamouse.learn_utils;

import android.app.Application;
import android.content.Context;

import com.example.pikamouse.learn_utils.test.AllInfoMonitor;
import com.example.pikamouse.learn_utils.test.DebugMonitor;
import com.example.pikamouse.learn_utils.test.MemoryMonitor;
import com.example.pikamouse.learn_utils.test.MonitorManager;
import com.example.pikamouse.learn_utils.test.NetInfoMonitor;

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
        MonitorManager.getInstance()
                .add(MonitorManager.MONITOR_DEBUG_CLASS, new DebugMonitor())
                .add(MonitorManager.MONITOR_MEMORY_CLASS, new MemoryMonitor())
                .add(MonitorManager.MONITOR_MEMORY_ALL_CLASS, new AllInfoMonitor())
                .add(MonitorManager.MONITOR_NET_INFO_CLASS, new NetInfoMonitor())
                .init(this);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
