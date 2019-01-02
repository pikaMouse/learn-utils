package com.example.pikamouse.learn_utils.test;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.test.util.DisplayUtil;
import com.example.pikamouse.learn_utils.test.util.MemoryUtil;
import com.example.pikamouse.learn_utils.test.util.ThreadUtil;
import com.example.pikamouse.learn_utils.test.view.FloatAllInfoView;
import com.example.pikamouse.learn_utils.test.window.FloatAllInfoWindow;
import com.example.pikamouse.learn_utils.test.window.FloatWindow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class AllInfoMonitor {

    private Context mContext;
    private FloatAllInfoView mFloatAllInfoView;
    private FloatAllInfoWindow mFloatAllInfoWindow;

    private final static int DURATION = 500;

    private AllInfoTimerTask mTask;
    private Timer mTimer;

    private AllInfoMonitor() {

    }
    private static class SingleHolder {
        private final static AllInfoMonitor ALL_INFO_MONITOR = new AllInfoMonitor();
    }
    public static AllInfoMonitor getInstance() {
        return SingleHolder.ALL_INFO_MONITOR;
    }

    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("you must init with application context");
        }
        mContext = context;
    }

    public void start() {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        mFloatAllInfoView = new FloatAllInfoView(mContext);
        mFloatAllInfoWindow = new FloatAllInfoWindow(mContext);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setX(DisplayUtil.getScreenWidth(mContext) - mFloatAllInfoView.getMeasuredWidth())
                .setY(0)
                .build();
        mFloatAllInfoWindow.attachToWindow(mFloatAllInfoView, layoutParams);

        mTimer = new Timer();
        mTask = new AllInfoTimerTask();
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    public void stop() {
        if (mFloatAllInfoView != null) {
            mFloatAllInfoWindow.release();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
    private class AllInfoTimerTask extends TimerTask {
        @Override
        public void run() {
            final MemoryUtil.AllInfo allInfo = MemoryUtil.getMemoryInfoSync(mContext);
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFloatAllInfoView.setData(allInfo);
                }
            });
        }
    }
}
