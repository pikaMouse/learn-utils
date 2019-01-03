package com.example.pikamouse.learn_utils.test;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class MonitorManager implements IMonitor{

    private static SparseArray<IMonitor> sMonitors = new SparseArray<>();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MONITOR_DEBUG, MONITOR_MEMORY, MONITOR_ALL_INFO, MONITOR_NET_INFO})
    public @interface MonitorType {
    }

    public final static int MONITOR_DEBUG = 0;
    public final static int MONITOR_MEMORY = 1;
    public final static int MONITOR_ALL_INFO = 2;
    public final static int MONITOR_NET_INFO = 3;

    private static class SingleHolder {
        private final static MonitorManager MEMORY_MONITOR = new MonitorManager();
    }

    private MonitorManager() {
    }

    public static MonitorManager getInstance() {
        return SingleHolder.MEMORY_MONITOR;
    }

    public MonitorManager add(@MonitorType int key, @NonNull IMonitor iMonitor) {
        sMonitors.put(key, iMonitor);
        return this;
    }

    @Override
    public void init(Context context) {
        int len = sMonitors.size();
        for (int i = 0; i < len; i++) {
            sMonitors.get(i).init(context);
        }
    }

    @Override
    public void stop() {
        int len = sMonitors.size();
        for (int i = 0; i < len; i++) {
            sMonitors.get(i).stop();
        }
    }

    public IMonitor get(@MonitorType int key) {
        IMonitor monitor = sMonitors.get(key);
        if (monitor instanceof  MemoryMonitor) {
            return monitor;
        }
        return null;
    }


}
