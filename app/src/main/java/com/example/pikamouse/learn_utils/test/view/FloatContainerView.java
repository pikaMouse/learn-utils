package com.example.pikamouse.learn_utils.test.view;

import android.content.Context;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.window.FloatWindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description: <悬浮内存显示曲线图，M为内存单位>
 */

public class FloatContainerView extends RelativeLayout implements View.OnClickListener {

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
        public int dataSize = 10;
        public int yPartCount = 5;
        public
        @MemoryType
        String type;
    }

    private FloatWindow mFloatWindow;

    private CurveChartView mCurveChartView;

    private TextView mNameAndValueTv;

    private TextView mClose;

    private Callback mCallback;

    private WindowManager.LayoutParams mLayoutParams;

    public FloatContainerView(Context context) {
        this(context, null);
    }

    public FloatContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mFloatWindow = new FloatWindow(getContext());
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_chart));
        inflate(getContext(), R.layout.mem_monitor_view_floatcurveview, this);
        mCurveChartView = (CurveChartView) this.findViewById(R.id.mem_monitor_view_floatcurveview);
        mNameAndValueTv = (TextView) this.findViewById(R.id.mem_monitor_view_namevalue);
        mClose = (TextView) this.findViewById(R.id.mem_monitor_view_close);
        mClose.setOnClickListener(this);
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
        mFloatWindow.attachToWindow(this, Gravity.LEFT | Gravity.TOP, config.x, config.y, config.width, config.height);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mem_monitor_view_close:
                if (mCallback != null) mCallback.onClose();
                break;
            default:
                break;
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void onClose();
        void onMove(WindowManager.LayoutParams layoutParams);
    }

    public abstract static class CallbackAdapter implements Callback {
        @Override
        public void onClose() {}

        @Override
        public void onMove(WindowManager.LayoutParams layoutParams) {}
    }
}
