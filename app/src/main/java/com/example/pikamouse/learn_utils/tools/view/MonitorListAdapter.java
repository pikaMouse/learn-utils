package com.example.pikamouse.learn_utils.tools.view;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.view.model.MonitorListItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiangfeng
 * @date: 2019/1/4
 */
public class MonitorListAdapter extends RecyclerView.Adapter<MonitorListAdapter.ViewHolder> {

    private final static String TAG = "MonitorListAdapter";

    private List<MonitorListItemBean> mData = new ArrayList<>();

    @Override
    public MonitorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitor_layout_config_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitorListAdapter.ViewHolder holder, int position) {
        MonitorListItemBean config = mData.get(position);
        holder.bind(config);
    }

    public void setData(List<MonitorListItemBean> data) {
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

        public void bind(MonitorListItemBean config) {
            mTitle.setText(config.mTitle);
            mLength = config.mList.size();
            mSwitch.setTag(config.mTitle);//tag为title
            boolean cache = getCacheChecked(config.mTitle);
            mSwitch.setChecked(cache);
            for (int i = 0; i < mLength; i++) {
                View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.monitor_layout_config_item_check, null);
                TextView mInfo = view.findViewById(R.id.monitor_item_check_box_text);
                CheckBox mCheckBox = view.findViewById(R.id.monitor_item_check_box);
                String tag = config.mList.get(i);
                mCheckBox.setTag(tag);//tag为item
                mCheckBox.setClickable(cache);
                mCheckBox.setChecked(getCacheChecked(tag));
                mCheckBox.setOnCheckedChangeListener(this);
                mInfo.setText(tag);
                mGrid.addView(view);
            }
            mSwitch.setOnCheckedChangeListener(this);
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String tag = (String) buttonView.getTag();
            setCacheChecked(isChecked, tag);
            //switch按钮
            if (buttonView.getId() == R.id.monitor_item_switch) {
                for (int i = 0; i < mLength; i++) {
                    View view = mGrid.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.monitor_item_check_box);
                    TextView textView = view.findViewById(R.id.monitor_item_check_box_text);
                    checkBox.setClickable(isChecked);
                    textView.setTextColor(isChecked ? Color.BLACK : buttonView.getContext().getResources().getColor(R.color.monitor_check_box_uncheckable));
                    if (!isChecked) checkBox.setChecked(false);
                }
                setTitle(isChecked, tag);
            } else {//CheckBox按钮
                boolean reset = false;
                for (int i = 0; i < mLength; i++) {
                    View view = mGrid.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.monitor_item_check_box);
                    reset = checkBox.isChecked();
                    if (reset) break;
                }
                if (!reset) {
                    mSwitch.setChecked(false);
                    return;
                }
                addItem(isChecked, tag);
            }
        }
    }

    private void setTitle(boolean isChecked, String tag) {
        MonitorManager.ItemBuilder.setTitle(isChecked, tag);
    }

    private void addItem(boolean isChecked, String tag) {
        MonitorManager.ItemBuilder.addItem(isChecked, tag);
    }

    private void setCacheChecked(boolean isChecked, String tag) {
        MonitorManager.ItemBuilder.setCheckState(isChecked, tag);
    }

    private boolean getCacheChecked(String tag) {
        return MonitorManager.ItemBuilder.getCheckState(tag);
    }
}
