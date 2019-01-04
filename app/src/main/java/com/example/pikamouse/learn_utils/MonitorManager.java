package com.example.pikamouse.learn_utils;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.SparseArray;

import com.example.pikamouse.learn_utils.tools.monitor.IMonitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class MonitorManager {

    private static SparseArray<IMonitor> sMonitors = new SparseArray<>();

    //提示信息只需要在源文件保留
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MONITOR_INSTRUMENT_CLASS, MONITOR_MEMORY_CHART_CLASS, MONITOR_MEMORY_INFO_CLASS, MONITOR_NET_INFO_CLASS})
    public @interface MonitorClass {
    }
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MONITOR_MEMORY_PSS_TYPE, MONITOR_MEMORY_HEAP_TYPE, MONITOR_MEMORY_INFO_TYPE, MONITOR_INSTRUMENT_TYPE, MONITOR_NET_INFO_TYPE})
    public @interface MonitorType {
    }

    public final static int MONITOR_INSTRUMENT_CLASS = 0;
    public final static int MONITOR_MEMORY_CHART_CLASS = 1;
    public final static int MONITOR_MEMORY_INFO_CLASS = 2;
    public final static int MONITOR_NET_INFO_CLASS = 3;

    public static final String MONITOR_INSTRUMENT_TYPE = "instrument";
    public static final String MONITOR_MEMORY_INFO_TYPE = "memory_info";
    public static final String MONITOR_MEMORY_PSS_TYPE = "pss_chart";
    public static final String MONITOR_MEMORY_HEAP_TYPE = "heap_chart";
    public static final String MONITOR_NET_INFO_TYPE = "net_info";

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

    public void init(Context context) {
        int len = sMonitors.size();
        for (int i = 0; i < len; i++) {
            sMonitors.get(i).init(context);
        }
    }

    public void start() {
        IMonitor monitor = get(MONITOR_INSTRUMENT_CLASS);
        if (monitor != null) {
            monitor.start(null);
        }
    }

    public void stopAll() {
        int len = sMonitors.size();
        for (int i = 0; i < len; i++) {
            sMonitors.get(i).stop();
        }
    }

    public IMonitor get(@MonitorClass int key) {
        return sMonitors.get(key);
    }

    public static class Configure {

//        private static final String CONFIG_MEM_HEAP_TAG = "monitor_config_mem_heap";
//        private static final String CONFIG_MEM_PSS_TAG = "monitor_config_mem_pss";
//        private static final String CONFIG_MEM_SYSTEM_TAG = "monitor_config_mem_system";
//        private static final String CONFIG_CHART_HEAP_TAG = "monitor_config_chart_heap";
//        private static final String CONFIG_CHART_PSS_TAG = "monitor_config_chart_pss";
//        private static final String CONFIG_NET_RX_TAG = "monitor_config_net_rx";
//        private static final String CONFIG_NET_TX_TAG = "monitor_config_net_tx";
//        private static final String CONFIG_NET_RATE_TAG = "monitor_config_net_rate";

        public static boolean isDefMem;
        public static boolean isHeap;
        public static boolean isPSS;
        public static boolean isSystemMem;

        public static boolean isDefChart;
        public static boolean isPSSChart;
        public static boolean isHeapChart;

        public static boolean isDefNet;
        public static boolean isRX;
        public static boolean isTX;
        public static boolean isRate;
    }
}
