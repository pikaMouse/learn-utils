package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;


import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ProcessUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatMemoryView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create by jiangfeng 2018/12/30
 */
public class MemoryMonitor implements IMonitor{

    private final static String TAG = "MemoryMonitor";
    private Context mContext;
    private int mY = 0;
    private static Map<String, Timer> sTimers = new HashMap<>();
    private static Map<String, FloatMemoryView> sFloatMemoryViews = new HashMap<>();

    private Timer mTimer;
    private FloatMemoryView mFloatMemoryView;

    private static final long DURATION = 500;

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
            items.add(MonitorManager.MONITOR_CHART_TAG_HEAP);
        }
        for (String item : items) {
            if (sFloatMemoryViews.get(item) == null) {
                mFloatMemoryView = new FloatMemoryView(mContext);
                sFloatMemoryViews.put(item, mFloatMemoryView);
            } else {
                mFloatMemoryView = sFloatMemoryViews.get(item);
            }
            FloatMemoryView.Config config = new FloatMemoryView.Config();
            config.height = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_height);
            config.width = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_width);
            config.padding = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_padding);
            config.dataSize = 40;
            config.yPartCount = 8;
            config.type = item;
            config.y = mY;
            mY = mY + mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_height);
            mFloatMemoryView.attachToWindow(config);
            if (sTimers.get(item) == null) {
                mTimer = new Timer();
                sTimers.put(item, mTimer);
            } else {
                mTimer = sTimers.get(item);
            }
            TimerTask timerTask;
            switch (item) {
                case MonitorManager.MONITOR_CHART_TAG_PSS:
                    timerTask = new PssTimerTask(mContext, mFloatMemoryView);
                    break;
                case MonitorManager.MONITOR_CHART_TAG_HEAP:
                    timerTask = new HeapTimerTask(mFloatMemoryView);
                    break;
                default:
                    timerTask = new HeapTimerTask(mFloatMemoryView);
                    break;
            }
            mTimer.scheduleAtFixedRate(timerTask, 0, DURATION);
        }
    }

    public static class PssTimerTask extends MemoryTimerTask {
        private Context mContext;

        public PssTimerTask(Context context, FloatMemoryView floatCurveView) {
            super(floatCurveView);
            this.mContext = context;
        }

        @Override
        public float getValue() {
            final int pid = ProcessUtil.getCurrentPid();
            MemoryUtil.PssInfo pssInfo = MemoryUtil.getAppPssInfo(mContext, pid);
            return (float) (pssInfo != null ? pssInfo.mTotalPss : 0) / 1024;
        }
    }

    public static class HeapTimerTask extends MemoryTimerTask {

        public HeapTimerTask(FloatMemoryView floatCurveView) {
            super(floatCurveView);
        }

        @Override
        public float getValue() {
            final MemoryUtil.DalvikHeapMem dalvikHeapMem = MemoryUtil.getAppDalvikHeapMem();
            return (float) dalvikHeapMem.mAllocatedMem / 1024;
        }
    }

    public static abstract class MemoryTimerTask extends TimerTask {
        protected FloatMemoryView mFloatContainerView;

        public MemoryTimerTask(FloatMemoryView floatCurveView) {
            this.mFloatContainerView = floatCurveView;
        }

        public abstract float getValue();

        @Override
        public void run() {
            mFloatContainerView.addData(getValue());
            mFloatContainerView.post(new Runnable() {
                @Override
                public void run() {
                    mFloatContainerView.setText(getValue());
                }
            });
        }
    }

    @Override
    public void stop() {
        if (sTimers != null) {
            for (Timer timer :sTimers.values()){
                timer.cancel();
            }
            sTimers.clear();
        }
        if (sFloatMemoryViews != null) {
            for (FloatMemoryView floatMemoryView : sFloatMemoryViews.values()) {
                floatMemoryView.release();
            }
            sFloatMemoryViews.clear();
        }
        mY = 0;
    }

}
