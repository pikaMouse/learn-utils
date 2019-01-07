package com.example.pikamouse.learn_utils.tools.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.window.FloatChartWindow;
import com.example.pikamouse.learn_utils.tools.window.FloatWindow;

import java.util.Locale;

/**
 * create by jiangfeng 2018/12/30
 *
 * Description: <悬浮内存显示曲线图，M为内存单位>
 */

public class FloatChartView extends RelativeLayout {

    private static final String VALUE_FORMAT = "%.1fM";
    private static final String VALUE_FORMAT_TXT = "%1$s:%2$.1fM";

    public static class Config {
        public int mHeight = 0;
        public int mWidth = 0;
        public int mPadding = 0;
        public int mX = 0;
        public int mY = 0;
        public int mDataSize = 10;                            //采样数量
        public int mYPartCount = 5;                          //纵坐标刻度数
        public @MonitorManager.MonitorTag String mTag;
    }

    private FloatChartWindow mFloatWindow;

    private CurveChartView mCurveChartView;

    private TextView mNameAndValueTv;

    public FloatChartView(Context context) {
        this(context, null);
    }

    public FloatChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mFloatWindow = new FloatChartWindow(getContext());
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.monitor_bg_chart));
        inflate(getContext(), R.layout.monitor_layout_chart_curveview, this);
        mCurveChartView = findViewById(R.id.mem_monitor_view_floatcurveview);
        mNameAndValueTv = findViewById(R.id.mem_monitor_view_namevalue);
    }

    private String mPrefix;

    public void attachToWindow(Config config) {
        mPrefix = config.mTag;
        CurveChartConfig.Builder builder = new CurveChartConfig.Builder();
        builder.setYFormat(VALUE_FORMAT)
                .setDataSize(config.mDataSize)
                .setMaxValueMulti(1.2f)
                .setMinValueMulti(0.8f)
                .setXTextPadding(70)
                .setYPartCount(config.mYPartCount)
                .setYLabelSize(20f);
        mCurveChartView.setUp(builder.create());
        mCurveChartView.setPadding(config.mPadding, config.mPadding, config.mPadding, config.mPadding);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setWidth(config.mWidth)
                .setHeight(config.mHeight)
                .setY(config.mY)
                .setX(config.mX)
                .build();
        mFloatWindow.attachToWindow(this, layoutParams);
    }

    public void release() {
        mFloatWindow.release();
    }

    public void setText(float value) {
        mNameAndValueTv.setText(String.format(Locale.getDefault(),VALUE_FORMAT_TXT, mPrefix, value));
    }

    public void addData(float data) {
        mCurveChartView.addData(data);
    }

}
