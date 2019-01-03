package com.example.pikamouse.learn_utils.test;

import android.app.Application;
import android.content.Context;
import android.net.TrafficStats;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.test.util.ThreadUtil;
import com.example.pikamouse.learn_utils.test.view.FloatNetInfoView;
import com.example.pikamouse.learn_utils.test.window.FloatNetInfoWindow;
import com.example.pikamouse.learn_utils.test.window.FloatWindow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class NetInfoMonitor implements IMonitor{

    private Context mContext;
    private Timer mTimer;
    private FloatNetInfoView mView;
    private FloatNetInfoWindow mWindow;
    private NetInfoTask mTask;
    private final static long DURATION = 500;
    private int mProcessUid;


//    private NetInfoMonitor() {
//
//    }
//
//    private static class SingleHolder {
//        private final static NetInfoMonitor INSTANCE = new NetInfoMonitor();
//    }
//
//    public static NetInfoMonitor getInstance() {
//        return SingleHolder.INSTANCE;
//    }
    @Override
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
        mView = new FloatNetInfoView(mContext);
        mWindow = new FloatNetInfoWindow(mContext);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder().build();
        mWindow.attachToWindow(mView, layoutParams);
        mProcessUid = android.os.Process.myUid();
        mTimer = new Timer();
        mTask = new NetInfoTask();
        mTimer.scheduleAtFixedRate(mTask, 0 , DURATION);
    }

    private class NetInfoTask extends TimerTask {

        @Override
        public void run() {
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.setData(TrafficStats.getUidTxBytes(mProcessUid), TrafficStats.getUidRxBytes(mProcessUid));
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
        if (mWindow != null) {
            mWindow.release();
        }
    }

}
