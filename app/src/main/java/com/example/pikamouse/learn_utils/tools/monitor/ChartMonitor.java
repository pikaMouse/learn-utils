package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;


import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.model.MonitorDataFactory;
import com.example.pikamouse.learn_utils.tools.model.MonitorDataImpl;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatChartView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * create by jiangfeng 2018/12/30
 */
public class ChartMonitor implements IMonitor{

    private final static String TAG = "ChartMonitor";
    private Context mContext;
    private int mLocation = 0;
    private static Map<String, FloatChartView> sFloatMemoryViews = new HashMap<>();
    private FloatChartView mFloatMemoryView;
    private boolean isPss;
    private boolean isHeap;

    private MonitorDataImpl mDataListener = new MonitorDataImpl() {
        @Override
        public void createMemoryData(MemoryUtil.AllInfo allInfo) {
            if (isPss) {
                final FloatChartView view = sFloatMemoryViews.get(MonitorManager.MONITOR_CHART_TAG_PSS);
                if (view != null && allInfo != null && allInfo.mPssInfo != null) {
                    final float value = (float)(allInfo.mPssInfo.mTotalPss /1024);
                    view.addData(value);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setText(value);
                        }
                    });

                }
            }
            if (isHeap) {
                final FloatChartView view = sFloatMemoryViews.get(MonitorManager.MONITOR_CHART_TAG_HEAP);
                if (view != null && allInfo != null && allInfo.mDalvikHeapMem != null) {
                    final float value = (float)(allInfo.mDalvikHeapMem.mAllocatedMem / 1024);
                    view.addData(value);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setText(value);
                        }
                    });

                }
            }
        }
    };

    @Override
    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("u must init with application context");
        }
        this.mContext = context;
    }

    @Override
    public void start(final @MonitorManager.MonitorTag String tag) {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        List<String> items = MonitorManager.ItemBuilder.getItems(tag);
        if (items.isEmpty()) {
            return;
        }
        for (String item : items) {
            if (item.equals(MonitorManager.MONITOR_CHART_TAG_HEAP)) {
                isHeap = true;
            }
            if (item.equals(MonitorManager.MONITOR_CHART_TAG_PSS)) {
                isPss = true;
            }
            if (sFloatMemoryViews.get(item) == null) {
                mFloatMemoryView = new FloatChartView(mContext);
                sFloatMemoryViews.put(item, mFloatMemoryView);
            } else {
                mFloatMemoryView = sFloatMemoryViews.get(item);
            }
            FloatChartView.Config config = new FloatChartView.Config();
            config.mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.monitor_chart_height);
            config.mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.monitor_chart_width);
            config.mPadding = mContext.getResources().getDimensionPixelSize(R.dimen.monitor_chart_padding);
            config.mDataSize = 40;
            config.mYPartCount = 8;
            config.mTag = item;
            config.mY = mLocation;
            config.mX = DisplayUtil.getScreenWidth(mContext) - mContext.getResources().getDimensionPixelSize(R.dimen.monitor_chart_width);
            mLocation = mLocation + mContext.getResources().getDimensionPixelSize(R.dimen.monitor_chart_height);
            mFloatMemoryView.attachToWindow(config);
        }
        MonitorDataFactory.getInstance(mContext).subscribeAllInfoData(tag, mDataListener);
    }

    @Override
    public void stop() {
        if (sFloatMemoryViews != null) {
            for (FloatChartView floatMemoryView : sFloatMemoryViews.values()) {
                floatMemoryView.release();
            }
            sFloatMemoryViews.clear();
        }
        mLocation = 0;
        isHeap = false;
        isPss = false;
        MonitorDataFactory.getInstance(mContext).release();
    }

}
