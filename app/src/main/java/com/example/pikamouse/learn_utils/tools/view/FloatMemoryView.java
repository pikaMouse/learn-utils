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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class FloatMemoryView extends ConstraintLayout {

    private final static String TAG = "FloatAllInfoView";

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

    private ConstraintLayout mPSSContainer;
    private ConstraintLayout mHeapContainer;
    private ConstraintLayout mSystemContainer;
    private Map<String,ConstraintLayout> mContainers = new HashMap<>();

    private static final String VALUE_FORMAT_TXT = "%.1f";



    public FloatMemoryView(Context context) {
        this(context, null);
    }

    public FloatMemoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatMemoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.monitor_layout_memory_info, this);
        setBackgroundColor(getResources().getColor(R.color.monitor_bg_chart));
        setPadding(DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(10));
        mPSSContainer = findViewById(R.id.mem_monitor_container1);
        mHeapContainer = findViewById(R.id.mem_monitor_container2);
        mSystemContainer = findViewById(R.id.mem_monitor_container3);
        mPSSContainer.setVisibility(View.GONE);
        mHeapContainer.setVisibility(View.GONE);
        mSystemContainer.setVisibility(View.GONE);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_PSS, mPSSContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_HEAP, mHeapContainer);
        mContainers.put(MonitorManager.MONITOR_MEM_TAG_SYSTEM,mSystemContainer);
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

    public void setData(MemoryUtil.AllInfo allInfo) {
        final MemoryUtil.PssInfo pssInfo = allInfo.mPssInfo;
        final MemoryUtil.DalvikHeapMem dalvikHeapMem = allInfo.mDalvikHeapMem;
        final MemoryUtil.RamMemoryInfo ramMemoryInfo = allInfo.mRamMemoryInfo;
        if (mTotalPSS != null && pssInfo != null) {
            mTotalPSS.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)pssInfo.mTotalPss / 1024));
        }
        if (mDalvikPSS != null && pssInfo != null) {
            mDalvikPSS.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)pssInfo.mDalvikPss / 1024));
        }
        if (mNativePSS != null && pssInfo != null) {
            mNativePSS.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)pssInfo.mNativePss / 1024));
        }
        if (mOtherPSS != null && pssInfo != null) {
            mOtherPSS.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)pssInfo.mOtherPss / 1024));
        }
        if (mHeapAlloc != null) {
            mHeapAlloc.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)dalvikHeapMem.mAllocatedMem / 1024));
        }
        if (mHeapSize != null) {
            mHeapSize.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)dalvikHeapMem.mTotalMem / 1024));
        }
        if (mHeapFree != null) {
            mHeapFree.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)dalvikHeapMem.mFreeMem / 1024));
        }
        if (mMemTotal != null && ramMemoryInfo != null) {
            mMemTotal.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)ramMemoryInfo.mTotalRAM / 1024));
        }
        if (mMemAvail != null && ramMemoryInfo != null) {
            mMemAvail.setText(String.format(Locale.getDefault(), VALUE_FORMAT_TXT, (float)ramMemoryInfo.mAvailRAM / 1024));
        }
        if (mIsLowMem != null && ramMemoryInfo != null) {
            if (ramMemoryInfo.mIsLowRAM) {
                mIsLowMem.setVisibility(VISIBLE);
            } else {
                mIsLowMem.setVisibility(GONE);
            }
        }
    }
}
