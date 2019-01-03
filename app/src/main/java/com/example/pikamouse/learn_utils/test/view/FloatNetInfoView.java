package com.example.pikamouse.learn_utils.test.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.R;

import java.util.Locale;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class FloatNetInfoView extends ConstraintLayout {

    private TextView mRX;
    private TextView mTX;
    private static final String VALUE_FORMAT_TXT = "%.2f";


    public FloatNetInfoView(Context context) {
        this(context, null);
    }

    public FloatNetInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatNetInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.net_monitor_float_total_info, this);
        mTX = findViewById(R.id.net_monitor_send_flow_info);
        mRX = findViewById(R.id.net_monitor_receive_flow_info);
    }

    public void setData(long txByte, long rxByte) {
        mTX.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT,(float)txByte / 1024 / 1024));
        mRX.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT,(float)rxByte / 1024 / 1024));
    }


}
