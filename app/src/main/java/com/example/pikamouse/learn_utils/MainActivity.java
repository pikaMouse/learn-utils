package com.example.pikamouse.learn_utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.utils.MemoryUtil;
import com.example.pikamouse.learn_utils.utils.ProcessUtil;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv_text);
        mTextView.setText("PSS: " + MemoryUtil.getAppPSSInfo(this, ProcessUtil.getCurrentPid()).totalPSS + "\n" +
                "allocateMem: " + MemoryUtil.getDalvikHeapInfo().allocatedMem);
    }
}
