package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatMemoryView;
import com.example.pikamouse.learn_utils.tools.window.FloatMemoryWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class MemoryMonitor implements IMonitor{

    private Context mContext;
    private FloatMemoryView mFloatAllInfoView;
    private FloatMemoryWindow mFloatMemoryWindow;

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
    public void start(String tag) {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        List<String> list = MonitorManager.ItemBuilder.getItems(tag);
        mFloatAllInfoView = new FloatMemoryView(mContext);
        mFloatAllInfoView.setViewVisibility(list);
        mFloatMemoryWindow = new FloatMemoryWindow(mContext);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                //可以唤起输入法，不接受任何触摸事件全部由下层window接受
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setX(DisplayUtil.getScreenWidth(mContext) - mFloatAllInfoView.getMeasuredWidth())
                .setY(0)
                .build();
        mFloatMemoryWindow.attachToWindow(mFloatAllInfoView, layoutParams);
        mTimer = new Timer();
        mTask = new AllInfoTimerTask();
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    @Override
    public void stop() {
        if (mFloatMemoryWindow != null) {
            mFloatMemoryWindow.release();
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
