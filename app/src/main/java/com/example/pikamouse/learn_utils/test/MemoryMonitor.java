package com.example.pikamouse.learn_utils.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.util.MemoryUtil;
import com.example.pikamouse.learn_utils.test.util.ProcessUtil;
import com.example.pikamouse.learn_utils.test.view.FloatContainerView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description: <文件描述>
 * Author: hui.zhao
 * Date: 2018/9/29
 * Copyright: Ctrip
 */

public class MemoryMonitor {

    private final static String TAG = "MemoryMonitor";

    private static class InstanceHolder {
        private static MemoryMonitor sInstance = new MemoryMonitor();
    }

    private MemoryMonitor() {
    }

    public static MemoryMonitor getInstance() {
        return InstanceHolder.sInstance;
    }

    private Context mContext;
    private Timer mTimer;
    private FloatContainerView mFloatContainerView;
    private boolean mIsRunning;

    private static final long DURATION = 500;

    private FloatContainerView.Callback mCallBack = new FloatContainerView.CallbackAdapter() {
        @Override
        public void onClose() {
            stop();
        }
    };


    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("u must init with application context");
        }
        this.mContext = context;
    }

    public void start(final @FloatContainerView.MemoryType String type) {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        if (mFloatContainerView == null) {
            mFloatContainerView = new FloatContainerView(mContext);
        }
        FloatContainerView.Config config = new FloatContainerView.Config();
        config.height = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_height);
        config.padding = mContext.getResources().getDimensionPixelSize(R.dimen.mem_monitor_padding);
        config.dataSize = 40;
        config.yPartCount = 8;
        config.type = type;
        mFloatContainerView.attachToWindow(config);
        mFloatContainerView.setCallback(mCallBack);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        TimerTask timerTask = null;
        switch (type) {
            case FloatContainerView.MEMORY_TYPE_PSS:
                timerTask = new PssTimerTask(mContext, mFloatContainerView);
                break;
            case FloatContainerView.MEMORY_TYPE_HEAP:
                timerTask = new HeapTimerTask(mFloatContainerView);
                break;
            default:
                break;
        }
        mTimer.scheduleAtFixedRate(timerTask, 0, DURATION);
        mIsRunning = true;
    }

    public static class PssTimerTask extends MemoryTimerTask {
        private Context mContext;

        public PssTimerTask(Context context, FloatContainerView floatCurveView) {
            super(floatCurveView);
            this.mContext = context;
        }

        @Override
        public float getValue() {
            final int pid = ProcessUtil.getCurrentPid();
            MemoryUtil.PssInfo pssInfo = MemoryUtil.getAppPssInfo(mContext, pid);
            Log.d(TAG, "pssInfo.totalPss: " + pssInfo.totalPss);
            return (float) pssInfo.totalPss / 1024;
        }
    }

    public static class HeapTimerTask extends MemoryTimerTask {

        public HeapTimerTask(FloatContainerView floatCurveView) {
            super(floatCurveView);
        }

        @Override
        public float getValue() {
            final MemoryUtil.DalvikHeapMem dalvikHeapMem = MemoryUtil.getAppDalvikHeapMem();
            Log.d(TAG, "dalvikHeapMem.allocated: " + dalvikHeapMem.allocated);
            return (float) dalvikHeapMem.allocated / 1024;
        }
    }

    public static abstract class MemoryTimerTask extends TimerTask {
        protected FloatContainerView mFloatContainerView;

        public MemoryTimerTask(FloatContainerView floatCurveView) {
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


    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mFloatContainerView != null) {
            mFloatContainerView.release();
            mFloatContainerView = null;
        }
        mIsRunning = false;
    }

    public void toggle(final @FloatContainerView.MemoryType String type) {
        if (mIsRunning) {
            stop();
        } else {
            start(type);
        }
    }

}
