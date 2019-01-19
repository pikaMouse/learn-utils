package com.example.pikamouse.learn_utils.tools.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;

import java.util.ArrayList;
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
    private final static String UNIT_PERCENTAGE = "%";
    private final static String UNIT_B = "B";
    private final static String UNIT_K = "K";
    private final static String UNIT_M = "M";
    private final static String UNIT_G = "G";
    private final static String DOLL = ".";

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

    private TextView mFrame;


    private LinearLayout mTotalPSSContainer;
    private LinearLayout mDalvikPSSContainer;
    private LinearLayout mNativePSSContainer;
    private LinearLayout mOtherPSSContainer;
    private LinearLayout mTotalHeapContainer;
    private LinearLayout mFreeHeapContainer;
    private LinearLayout mAllocHeapContainer;
    private LinearLayout mTotalSystemContainer;
    private LinearLayout mAvailSystemContainer;
    private LinearLayout mRxContainer;
    private LinearLayout mTxContainer;
    private LinearLayout mRateContainer;
    private LinearLayout mPercentageContainer;
    private LinearLayout mFrameContainer;
    private Map<String,LinearLayout> mContainers = new HashMap<>();

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
        mTotalPSSContainer = findViewById(R.id.mem_monitor_total_pss_container);
        mDalvikPSSContainer = findViewById(R.id.mem_monitor_dalvik_pss_container);
        mNativePSSContainer = findViewById(R.id.mem_monitor_native_pss_container);
        mOtherPSSContainer = findViewById(R.id.mem_monitor_other_pss_container);
        mTotalHeapContainer = findViewById(R.id.mem_monitor_total_heap_container);
        mFreeHeapContainer = findViewById(R.id.mem_monitor_free_heap_container);
        mAllocHeapContainer = findViewById(R.id.mem_monitor_allocated_heap_container);
        mTotalSystemContainer = findViewById(R.id.mem_monitor_total_mem_container);
        mAvailSystemContainer = findViewById(R.id.mem_monitor_avail_mem_container);
        mRxContainer = findViewById(R.id.net_monitor_rx_container);
        mTxContainer = findViewById(R.id.net_monitor_tx_container);
        mRateContainer = findViewById(R.id.net_monitor_rate_container);
        mPercentageContainer = findViewById(R.id.cpu_monitor_percentage_container);
        mFrameContainer = findViewById(R.id.frame_monitor_container);

        mTotalPSSContainer.setVisibility(View.GONE);
        mDalvikPSSContainer.setVisibility(View.GONE);
        mNativePSSContainer.setVisibility(View.GONE);
        mOtherPSSContainer.setVisibility(View.GONE);
        mTotalHeapContainer.setVisibility(View.GONE);
        mFreeHeapContainer.setVisibility(View.GONE);
        mAllocHeapContainer.setVisibility(View.GONE);
        mTotalSystemContainer.setVisibility(View.GONE);
        mAvailSystemContainer.setVisibility(View.GONE);
        mRxContainer.setVisibility(View.GONE);
        mTxContainer.setVisibility(View.GONE);
        mRateContainer.setVisibility(View.GONE);
        mPercentageContainer.setVisibility(GONE);
        mFrameContainer.setVisibility(GONE);

        mContainers.put(MonitorManager.MONITOR_MEM_TAG_PSS, mTotalPSSContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_PSS_DALVIK, mDalvikPSSContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_PSS_NATIVE, mNativePSSContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_PSS_OTHER, mOtherPSSContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_HEAP, mTotalHeapContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_HEAP_FREE, mFreeHeapContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_HEAP_ALLOC, mAllocHeapContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_SYSTEM, mTotalSystemContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_SYSTEM_AVAIL, mAvailSystemContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_TX, mTxContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_RX, mRxContainer);
        mContainers.put(MonitorManager.MONITOR_NET_TAG_RATE, mRateContainer);
        mContainers.put(MonitorManager.MONITOR_CPU_TAG_PERCENTAGE, mPercentageContainer);
        mContainers.put(MonitorManager.MONITOR_FRAME_TAG_FPS, mFrameContainer);

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

        mPercentage = findViewById(R.id.cpu_monitor_percentage_info);

        mFrame = findViewById(R.id.frame_monitor_info);
    }

    public void setViewVisibility(String type) {
        List<String> items = new ArrayList<>();
        if (type.equals(MonitorManager.MONITOR_TOTAL_TAG)) {
            items = MonitorManager.ItemBuilder.getAllItems();
        } else {
            items = MonitorManager.ItemBuilder.getItems(type);
        }
        for (String s : items) {
            LinearLayout c = mContainers.get(s);
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

    public void setFrame(String frame) {
        if (mFrame != null) mFrame.setText(frame);
    }

    public void setNetData(long txByte, long rxByte, long rate) {
        if (mTX != null) mTX.setText(formatNet(txByte));
        if (mRX != null) mRX.setText(formatNet(rxByte));
        if (mRate != null) mRate.setText(formatNet(rate));
    }

    public void setCPUData(float value) {
        if (mPercentage != null) {
            mPercentage.setText(formatCPU(value));
        }
    }

    private String formatMemory(long mem) {
        return String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)mem / 1024) + UNIT_M;
    }

    private String formatCPU(float percentage) {
        return String.format(Locale.getDefault(), VALUE_FORMAT_TXT, percentage) + UNIT_PERCENTAGE;
    }

    private String formatNet(long rate) {
        if (rate < 1024) {
            return String.valueOf(rate) + UNIT_B;
        } else {
            rate = rate / 1024;
        }
        if (rate < 1024) {
            return String.valueOf(rate) + UNIT_K;
        } else {
            rate = rate / 1024;
        }
        if (rate < 1024) {
            rate = rate * 100;
            return String.valueOf(rate / 100) + DOLL + String.valueOf(rate % 100) + UNIT_M;
        } else {
            rate = rate * 100 / 1024;
            return String.valueOf(rate / 100) + DOLL + String.valueOf(rate % 100) + UNIT_G;
        }
    }
}
