package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.example.pikamouse.learn_utils.MonitorManager;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ProcessUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatMemoryView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * create by jiangfeng 2018/12/30
 */
public class MemoryMonitor implements IMonitor{

    private final static String TAG = "MemoryMonitor";
    private Context mContext;
    private Timer mTimer;
    private FloatMemoryView mFloatMemoryView;
    private boolean mIsRunning;

    private static final long DURATION = 500;

    @Override
    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("u must init with application context");
        }
        this.mContext = context;
    }

    @Override
    public void start(final @MonitorManager.MonitorType String type) {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        if (mFloatMemoryView == null) {
            mFloatMemoryView = new FloatMemoryView(mContext);
        }
        FloatMemoryView.Config config = new FloatMemoryView.Config();
        config.height = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_height);
        config.padding = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_padding);
        config.dataSize = 40;
        config.yPartCount = 8;
        config.type = type;
        mFloatMemoryView.attachToWindow(config);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        TimerTask timerTask = null;
        switch (type) {
            case MonitorManager.MONITOR_MEMORY_PSS_TYPE:
                timerTask = new PssTimerTask(mContext, mFloatMemoryView);
                break;
            case MonitorManager.MONITOR_MEMORY_HEAP_TYPE:
                timerTask = new HeapTimerTask(mFloatMemoryView);
                break;
            default:
                break;
        }
        mTimer.scheduleAtFixedRate(timerTask, 0, DURATION);
        mIsRunning = true;
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
            Log.d(TAG, "pssInfo.mTotalPss: " + (pssInfo != null ? pssInfo.mTotalPss : 0));
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
            Log.d(TAG, "dalvikHeapMem.mAllocatedMem: " + dalvikHeapMem.mAllocatedMem);
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
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mFloatMemoryView != null) {
            mFloatMemoryView.release();
            mFloatMemoryView = null;
        }
        mIsRunning = false;
    }

}
