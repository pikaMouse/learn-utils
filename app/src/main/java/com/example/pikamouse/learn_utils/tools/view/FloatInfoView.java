package com.example.pikamouse.learn_utils.tools.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class FloatInfoView extends ConstraintLayout {

    private final static String TAG = "FloatAllInfoView";
    private final static String UNIT = "M";

    private TextView mTotalPSS;
    private TextView mDalvikPSS;
    private TextView mNativePSS;
    private TextView mOtherPSS;
    private TextView mHeapSize;
    private TextView mHeapAlloc;
    private TextView mHeapFree;
    private TextView mMemTotal;
    private TextView mMemAvail;
    private TextView mIsLowMem;

    private TextView mRX;
    private TextView mTX;
    private TextView mRate;

    private TextView mPercentage;


    private ConstraintLayout mPSSContainer;
    private ConstraintLayout mHeapContainer;
    private ConstraintLayout mSystemContainer;
    private ConstraintLayout mRxContainer;
    private ConstraintLayout mTxContainer;
    private ConstraintLayout mRateContainer;
    private ConstraintLayout mPercentageContainer;
    private Map<String,ConstraintLayout> mContainers = new HashMap<>();

    private static final String VALUE_FORMAT_TXT = "%.1f";



    public FloatInfoView(Context context) {
        this(context, null);
    }

    public FloatInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.monitor_layout_info, this);
        setBackgroundColor(getResources().getColor(R.color.monitor_bg_chart));
        setPadding(DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(10));
        mPSSContainer = findViewById(R.id.mem_monitor_container1);
        mHeapContainer = findViewById(R.id.mem_monitor_container2);
        mSystemContainer = findViewById(R.id.mem_monitor_container3);
        mRxContainer = findViewById(R.id.net_monitor_rx_container);
        mTxContainer = findViewById(R.id.net_monitor_tx_container);
        mRateContainer = findViewById(R.id.net_monitor_rate_container);
        mPercentageContainer = findViewById(R.id.cpu_monitor_percentage_container);

        mPSSContainer.setVisibility(View.GONE);
        mHeapContainer.setVisibility(View.GONE);
        mSystemContainer.setVisibility(View.GONE);
        mRxContainer.setVisibility(View.GONE);
        mTxContainer.setVisibility(View.GONE);
        mRateContainer.setVisibility(View.GONE);
        mPercentageContainer.setVisibility(GONE);

        mContainers.put(MonitorManager.MONITOR_MEM_TAG_PSS, mPSSContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_HEAP, mHeapContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_SYSTEM, mSystemContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_TX, mTxContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_RX, mRxContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_RATE, mRateContainer);
        mContainers.put(MonitorManager.MONITOR_CPU_TAG_PERCENTAGE, mPercentageContainer);

        mTotalPSS = findViewById(R.id.mem_monitor_total_pss_info);
        mDalvikPSS = findViewById(R.id.mem_monitor_dalvik_pss_info);
        mNativePSS = findViewById(R.id.mem_monitor_native_pss_info);
        mOtherPSS = findViewById(R.id.mem_monitor_other_pss_info);
        mHeapSize = findViewById(R.id.mem_monitor_total_heap_info);
        mHeapAlloc = findViewById(R.id.mem_monitor_allocated_heap_info);
        mHeapFree = findViewById(R.id.mem_monitor_free_heap_info);
        mMemTotal = findViewById(R.id.mem_monitor_total_mem_info);
        mMemAvail = findViewById(R.id.mem_monitor_avail_mem_info);
        mIsLowMem = findViewById(R.id.mem_monitor_is_low_mem_info);

        mTX = findViewById(R.id.net_monitor_send_flow_info);
        mRX = findViewById(R.id.net_monitor_receive_flow_info);
        mRate = findViewById(R.id.net_monitor_rate_info);

        mPercentage = findViewById(R.id.cpu_monitor_percentage);
    }

    public void setViewVisibility(String type) {
        List<String> items = MonitorManager.ItemBuilder.getItems(type);
        if (items.isEmpty()) {
            items = MonitorManager.ItemBuilder.getDefaultItems(type);
        }
        for (String s : items) {
            ConstraintLayout c = mContainers.get(s);
            if (c != null) c.setVisibility(VISIBLE);
        }
    }

    public void setMemoryData(MemoryUtil.AllInfo allInfo) {
        final MemoryUtil.PssInfo pssInfo = allInfo.mPssInfo;
        final MemoryUtil.DalvikHeapMem dalvikHeapMem = allInfo.mDalvikHeapMem;
        final MemoryUtil.RamMemoryInfo ramMemoryInfo = allInfo.mRamMemoryInfo;
        if (mTotalPSS != null && pssInfo != null) {
            mTotalPSS.setText(formatMemory(pssInfo.mTotalPss));
        }
        if (mDalvikPSS != null && pssInfo != null) {
            mDalvikPSS.setText(formatMemory(pssInfo.mDalvikPss));
        }
        if (mNativePSS != null && pssInfo != null) {
            mNativePSS.setText(formatMemory(pssInfo.mNativePss));
        }
        if (mOtherPSS != null && pssInfo != null) {
            mOtherPSS.setText(formatMemory(pssInfo.mOtherPss));
        }
        if (mHeapAlloc != null) {
            mHeapAlloc.setText(formatMemory(dalvikHeapMem.mAllocatedMem));
        }
        if (mHeapSize != null) {
            mHeapSize.setText(formatMemory(dalvikHeapMem.mTotalMem));
        }
        if (mHeapFree != null) {
            mHeapFree.setText(formatMemory(dalvikHeapMem.mFreeMem));
        }
        if (mMemTotal != null && ramMemoryInfo != null) {
            mMemTotal.setText(formatMemory(ramMemoryInfo.mTotalRAM));
        }
        if (mMemAvail != null && ramMemoryInfo != null) {
            mMemAvail.setText(formatMemory(ramMemoryInfo.mAvailRAM));
        }
        if (mIsLowMem != null && ramMemoryInfo != null) {
            if (ramMemoryInfo.mIsLowRAM) {
                mIsLowMem.setVisibility(VISIBLE);
            } else {
                mIsLowMem.setVisibility(GONE);
            }
        }
    }

    public void setNetData(long txByte, long rxByte, long rate) {
        //todo 优化单位显示
        String txStr = String.format(Locale.getDefault(), VALUE_FORMAT_TXT,(float)txByte / 1024 / 1024) + "M";
        if (mTX != null) mTX.setText(txStr);
        String rxStr = String.format(Locale.getDefault(), VALUE_FORMAT_TXT,(float)rxByte / 1024 / 1024) + "M";
        if (mRX != null) mRX.setText(rxStr);
        String rateStr = String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)(rate / 1024)) + "k/s";
        if (mRate != null) mRate.setText(rateStr);
    }

    public void setCPUData(String str) {
        //todo
        if (mPercentage != null) {
            mPercentage.setText(str);
        }
    }

    private String formatMemory(long mem) {
        return String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)mem / 1024) + UNIT;
    }
}
