package com.example.pikamouse.learn_utils.test;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class MonitorManager implements IMonitor{

    private static SparseArray<IMonitor> sMonitors = new SparseArray<>();

    //提示信息只需要在源文件保留
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MONITOR_DEBUG_CLASS, MONITOR_MEMORY_CLASS, MONITOR_MEMORY_ALL_CLASS, MONITOR_NET_INFO_CLASS})
    public @interface MonitorClass {
    }
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MONITOR_MEMORY_PSS_TYPE, MONITOR_MEMORY_HEAP_TYPE, MONITOR_MEMORY_ALL_TYPE, MONITOR_DEBUG_BALL_TYPE, MONITOR_NET_ALL_TYPE})
    public @interface MonitorType {
    }

    public final static int MONITOR_DEBUG_CLASS = 0;
    public final static int MONITOR_MEMORY_CLASS = 1;
    public final static int MONITOR_MEMORY_ALL_CLASS = 2;
    public final static int MONITOR_NET_INFO_CLASS = 3;

    public static final String MONITOR_DEBUG_BALL_TYPE = "ball";
    public static final String MONITOR_MEMORY_ALL_TYPE = "all";
    public static final String MONITOR_MEMORY_PSS_TYPE = "pss";
    public static final String MONITOR_MEMORY_HEAP_TYPE = "heap";
    public static final String MONITOR_NET_ALL_TYPE = "net";

    private static class SingleHolder {
        private final static MonitorManager MEMORY_MONITOR = new MonitorManager();
    }

    private MonitorManager() {
    }

    public static MonitorManager getInstance() {
        return SingleHolder.MEMORY_MONITOR;
    }

    public MonitorManager add(@MonitorClass int key, @NonNull IMonitor iMonitor) {
        if (sMonitors.valueAt(key) == null) {
            sMonitors.put(key, iMonitor);
        }
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
    public void start(String type) {
        IMonitor monitor = get(MONITOR_DEBUG_CLASS);
        if (monitor != null) {
            monitor.start(type);
        }
    }

    @Override
    public void stop() {
        int len = sMonitors.size();
        for (int i = 0; i < len; i++) {
            sMonitors.get(i).stop();
        }
    }

    public IMonitor get(@MonitorClass int key) {
        return sMonitors.get(key);
    }


}
