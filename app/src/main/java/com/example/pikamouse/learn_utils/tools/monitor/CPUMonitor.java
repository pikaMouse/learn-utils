package com.example.pikamouse.learn_utils.tools.monitor;

import android.content.Context;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.tools.util.CpuUtil;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatCPUView;
import com.example.pikamouse.learn_utils.tools.window.DefaultWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/7
 */
public class CPUMonitor implements IMonitor {

    private Context mContext;
    private FloatCPUView mFloatCPUView;
    private DefaultWindow mFloatCPUWindow;
    private final static int DURATION = 1000;
    private Timer mTimer;
    private CPUTimerTask mTask;

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public void start(String tag) {
        List<String> items = MonitorManager.ItemBuilder.getItems(tag);
        if (mFloatCPUView == null) {
            mFloatCPUView = new FloatCPUView(mContext);
        }
        mFloatCPUWindow = new DefaultWindow(mContext);
        mFloatCPUView.setViewVisibility(items);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setX(DisplayUtil.getScreenWidth(mContext) - mFloatCPUView.getMeasuredWidth())
                .setY(0)
                .build();
        mFloatCPUWindow.attachToWindow(mFloatCPUView, layoutParams);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTask = new CPUTimerTask();
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    @Override
    public void stop() {
        if (mFloatCPUWindow != null) {
            mFloatCPUWindow.release();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class CPUTimerTask extends TimerTask {

        @Override
        public void run() {
            final String str = CpuUtil.getCPURateDesc() + "%";
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFloatCPUView.setData(str);
                }
            });
        }
    }
}
