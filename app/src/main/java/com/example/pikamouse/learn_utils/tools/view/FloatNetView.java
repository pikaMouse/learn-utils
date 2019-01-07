package com.example.pikamouse.learn_utils.tools.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public class FloatNetView extends ConstraintLayout {

    private TextView mRX;
    private TextView mTX;
    private TextView mRate;
    private ConstraintLayout mTxContainer;
    private ConstraintLayout mRxContainer;
    private ConstraintLayout mRateContainer;
    private Map<String, ConstraintLayout> mContainers = new HashMap<>();
    private static final String VALUE_FORMAT_TXT = "%.2f";


    public FloatNetView(Context context) {
        this(context, null);
    }

    public FloatNetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatNetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.monitor_layout_net_info, this);
        setBackgroundColor(getResources().getColor(R.color.monitor_bg_chart));
        setPadding(DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(10));
        mTX = findViewById(R.id.net_monitor_send_flow_info);
        mRX = findViewById(R.id.net_monitor_receive_flow_info);
        mRate = findViewById(R.id.net_monitor_rate_info);
        mTxContainer = findViewById(R.id.net_monitor_tx_container);
        mRxContainer = findViewById(R.id.net_monitor_rx_container);
        mRateContainer = findViewById(R.id.net_monitor_rate_container);
        mRxContainer.setVisibility(GONE);
        mTxContainer.setVisibility(GONE);
        mRateContainer.setVisibility(GONE);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_TX, mTxContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_RX, mRxContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_RATE, mRateContainer);
    }

    public void setViewVisibility(List<String> item) {
        if (item.isEmpty()) {
            Collection<ConstraintLayout> list = mContainers.values();
            for (ConstraintLayout c : list) {
                c.setVisibility(VISIBLE);
            }
        } else {
            for (String s : item) {
                mContainers.get(s).setVisibility(VISIBLE);
            }
        }
    }

    public void setData(long txByte, long rxByte, long rate) {
        String txStr = String.format(Locale.getDefault(), VALUE_FORMAT_TXT,(float)txByte / 1024 / 1024) + "M";
        if (mTX != null) mTX.setText(txStr);
        String rxStr = String.format(Locale.getDefault(), VALUE_FORMAT_TXT,(float)rxByte / 1024 / 1024) + "M";
        if (mRX != null) mRX.setText(rxStr);
        String rateStr = String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)(rate / 1024)) + "k/s";
        if (mRate != null) mRate.setText(rateStr);
    }
}
