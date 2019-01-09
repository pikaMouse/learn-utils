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
            MONITOR_TAG_DEFAULT,
            MONITOR_INSTRUMENT_TAG,
            MONITOR_MEM_TAG,
            MONITOR_MEM_TAG_HEAP,
            MONITOR_MEM_TAG_PSS,
            MONITOR_MEM_TAG_SYSTEM,
            MONITOR_NET_TAG,
            MONITOR_NET_TAG_RX,
            MONITOR_NET_TAG_TX,
            MONITOR_NET_TAG_RATE,
            MONITOR_CHART_TAG,
            MONITOR_CHART_TAG_PSS,
            MONITOR_CHART_TAG_HEAP
            })
    public @interface MonitorTag {
    }

    public final static int MONITOR_INSTRUMENT_CLASS = 0;
    public final static int MONITOR_CHART_CLASS = 1;
    public final static int MONITOR_ALL_INFO_CLASS = 2;

    public static final String MONITOR_INSTRUMENT_TAG = "instrument";

    public static final String MONITOR_TOTAL_TAG = "全部";

    public static final String MONITOR_TAG_DEFAULT = "默认";

    public static final String MONITOR_MEM_TAG = "内存";
    public static final String MONITOR_MEM_TAG_HEAP = "Heap";
    public static final String MONITOR_MEM_TAG_PSS = "PSS";
    public static final String MONITOR_MEM_TAG_SYSTEM = "System";

    public static final String MONITOR_NET_TAG = "网络";
    public static final String MONITOR_NET_TAG_RX = "RX";
    public static final String MONITOR_NET_TAG_TX = "TX";
    public static final String MONITOR_NET_TAG_RATE = "Rate";

    public static final String MONITOR_CHART_TAG = "图表";
    public static final String MONITOR_CHART_TAG_PSS = "pss";
    public static final String MONITOR_CHART_TAG_HEAP = "heap";

    public static final String MONITOR_CPU_TAG = "CPU";
    public static final String MONITOR_CPU_TAG_PERCENTAGE = "percentage";


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
        ItemBuilder.clear();
    }

    public IMonitor get(@MonitorClass int key) {
        return sMonitors.get(key);
    }

    public static class ItemBuilder {
        private List<String> mList;

        private static Map<String, ItemBuilder> sBuilders = new HashMap<>();
        private static List<String> sTitles = new ArrayList<>();
        private static Map<String, List<String>> sItems = new HashMap<>();

        private ItemBuilder () {
            mList = new ArrayList<>();
        }
        public static ItemBuilder create(String title) {
            ItemBuilder itemBuilder = sBuilders.get(title);
            if (itemBuilder == null) {
                itemBuilder = new ItemBuilder();
                sBuilders.put(title, itemBuilder);
            }
            return itemBuilder;
        }
        public void setTitle(boolean isAdd, @NonNull String title) {
            if (isAdd) {
                sItems.put(title, mList);
                sTitles.add(title);
            } else {
                sItems.remove(title);
                sTitles.remove(title);
            }
        }
        public void addItem(boolean isAdd, @NonNull String item) {
            String title = Item2Title(item);
            if (title == null) return;
            List<String> list = sItems.get(title);
            if (list == null) return;
            if (isAdd) {
                list.add(item);
            } else {
                list.remove(item);
            }
        }

        public static void clear() {
            sBuilders.clear();
            sItems.clear();
            sTitles.clear();
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
                if (items.isEmpty()) items = getDefaultItems(title);
                allItems.addAll((items));
            }
            return allItems;
        }

        private static String Item2Title(String item) {
            if (item.equals(MONITOR_MEM_TAG_HEAP) || item.equals(MONITOR_MEM_TAG_PSS) || item.equals(MONITOR_MEM_TAG_SYSTEM)) {
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
            return null;
        }

        private static List<String>Title2Item(String title) {
            List<String> items = new ArrayList<>();
            if (title.equals(MONITOR_MEM_TAG)) {
                items.add(MONITOR_MEM_TAG_HEAP);
                items.add(MONITOR_MEM_TAG_PSS);
                items.add(MONITOR_MEM_TAG_SYSTEM);
            } else if (title.equals(MONITOR_NET_TAG)) {
                items.add(MONITOR_NET_TAG_RX);
                items.add(MONITOR_NET_TAG_TX);
                items.add(MONITOR_NET_TAG_RATE);
            } else if (title.equals(MONITOR_CHART_TAG)) {
                items.add(MONITOR_CHART_TAG_HEAP);
                items.add(MONITOR_CHART_TAG_PSS);
            } else if (title.equals(MONITOR_CPU_TAG)) {
                items.add(MONITOR_CPU_TAG_PERCENTAGE);
            }
            return items;
        }
    }
}
