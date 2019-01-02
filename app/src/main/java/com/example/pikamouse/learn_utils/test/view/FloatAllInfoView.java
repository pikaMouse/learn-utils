package com.example.pikamouse.learn_utils.test.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.util.MemoryUtil;

/**
 * @author: jiangfeng
 * @date: 2019/1/2
 */
public class FloatAllInfoView extends ConstraintLayout {

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



    public FloatAllInfoView(Context context) {
        this(context, null);
    }

    public FloatAllInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatAllInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.mem_monitor_float_total_info, this);
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

    public void setData(MemoryUtil.AllInfo allInfo) {
        final MemoryUtil.PssInfo pssInfo = allInfo.mPssInfo;
        final MemoryUtil.DalvikHeapMem dalvikHeapMem = allInfo.mDalvikHeapMem;
        final MemoryUtil.RamMemoryInfo ramMemoryInfo = allInfo.mRamMemoryInfo;
        if (mTotalPSS != null && pssInfo != null) {
            mTotalPSS.setText(String.valueOf(pssInfo.mTotalPss));
        }
        if (mDalvikPSS != null && pssInfo != null) {
            mDalvikPSS.setText(String.valueOf(pssInfo.mDalvikPss));
        }
        if (mNativePSS != null && pssInfo != null) {
            mNativePSS.setText(String.valueOf(pssInfo.mNativePss));
        }
        if (mOtherPSS != null && pssInfo != null) {
            mOtherPSS.setText(String.valueOf(pssInfo.mOtherPss));
        }
        if (mHeapAlloc != null) {
            mHeapAlloc.setText(String.valueOf(dalvikHeapMem.mAllocatedMem));
        }
        if (mHeapSize != null) {
            mHeapSize.setText(String.valueOf(dalvikHeapMem.mTotalMem));
        }
        if (mHeapFree != null) {
            mHeapFree.setText(String.valueOf(dalvikHeapMem.mFreeMem));
        }
        if (mMemTotal != null && ramMemoryInfo != null) {
            mMemTotal.setText(String.valueOf(ramMemoryInfo.mTotalMem));
        }
        if (mMemAvail != null && ramMemoryInfo != null) {
            mMemAvail.setText(String.valueOf(ramMemoryInfo.mAvailMem));
        }
        if (mIsLowMem != null && ramMemoryInfo != null) {
            if (ramMemoryInfo.mIsLowMemory) {
                mIsLowMem.setVisibility(VISIBLE);
            } else {
                mIsLowMem.setVisibility(GONE);
            }
        }
    }
}
