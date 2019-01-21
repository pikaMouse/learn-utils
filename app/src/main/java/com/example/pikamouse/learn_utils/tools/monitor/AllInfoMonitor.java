package com.example.pikamouse.learn_utils.tools.monitor;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;
import com.example.pikamouse.learn_utils.tools.model.MonitorDataFactory;
import com.example.pikamouse.learn_utils.tools.model.MonitorDataImpl;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;
import com.example.pikamouse.learn_utils.tools.util.ThreadUtil;
import com.example.pikamouse.learn_utils.tools.view.FloatInfoView;
import com.example.pikamouse.learn_utils.tools.window.DefaultWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;


/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class AllInfoMonitor implements IMonitor{

    private Context mContext;
    private FloatInfoView mAllInfoView;
    private DefaultWindow mAllInfoWindow;

    private MonitorDataImpl mDataListener = new MonitorDataImpl() {
        @Override
        public void createMemoryData(final MemoryUtil.AllInfo allInfo) {
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAllInfoView != null) mAllInfoView.setMemoryData(allInfo);
                }
            });
        }

        @Override
        public void createFrame(final String frame) {
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAllInfoView.setFrame(frame + "");
                }
            });
        }

        @Override
        public void createNetData(final long txByte, final long rxByte, final long rate) {
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAllInfoView != null) mAllInfoView.setNetData(txByte, rxByte, rate);
                }
            });
        }

        @Override
        public void createCPUData(final float value) {
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAllInfoView != null) mAllInfoView.setCPUData(value);
                }
            });
        }
    };

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
        mAllInfoView = new FloatInfoView(mContext);
        mAllInfoView.setViewVisibility(tag);
        mAllInfoWindow = new DefaultWindow(mContext);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                //可以唤起输入法，不接受任何触摸事件全部由下层window接受
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setX(DisplayUtil.getScreenWidth(mContext) - mAllInfoView.getMeasuredWidth())
                .setY(0)
                .build();
        mAllInfoWindow.attachToWindow(mAllInfoView, layoutParams);
        MonitorDataFactory.getInstance(mContext).subscribeAllInfoData(tag, mDataListener);
    }

    @Override
    public void stop() {
        if (mAllInfoWindow != null) {
            mAllInfoWindow.release();
        }
        MonitorDataFactory.getInstance(mContext).release();
    }
}
