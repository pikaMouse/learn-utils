package com.example.pikamouse.learn_utils.tools.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.MonitorManager;


/**
 * create by jiangfeng 2018/12/30
 */
public class DebugDialog extends DialogFragment implements View.OnClickListener{

    private Button mStartPSS;
    private Button mStartHeap;
    private Button mStartAll;
    private Button mStartNet;
    private Button mClose;
    private static DebugDialogCallBack mCallback;




    public DebugDialog() {

    }

    public void setCallback(DebugDialogCallBack callback) {
        mCallback = callback;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_tools, null);
        mStartHeap = view.findViewById(R.id.btn_start_heap_float);
        mStartPSS = view.findViewById(R.id.btn_start_pss_float);
        mStartAll = view.findViewById(R.id.btn_start_total_float);
        mStartNet = view.findViewById(R.id.btn_start_net);
        mClose = view.findViewById(R.id.btn_close);
        mStartPSS.setOnClickListener(this);
        mStartHeap.setOnClickListener(this);
        mStartAll.setOnClickListener(this);
        mStartNet.setOnClickListener(this);
        mClose.setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String type;
        switch (v.getId()) {
            case R.id.btn_start_heap_float:
                type = MonitorManager.MONITOR_MEMORY_HEAP_TYPE;
                MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_CHART_CLASS).start(type);
                if (mCallback != null) {
                    mCallback.onStartFloat(type);
                }
                dismiss();
                break;
            case R.id.btn_start_pss_float:
                type = MonitorManager.MONITOR_MEMORY_PSS_TYPE;
                MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_CHART_CLASS).start(type);
                if (mCallback != null) {
                    mCallback.onStartFloat(type);
                }
                dismiss();
                break;
            case R.id.btn_start_total_float:
                type = MonitorManager.MONITOR_MEMORY_INFO_TYPE;
                MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_INFO_CLASS).start(type);
                if (mCallback != null) {
                    mCallback.onStartFloat(type);
                }
                dismiss();
                break;
            case R.id.btn_start_net:
                type = MonitorManager.MONITOR_NET_INFO_TYPE;
                MonitorManager.getInstance().get(MonitorManager.MONITOR_NET_INFO_CLASS).start(type);
                if (mCallback != null) {
                    mCallback.onStartFloat(type);
                }
                dismiss();
                break;
            case R.id.btn_close:
                MonitorManager.getInstance().stopAll();
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface DebugDialogCallBack {
        void onStartFloat(@MonitorManager.MonitorType String type);
        void onDisMiss();
    }
}
