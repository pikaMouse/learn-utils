package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class MonitorManager {

    private static SparseArray<IMonitor> sMonitors = new SparseArray<>();

    //提示信息只需要在源文件保留
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MONITOR_INSTRUMENT_CLASS, MONITOR_CHART_CLASS, MONITOR_ALL_INFO_CLASS})
    public @interface MonitorClass {
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            MONITOR_TOTAL_TAG,
            MONITOR_INSTRUMENT_TAG,
            MONITOR_MEM_TAG,
            MONITOR_MEM_TAG_HEAP,
            MONITOR_MEM_TAG_HEAP_FREE,
            MONITOR_MEM_TAG_HEAP_ALLOC,
            MONITOR_MEM_TAG_PSS,
            MONITOR_MEM_TAG_PSS_DALVIK,
            MONITOR_MEM_TAG_PSS_NATIVE,
            MONITOR_MEM_TAG_PSS_OTHER,
            MONITOR_MEM_TAG_SYSTEM,
            MONITOR_MEM_TAG_SYSTEM_AVAIL,
            MONITOR_NET_TAG,
            MONITOR_NET_TAG_RX,
            MONITOR_NET_TAG_TX,
            MONITOR_NET_TAG_RATE,
            MONITOR_CHART_TAG,
            MONITOR_CHART_TAG_PSS,
            MONITOR_CHART_TAG_HEAP,
            MONITOR_FRAME_TAG,
            MONITOR_FRAME_TAG_FPS
            })
    public @interface MonitorTag {
    }

    public final static int MONITOR_INSTRUMENT_CLASS = 0;
    public final static int MONITOR_CHART_CLASS = 1;
    public final static int MONITOR_ALL_INFO_CLASS = 2;

    public static final String MONITOR_INSTRUMENT_TAG = "instrument";

    public static final String MONITOR_TOTAL_TAG = "全部";


    public static final String MONITOR_MEM_TAG = "内存";
    public static final String MONITOR_MEM_TAG_HEAP = "Heap";
    public static final String MONITOR_MEM_TAG_HEAP_FREE = "Heap Free";
    public static final String MONITOR_MEM_TAG_HEAP_ALLOC = "Heap Alloc";
    public static final String MONITOR_MEM_TAG_PSS = "PSS";
    public static final String MONITOR_MEM_TAG_PSS_DALVIK = "PSS Dalvik";
    public static final String MONITOR_MEM_TAG_PSS_NATIVE = "PSS Native";
    public static final String MONITOR_MEM_TAG_PSS_OTHER = "PSS Other";
    public static final String MONITOR_MEM_TAG_SYSTEM = "System Total";
    public static final String MONITOR_MEM_TAG_SYSTEM_AVAIL = "System Avail";

    public static final String MONITOR_NET_TAG = "网络";
    public static final String MONITOR_NET_TAG_RX = "RX";
    public static final String MONITOR_NET_TAG_TX = "TX";
    public static final String MONITOR_NET_TAG_RATE = "Rate";

    public static final String MONITOR_CHART_TAG = "图表";
    public static final String MONITOR_CHART_TAG_PSS = "pss";
    public static final String MONITOR_CHART_TAG_HEAP = "heap";

    public static final String MONITOR_CPU_TAG = "CPU";
    public static final String MONITOR_CPU_TAG_PERCENTAGE = "percentage";

    public static final String MONITOR_FRAME_TAG = "帧率";
    public static final String MONITOR_FRAME_TAG_FPS = "FPS";


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
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("u must init with application context");
        }
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

    public static class ItemBuilder {

        private static List<String> sTitles = new ArrayList<>();
        private static Map<String, List<String>> sItems = new HashMap<>();
        private static Map<String, Boolean> sCheckState = new HashMap<>();

        public static void setCheckState(boolean isCheck, @NonNull String tag) {
            if (isTitle(tag)) {
                sCheckState.put(tag, isCheck);
                List<String> items = getItems(tag);
                if (items == null) return;
                for (String item : items) {
                    sCheckState.put(item, false);
                }
            } else {
               sCheckState.put(tag, isCheck);
            }
        }

        public static boolean getCheckState(String tag) {
            Boolean b = sCheckState.get(tag);
            return b == null ? false : b;
        }

        public static void setTitle(boolean isAdd, @NonNull String title) {
            if (isAdd) {
                List<String>list = new ArrayList<>();
                sItems.put(title, list);
                sTitles.add(title);
            } else {
                sItems.remove(title);
                sTitles.remove(title);
            }
        }
        public static void addItem(boolean isAdd, @NonNull String item) {
            String title = Item2Title(item);
            if (title == null) return;
            List<String> list = sItems.get(title);
            if (list == null) return;
            if (isAdd) {
                list.add(item);
            } else {
                list.remove(item);
            }
            sItems.put(title, list);
        }

        public static void clear() {
            sItems.clear();
            sTitles.clear();
        }

        public static int getItemsSize(@NonNull String title) {
            List<String>items = sItems.get(title);
            return items == null ? 0 : items.size();
        }

        public static List<String> getItems(@NonNull String title) {
            return sItems.get(title);
        }

        public static List<String> getDefaultItems(String title) {
            return Title2Item(title);
        }

        public static List<String> getTitles() {
            return sTitles;
        }

        public static List<String> getAllItems() {
            List<String> allItems = new ArrayList<>();
            List<String> titles = getTitles();
            for (String title : titles) {
                List<String> items = getItems(title);
                allItems.addAll((items));
            }
            return allItems;
        }

        private static boolean isTitle(String tag) {
            return tag.equals(MONITOR_MEM_TAG) || tag.equals(MONITOR_NET_TAG)
                    || tag.equals(MONITOR_CHART_TAG) || tag.equals(MONITOR_CPU_TAG) ||tag.equals(MONITOR_FRAME_TAG);
        }

        private static String Item2Title(String item) {
            if (item.equals(MONITOR_MEM_TAG_HEAP) || item.equals(MONITOR_MEM_TAG_HEAP_FREE) || item.equals(MONITOR_MEM_TAG_HEAP_ALLOC)
                    || item.equals(MONITOR_MEM_TAG_PSS) || item.equals(MONITOR_MEM_TAG_PSS_DALVIK) || item.equals(MONITOR_MEM_TAG_PSS_NATIVE)
                    || item.equals(MONITOR_MEM_TAG_PSS_OTHER) || item.equals(MONITOR_MEM_TAG_SYSTEM) || item.equals(MONITOR_MEM_TAG_SYSTEM_AVAIL)) {
                return MONITOR_MEM_TAG;
            }
            if (item.equals(MONITOR_NET_TAG_RX) || item.equals(MONITOR_NET_TAG_TX) || item.equals(MONITOR_NET_TAG_RATE)) {
                return MONITOR_NET_TAG;
            }
            if (item.equals(MONITOR_CHART_TAG_HEAP) || item.equals(MONITOR_CHART_TAG_PSS)) {
                return MONITOR_CHART_TAG;
            }
            if (item.equals(MONITOR_CPU_TAG_PERCENTAGE)) {
                return MONITOR_CPU_TAG;
            }
            if (item.equals(MONITOR_FRAME_TAG_FPS)) {
                return MONITOR_FRAME_TAG;
            }
            return null;
        }

        private static List<String>Title2Item(String title) {
            List<String> items = new ArrayList<>();
            if (title.equals(MONITOR_MEM_TAG)) {
                items.add(MONITOR_MEM_TAG_HEAP);
                items.add(MONITOR_MEM_TAG_HEAP_FREE);
                items.add(MONITOR_MEM_TAG_HEAP_ALLOC);
                items.add(MONITOR_MEM_TAG_PSS);
                items.add(MONITOR_MEM_TAG_PSS_DALVIK);
                items.add(MONITOR_MEM_TAG_PSS_NATIVE);
                items.add(MONITOR_MEM_TAG_PSS_OTHER);
                items.add(MONITOR_MEM_TAG_SYSTEM);
                items.add(MONITOR_MEM_TAG_SYSTEM_AVAIL);
            } else if (title.equals(MONITOR_NET_TAG)) {
                items.add(MONITOR_NET_TAG_RX);
                items.add(MONITOR_NET_TAG_TX);
                items.add(MONITOR_NET_TAG_RATE);
            } else if (title.equals(MONITOR_CHART_TAG)) {
                items.add(MONITOR_CHART_TAG_HEAP);
                items.add(MONITOR_CHART_TAG_PSS);
            } else if (title.equals(MONITOR_CPU_TAG)) {
                items.add(MONITOR_CPU_TAG_PERCENTAGE);
            } else if (title.equals(MONITOR_FRAME_TAG)) {
                items.add(MONITOR_FRAME_TAG_FPS);
            }
            return items;
        }
    }
}
