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
public class InstrumentDialog extends DialogFragment implements View.OnClickListener{


    private static InstrumentDialogCallBack mCallback;
    private LinearLayout mContainer;

    public InstrumentDialog() {

    }

    public void setCallback(InstrumentDialogCallBack callback) {
        mCallback = callback;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.monitor_dialog_instrument, null);
        mContainer = view.findViewById(R.id.monitor_dialog_container);
        Button totalBtn = new Button(getActivity().getApplicationContext());
        totalBtn.setWidth(DisplayUtil.dp2px(100));
        totalBtn.setHeight(DisplayUtil.dp2px(48));
        totalBtn.setAllCaps(false);
        String total = getActivity().getResources().getString(R.string.monitor_instrument_total);
        totalBtn.setText(total);
        totalBtn.setTag(MonitorManager.MONITOR_TOTAL_TAG);
        totalBtn.setOnClickListener(this);
        mContainer.addView(totalBtn);
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
        Button closeBtn = new Button(getActivity().getApplicationContext());
        closeBtn.setWidth(DisplayUtil.dp2px(100));
        closeBtn.setHeight(DisplayUtil.dp2px(48));
        closeBtn.setAllCaps(false);
        String close = getActivity().getResources().getString(R.string.monitor_instrument_close);
        closeBtn.setText(close);
        closeBtn.setTag(MonitorManager.MONITOR_INSTRUMENT_TAG);
        closeBtn.setOnClickListener(this);
        mContainer.addView(closeBtn);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        switch (tag) {
            case MonitorManager.MONITOR_CHART_TAG:
                MonitorManager.getInstance().get(MonitorManager.MONITOR_CHART_CLASS).start(tag);
                if (mCallback != null) {
                    mCallback.onStartFloat(tag);
                }
                dismiss();
                break;
            case MonitorManager.MONITOR_MEM_TAG:
            case MonitorManager.MONITOR_NET_TAG:
            case MonitorManager.MONITOR_CPU_TAG:
            case MonitorManager.MONITOR_TOTAL_TAG:
                MonitorManager.getInstance().get(MonitorManager.MONITOR_ALL_INFO_CLASS).start(tag);
                if (mCallback != null) {
                    mCallback.onStartFloat(tag);
                }
                dismiss();
                break;
            default:
                MonitorManager.getInstance().stopAll();
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface InstrumentDialogCallBack {
        void onStartFloat(@MonitorManager.MonitorTag String type);
        void onDisMiss();
    }
}
