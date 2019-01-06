package com.example.pikamouse.learn_utils.tools.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;

import java.util.List;


/**
 * create by jiangfeng 2018/12/30
 */
public class DebugDialog1 extends DialogFragment implements View.OnClickListener{


    private static DebugDialogCallBack mCallback;
    private LinearLayout mContainer;




    public DebugDialog1() {

    }

    public void setCallback(DebugDialogCallBack callback) {
        mCallback = callback;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_tools1, null);
        mContainer = view.findViewById(R.id.monitor_dialog_container);
        List<String> list = MonitorManager.ItemBuilder.getTitles();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            Button button = new Button(getActivity().getApplicationContext());
            button.setWidth(DisplayUtil.dp2px(100));
            button.setHeight(DisplayUtil.dp2px(48));
            button.setAllCaps(false);
            String text = list.get(i);
            button.setText(text);
            button.setTag(text);
            button.setOnClickListener(this);
            mContainer.addView(button);
        }
        Button close = new Button(getActivity().getApplicationContext());
        close.setWidth(DisplayUtil.dp2px(100));
        close.setHeight(DisplayUtil.dp2px(48));
        close.setAllCaps(false);
        String text = getActivity().getResources().getString(R.string.monitor_config_close);
        close.setText(text);
        close.setTag(MonitorManager.MONITOR_INSTRUMENT_TAG);
        close.setOnClickListener(this);
        mContainer.addView(close);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(MonitorManager.MONITOR_CHART_TAG)) {
            MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_CHART_CLASS).start(tag);
            if (mCallback != null) {
                mCallback.onStartFloat(tag);
            }
            dismiss();
        } else if (tag.equals(MonitorManager.MONITOR_MEM_TAG)) {
            MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_INFO_CLASS).start(tag);
            if (mCallback != null) {
                mCallback.onStartFloat(tag);
            }
            dismiss();
        } else if (tag.equals(MonitorManager.MONITOR_NET_TAG)) {
            MonitorManager.getInstance().get(MonitorManager.MONITOR_NET_INFO_CLASS).start(tag);
            if (mCallback != null) {
                mCallback.onStartFloat(tag);
            }
            dismiss();
        } else {
            MonitorManager.getInstance().stopAll();
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface DebugDialogCallBack {
        void onStartFloat(@MonitorManager.MonitorTag String type);
        void onDisMiss();
    }
}
