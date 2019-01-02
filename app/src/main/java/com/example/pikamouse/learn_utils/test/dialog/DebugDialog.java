package com.example.pikamouse.learn_utils.test.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.AllInfoMonitor;
import com.example.pikamouse.learn_utils.test.MemoryMonitor;
import com.example.pikamouse.learn_utils.test.view.FloatAllInfoView;
import com.example.pikamouse.learn_utils.test.view.FloatMemoryView;
import com.example.pikamouse.learn_utils.test.window.FloatAllInfoWindow;


/**
 * create by jiangfeng 2018/12/30
 */
public class DebugDialog extends DialogFragment implements View.OnClickListener{

    private Button mStartPSS;
    private Button mStartHeap;
    private Button mStartAll;
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
        mStartPSS.setOnClickListener(this);
        mStartHeap.setOnClickListener(this);
        mStartAll.setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_heap_float:
                MemoryMonitor.getInstance().start(FloatMemoryView.MEMORY_TYPE_HEAP);
                if (mCallback != null) {
                    mCallback.onStartFloat();
                }
                dismiss();
                break;
            case R.id.btn_start_pss_float:
                MemoryMonitor.getInstance().start(FloatMemoryView.MEMORY_TYPE_PSS);
                if (mCallback != null) {
                    mCallback.onStartFloat();
                }
                dismiss();
                break;
            case R.id.btn_start_total_float:
                AllInfoMonitor.getInstance().start();
                if (mCallback != null) {
                    mCallback.onStartFloat();
                }
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
        void onStartFloat();
        void onDisMiss();
    }
}
