package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatAllInfoView;
import com.example.pikamouse.learn_utils.tools.window.FloatAllInfoWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class AllInfoMonitor implements IMonitor{

    private Context mContext;
    private FloatAllInfoView mFloatAllInfoView;
    private FloatAllInfoWindow mFloatAllInfoWindow;

    private final static int DURATION = 500;

    private AllInfoTimerTask mTask;
    private Timer mTimer;

    @Override
    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("you must init with application context");
        }
        mContext = context;
    }

    @Override
    public void start(String type) {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        mFloatAllInfoView = new FloatAllInfoView(mContext);
        mFloatAllInfoWindow = new FloatAllInfoWindow(mContext);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setX(DisplayUtil.getScreenWidth(mContext) - mFloatAllInfoView.getMeasuredWidth())
                .setY(0)
                .build();
        mFloatAllInfoWindow.attachToWindow(mFloatAllInfoView, layoutParams);

        mTimer = new Timer();
        mTask = new AllInfoTimerTask();
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    @Override
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
