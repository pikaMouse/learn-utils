package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.util.CpuUtil;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.FrameUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatInfoView;
import com.example.pikamouse.learn_utils.tools.window.DefaultWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class AllInfoMonitor implements IMonitor{

    private Context mContext;
    private FloatInfoView mAllInfoView;
    private DefaultWindow mAllInfoWindow;
    private int mProcessUid;
    private String mTag;
    private final static int DURATION = 1000;

    private AllInfoTimerTask mTask;
    private Timer mTimer;
    private int mTipNum;
    private int mCpuItem;

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
        mTag = tag;
        mAllInfoView = new FloatInfoView(mContext);
        mAllInfoView.setViewVisibility(tag);
        mAllInfoWindow = new DefaultWindow(mContext);
        mProcessUid = Process.myUid();
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                //可以唤起输入法，不接受任何触摸事件全部由下层window接受
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setX(DisplayUtil.getScreenWidth(mContext) - mAllInfoView.getMeasuredWidth())
                .setY(0)
                .build();
        mAllInfoWindow.attachToWindow(mAllInfoView, layoutParams);
        if (mTag.equals(MonitorManager.MONITOR_TOTAL_TAG)) {
            mCpuItem = MonitorManager.ItemBuilder.getItemsSize(MonitorManager.MONITOR_CPU_TAG);
        } else if (mTag.equals(MonitorManager.MONITOR_FRAME_TAG) || mTag.equals(MonitorManager.MONITOR_TOTAL_TAG)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                FrameUtil.getInstance().getFrameInfo(new FrameUtil.CallBack() {
                    @Override
                    public void onSuccess(int value) {
                        mAllInfoView.setFrame(value + "");
                    }
                });
            }
        }
        mTimer = new Timer();
        mTask = new AllInfoTimerTask();
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    @Override
    public void stop() {
        if (mAllInfoWindow != null) {
            mAllInfoWindow.release();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class AllInfoTimerTask extends TimerTask {

        private long mLastTotalRxBytes = TrafficStats.getTotalRxBytes();
        private long mLastTimeStamp = System.currentTimeMillis();

        @Override
        public void run() {
            if (mTag.equals(MonitorManager.MONITOR_TOTAL_TAG) || mTag.equals(MonitorManager.MONITOR_MEM_TAG)) {
                final MemoryUtil.AllInfo allInfo = MemoryUtil.getMemoryInfoSync(mContext);
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAllInfoView != null) mAllInfoView.setMemoryData(allInfo);
                    }
                });
            }
            if (mTag.equals(MonitorManager.MONITOR_TOTAL_TAG) || mTag.equals(MonitorManager.MONITOR_NET_TAG)) {
                try {
                    long nowTotalRxBytes = TrafficStats.getTotalRxBytes();
                    long nowTimeStamp = System.currentTimeMillis();
                    final long tx = TrafficStats.getUidTxBytes(mProcessUid);
                    final long rx = TrafficStats.getUidRxBytes(mProcessUid);
                    final long rate = ((nowTotalRxBytes - mLastTotalRxBytes) * 1000 / (nowTimeStamp - mLastTimeStamp));
                    mLastTotalRxBytes = nowTotalRxBytes;
                    mLastTimeStamp = nowTimeStamp;
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mAllInfoView != null) mAllInfoView.setNetData(tx, rx, rate);
                        }
                    });
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }
            }
            if (mCpuItem > 0 || mTag.equals(MonitorManager.MONITOR_CPU_TAG)) {
                 CpuUtil.getInstance().getCPUData(mContext, new CpuUtil.CallBack() {
                    @Override
                    public void onSuccess(final float value) {
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mAllInfoView != null) mAllInfoView.setCPUData(value);
                            }
                        });
                    }

                    @Override
                    public void onFail(final String err) {
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mTipNum < 1) {
                                    Toast.makeText(mContext, err, Toast.LENGTH_LONG).show();
                                    mTipNum++;
                                } else if (mTipNum < 3){
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.cpu_monitor_tip), Toast.LENGTH_LONG).show();
                                    mTipNum++;
                                }
                            }
                        });
                    }
                });

            }

        }
    }
}
