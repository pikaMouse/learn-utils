package com.example.pikamouse.learn_utils.tools.view;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.MonitorManager;
import com.example.pikamouse.learn_utils.MyApplication;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.view.model.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiangfeng
 * @date: 2019/1/4
 */
public class MonitorListAdapter extends RecyclerView.Adapter<MonitorListAdapter.ViewHolder> {

    private final static String TAG = "MonitorListAdapter";

    public static final String CONFIG_MEM_DEFAULT_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_mem_title);
    public static final String CONFIG_NET_DEFAULT_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_net_title);
    public static final String CONFIG_CHART_DEFAULT_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_chart_title);
    public static final String CONFIG_MEM_HEAP_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_mem_heap);
    public static final String CONFIG_MEM_PSS_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_mem_pss);
    public static final String CONFIG_MEM_SYSTEM_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_mem_system);
    public static final String CONFIG_CHART_HEAP_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_chart_heap);
    public static final String CONFIG_CHART_PSS_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_chart_pss);
    public static final String CONFIG_NET_RX_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_net_rx);
    public static final String CONFIG_NET_TX_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_net_tx);
    public static final String CONFIG_NET_RATE_TAG = MyApplication.getAppContext().getResources().getString(R.string.monitor_config_net_rate);

    private List<Config> mData = new ArrayList<>();

    @Override
    public MonitorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_monitor_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitorListAdapter.ViewHolder holder, int position) {
        Config config = mData.get(position);
        holder.bind(config);
    }

    public void setData(List<Config> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private TextView mTitle;
        private Switch mSwitch;
        private GridLayout mGrid;
        private int mLength;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.monitor_item_title);
            mSwitch = itemView.findViewById(R.id.monitor_item_switch);
            mGrid = itemView.findViewById(R.id.monitor_item_grid);
        }

        public void bind(Config config) {
            mTitle.setText(config.mTitle);
            mLength = config.mList.size();
            mSwitch.setOnCheckedChangeListener(this);
            mSwitch.setTag(config.mTitle);
            for (int i = 0; i < mLength; i++) {
                View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_monitor_config_item_check, null);
                TextView mInfo = view.findViewById(R.id.monitor_item_check_box_text);
                CheckBox mCheckBox = view.findViewById(R.id.monitor_item_check_box);
                String tag = config.mList.get(i);
                mCheckBox.setTag(tag);
                mCheckBox.setClickable(false);
                mCheckBox.setOnCheckedChangeListener(this);
                mInfo.setText(tag);
                mGrid.addView(view);
            }
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String tag = (String) buttonView.getTag();
            if (buttonView.getId() == R.id.monitor_item_switch) {
                for (int i = 0; i < mLength; i++) {
                    View view = mGrid.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.monitor_item_check_box);
                    checkBox.setClickable(isChecked);
                    if (i == 0) checkBox.setChecked(isChecked);
                    if (!isChecked) checkBox.setChecked(false);
                }
                if (tag.equals(CONFIG_MEM_DEFAULT_TAG)) {
                    addDialogItem(isChecked, tag);
                } else if (tag.equals(CONFIG_CHART_DEFAULT_TAG)) {
                    addDialogItem(isChecked, "Heap" + tag);
                    addDialogItem(isChecked, "PSS" + tag);
                } else if (tag.equals(CONFIG_NET_DEFAULT_TAG)) {
                    addDialogItem(isChecked, tag);
                }
            } else {
                boolean reset = false;
                for (int i = 0; i < mLength; i++) {
                    View view = mGrid.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.monitor_item_check_box);
                    reset = checkBox.isChecked();
                    if (reset) break;
                }
                if (!reset) {
                    mSwitch.setChecked(false);
                    MonitorManager.Configure.isDefMem = false;
                    MonitorManager.Configure.isDefChart = false;
                    MonitorManager.Configure.isDefNet = false;
                    return;
                }
                if (tag.equals(CONFIG_MEM_HEAP_TAG)) {
                    addMemoryItem(isChecked, CONFIG_MEM_HEAP_TAG);
                }
                if (tag.equals(CONFIG_MEM_PSS_TAG)) {
                    addMemoryItem(isChecked, CONFIG_MEM_PSS_TAG);
                }
                if (tag.equals(CONFIG_MEM_SYSTEM_TAG)) {
                    addMemoryItem(isChecked, CONFIG_MEM_SYSTEM_TAG);
                }
            }
        }
    }

    private void addDialogItem(boolean isAdd, String tag) {
        if (isAdd) {
            MonitorManager.Configure.sDialogItemList.add(tag);
        } else {
            MonitorManager.Configure.sDialogItemList.remove(tag);
        }
    }


    private void addMemoryItem(boolean isAdd, String tag) {
        if (isAdd) {
            MonitorManager.Configure.sMemoryItemList.add(tag);
        } else {
            MonitorManager.Configure.sMemoryItemList.remove(tag);
        }
    }


}
