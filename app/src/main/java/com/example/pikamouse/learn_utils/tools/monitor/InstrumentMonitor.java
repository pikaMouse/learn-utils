package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatInstrumentView;
import com.example.pikamouse.learn_utils.tools.window.FloatInstrumentWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;

/**
 * create by jiangfeng 2018/12/30
 */
public class InstrumentMonitor implements IMonitor{

    private final static String TAG = "InstrumentMonitor";

    private Context mContext;
    private FloatInstrumentView mBall;
    private FloatInstrumentWindow mFloatBallWin;


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
        mBall = new FloatInstrumentView(mContext);
        mFloatBallWin = new FloatInstrumentWindow(mBall.getContext());
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                .setX((DisplayUtil.getScreenWidth(mContext) * 4 / 5))
                .setY((DisplayUtil.getScreenHeight(mContext) * 4 / 5))
                .build();
        mFloatBallWin.attachToWindow(mBall, layoutParams);
    }

    @Override
    public void stop() {
        if (mFloatBallWin != null) {
            mFloatBallWin.release();
        }

    }




}
