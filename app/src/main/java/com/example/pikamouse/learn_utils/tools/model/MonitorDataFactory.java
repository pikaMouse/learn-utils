package com.example.pikamouse.learn_utils.tools.model;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Process;
import android.widget.Toast;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.util.CpuUtil;
import com.example.pikamouse.learn_utils.tools.util.FrameUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/19
 */
public class MonitorDataFactory {

    private static volatile MonitorDataFactory sInstance;
    private IMonitorData mData;
    private Context mContext;
    private int mProcessUid;
    private boolean isMem;
    private boolean isMemChart;
    private boolean isNet;
    private boolean isCpu;
    private boolean isFrame;
    private final static int DURATION = 500;
    private AllInfoTimerTask mTask;
    private Timer mTimer;
    private String mTag;


    private MonitorDataFactory(Context context) {
        mContext = context;
        mProcessUid = Process.myUid();
    }

    public static MonitorDataFactory getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MonitorDataFactory.class) {
                if (sInstance == null) {
                    sInstance = new MonitorDataFactory(context);
                }
            }
        }
        return sInstance;
    }

    public void subscribeAllInfoData(String tag, IMonitorData data) {
        mData = data;
        mTag = tag;
        starTimer();
    }

    private void starTimer() {
        if (mTag.equals(MonitorManager.MONITOR_TOTAL_TAG)) {
            isMem = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_MEM_TAG);
            isNet = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_NET_TAG);
            isCpu = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_CPU_TAG);
            isFrame = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_FRAME_TAG);
        } else {
            isMem = mTag.equals(MonitorManager.MONITOR_MEM_TAG);
            isNet = mTag.equals(MonitorManager.MONITOR_NET_TAG);
            isCpu = mTag.equals(MonitorManager.MONITOR_CPU_TAG);
            isFrame = mTag.equals(MonitorManager.MONITOR_FRAME_TAG);
            isMemChart = mTag.equals(MonitorManager.MONITOR_CHART_TAG);
        }
        if (mTask == null) {
            mTask = new AllInfoTimerTask();
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    public void release() {
        isFrame = false;
        isCpu = false;
        isMem = false;
        isNet = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTask != null) {
            mTask = null;
        }
        FrameUtil.getInstance().stopFrameInfo();
        CpuUtil.getInstance().release();
    }

    private class AllInfoTimerTask extends TimerTask {

        private long mLastTotalRxBytes = TrafficStats.getTotalRxBytes();
        private long mLastTimeStamp = System.currentTimeMillis();

        public AllInfoTimerTask() {
            super();
            if (isFrame) {
                FrameUtil.getInstance().getFrameInfo(new FrameUtil.CallBack() {
                    @Override
                    public void onSuccess(int value) {
                        mData.createFrame(value + "");
                    }
                });
            }
        }

        @Override
        public void run() {
            if (isMem || isMemChart) {
                MemoryUtil.AllInfo allInfo = MemoryUtil.getMemoryInfoSync(mContext);
                mData.createMemoryData(allInfo);
            }
            if (isNet) {
                try {
                    long nowTotalRxBytes = TrafficStats.getTotalRxBytes();
                    long nowTimeStamp = System.currentTimeMillis();
                    final long tx = TrafficStats.getUidTxBytes(mProcessUid);
                    final long rx = TrafficStats.getUidRxBytes(mProcessUid);
                    final long rate = ((nowTotalRxBytes - mLastTotalRxBytes) * 1000 / (nowTimeStamp - mLastTimeStamp));
                    mLastTotalRxBytes = nowTotalRxBytes;
                    mLastTimeStamp = nowTimeStamp;
                    mData.createNetData(tx, rx, rate);
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }
            }
            if (isCpu) {
                CpuUtil.getInstance().getCPUData(mContext, new CpuUtil.CallBack() {
                    @Override
                    public void onSuccess(float value) {
                        mData.createCPUData(value);
                    }

                    @Override
                    public void onFail(final String msg) {
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
