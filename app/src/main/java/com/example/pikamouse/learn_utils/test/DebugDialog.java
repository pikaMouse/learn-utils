package com.example.pikamouse.learn_utils.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pikamouse.learn_utils.MainActivity;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.view.FloatMemoryView;

/**
 * create by jiangfeng 2018/12/30
 */
public class DebugDialog extends DialogFragment implements View.OnClickListener{

    private Button mStartPSS;
    private Button mStartHeap;
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
        mStartPSS.setOnClickListener(this);
        mStartHeap.setOnClickListener(this);
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
