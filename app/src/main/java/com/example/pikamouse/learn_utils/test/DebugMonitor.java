package com.example.pikamouse.learn_utils.test;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.test.util.DisplayUtil;
import com.example.pikamouse.learn_utils.test.view.FloatBallView;
import com.example.pikamouse.learn_utils.test.window.FloatBallWindow;
import com.example.pikamouse.learn_utils.test.window.FloatWindow;

/**
 * create by jiangfeng 2018/12/30
 */
public class DebugMonitor {

    private final static String TAG = "DebugMonitor";

    private Context mContext;
    private FloatBallView mBall;
    private FloatBallWindow mFloatBallWin;

    private static class SingleHolder {
      private final static DebugMonitor DEBUG_MONITOR = new DebugMonitor();
    }

    private DebugMonitor() {

    }

    public static DebugMonitor getInstance() {
        return SingleHolder.DEBUG_MONITOR;
    }

    public void init(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("you must init with application context");
        }
        MemoryMonitor.getInstance().init(context);
        AllInfoMonitor.getInstance().init(context);
        mContext = context;
    }

    public void start() {
        if (mContext == null) {
            throw new IllegalStateException("init must be called");
        }
        stop();
        mBall = new FloatBallView(mContext);
        mFloatBallWin = new FloatBallWindow(mBall.getContext());
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                .setX((DisplayUtil.getScreenWidth(mContext) * 4 / 5))
                .setY((DisplayUtil.getScreenHeight(mContext) * 4 / 5))
                .build();
        mFloatBallWin.attachToWindow(mBall, layoutParams);
    }

    public void stop() {
        if (mFloatBallWin != null) {
            mFloatBallWin.release();
        }
        MemoryMonitor.getInstance().stop();
        AllInfoMonitor.getInstance().stop();
    }




}
