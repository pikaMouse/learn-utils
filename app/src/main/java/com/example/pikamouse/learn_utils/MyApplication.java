package com.example.pikamouse.learn_utils;

import android.app.Application;
import android.content.Context;

import com.example.pikamouse.learn_utils.tools.monitor.AllInfoMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.DebugMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.MemoryMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.NetInfoMonitor;

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
                .add(MonitorManager.MONITOR_INSTRUMENT_CLASS, new DebugMonitor())
                .add(MonitorManager.MONITOR_MEMORY_CHART_CLASS, new MemoryMonitor())
                .add(MonitorManager.MONITOR_MEMORY_INFO_CLASS, new AllInfoMonitor())
                .add(MonitorManager.MONITOR_NET_INFO_CLASS, new NetInfoMonitor())
                .init(this);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
