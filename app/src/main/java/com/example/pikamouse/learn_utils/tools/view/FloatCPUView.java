package com.example.pikamouse.learn_utils.tools.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: jiangfeng
 * @date: 2019/1/7
 */
public class FloatCPUView extends ConstraintLayout {

    private ConstraintLayout mPercentageContainer;
    private Map<String,ConstraintLayout> mContainers = new HashMap<>();
    private TextView mPercentage;

    public FloatCPUView(Context context) {
        this(context, null);
    }

    public FloatCPUView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatCPUView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setBackgroundColor(getResources().getColor(R.color.monitor_bg_chart));
        inflate(context, R.layout.monitor_layout_cpu_info, this);
        mPercentageContainer = findViewById(R.id.cpu_monitor_percentage_container);
        mPercentageContainer.setTag(MonitorManager.MONITOR_CPU_TAG_PERCENTAGE);
        mPercentage = findViewById(R.id.cpu_monitor_percentage);
        mPercentageContainer.setVisibility(GONE);
        mContainers.put(MonitorManager.MONITOR_CPU_TAG_PERCENTAGE, mPercentageContainer);
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

    public void setData(String str) {
        if (mPercentage != null) {
            mPercentage.setText(str);
        }
    }
}
