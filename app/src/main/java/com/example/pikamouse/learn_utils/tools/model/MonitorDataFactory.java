package com.example.pikamouse.learn_utils.tools.model;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Build;
import android.widget.Toast;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.AllInfoMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.IMonitor;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.util.CpuUtil;
import com.example.pikamouse.learn_utils.tools.util.FrameUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: jiangfeng
 * @date: 2019/1/19
 */
public class MonitorDataFactory {

    private static volatile MonitorDataFactory sMonitorDataFactory;
    private IMonitorData mData;
    private Context mContext;
    private int mProcessUid;
    private boolean isMem;
    private boolean isNet;
    private boolean isCpu;
    private boolean isFrame;
    private final static int DURATION = 1000;
    private AllInfoTimerTask mTask;
    private Timer mTimer;
    private int mTipNum;
    private int mCpuItem;


    private MonitorDataFactory(Context context) {
        mContext = context;
    }

    public static MonitorDataFactory getInstance(Context context) {
        if (sMonitorDataFactory == null) {
            synchronized (sMonitorDataFactory) {
                if (sMonitorDataFactory == null) {
                    sMonitorDataFactory = new MonitorDataFactory(context);
                }
            }
        }
        return sMonitorDataFactory;
    }

    public void createAllInfoData(IMonitorData data) {
        mData = data;
        isMem = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_MEM_TAG);
        isNet = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_NET_TAG);
        isCpu = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_CPU_TAG);
        isFrame = MonitorManager.ItemBuilder.isExitItem(MonitorManager.MONITOR_FRAME_TAG);
        if (isFrame) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                FrameUtil.getInstance().getFrameInfo(new FrameUtil.CallBack() {
                    @Override
                    public void onSuccess(int value) {
                        mData.createFrame(value + "");
                    }
                });
            }
        }
        mTask = new AllInfoTimerTask();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTask, 0, DURATION);
    }

    public float createChartData(String tag) {
        return 0;
    }

    public void release() {
        isFrame = false;
        isCpu = false;
        isMem = false;
        isNet = false;
    }



    private class AllInfoTimerTask extends TimerTask {

        private long mLastTotalRxBytes = TrafficStats.getTotalRxBytes();
        private long mLastTimeStamp = System.currentTimeMillis();


        public AllInfoTimerTask() {
            if (isMem) {
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
                                if (mTipNum < 1) {
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
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

        @Override
        public void run() {


        }
    }
}
