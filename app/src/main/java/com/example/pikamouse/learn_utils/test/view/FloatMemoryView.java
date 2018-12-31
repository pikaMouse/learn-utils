package com.example.pikamouse.learn_utils.test.view;

import android.content.Context;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.window.FloatMemWindow;
import com.example.pikamouse.learn_utils.test.window.FloatWindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * create by jiangfeng 2018/12/30
 *
 * Description: <悬浮内存显示曲线图，M为内存单位>
 */

public class FloatMemoryView extends RelativeLayout {

    private static final String VALUE_FORMAT = "%.1fM";
    private static final String VALUE_FORMAT_TXT = "%1$s:%2$.1fM";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MEMORY_TYPE_PSS, MEMORY_TYPE_HEAP})
    public @interface MemoryType {
    }

    public static final String MEMORY_TYPE_PSS = "pss";
    public static final String MEMORY_TYPE_HEAP = "heap";

    public static class Config {
        public int height = WindowManager.LayoutParams.MATCH_PARENT;
        public int width = WindowManager.LayoutParams.MATCH_PARENT;
        public int padding = 0;
        public int x = 0;
        public int y = 0;
        public int dataSize = 10;                            //采样数量
        public int yPartCount = 5;                          //纵坐标刻度数
        public
        @MemoryType
        String type;
    }

    private FloatMemWindow mFloatWindow;

    private CurveChartView mCurveChartView;

    private TextView mNameAndValueTv;

    public FloatMemoryView(Context context) {
        this(context, null);
    }

    public FloatMemoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mFloatWindow = new FloatMemWindow(getContext());
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_chart));
        inflate(getContext(), R.layout.mem_monitor_view_floatcurveview, this);
        mCurveChartView = findViewById(R.id.mem_monitor_view_floatcurveview);
        mNameAndValueTv = findViewById(R.id.mem_monitor_view_namevalue);
    }

    private String mPrefix;

    public void attachToWindow(Config config) {
        mPrefix = config.type;
        CurveChartConfig.Builder builder = new CurveChartConfig.Builder();
        builder.setYFormat(VALUE_FORMAT)
                .setDataSize(config.dataSize)
                .setMaxValueMulti(1.2f)
                .setMinValueMulti(0.8f)
                .setXTextPadding(70)
                .setYPartCount(config.yPartCount)
                .setYLabelSize(20f);
        mCurveChartView.setUp(builder.create());
        mCurveChartView.setPadding(config.padding, config.padding, config.padding, config.padding);
        WindowManager.LayoutParams layoutParams = new FloatWindow.WMLayoutParamsBuilder()
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .setWidth(config.width)
                .setHeight(config.height)
                .build();
        mFloatWindow.attachToWindow(this, layoutParams);
    }

    public void release() {
        mFloatWindow.release();
    }

    public void setText(float value) {
        mNameAndValueTv.setText(String.format(VALUE_FORMAT_TXT, mPrefix, value));
    }

    public void addData(float data) {
        mCurveChartView.addData(data);
    }

}
