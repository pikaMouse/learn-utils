package com.example.pikamouse.learn_utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.test.FloatCurveView;
import com.example.pikamouse.learn_utils.test.MemoryMonitor;
import com.example.pikamouse.learn_utils.utils.MemoryUtil;
import com.example.pikamouse.learn_utils.utils.ProcessUtil;
import com.example.pikamouse.learn_utils.view.MemoryView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MemoryMonitor.getInstance().start(FloatCurveView.MEMORY_TYPE_HEAP);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MemoryMonitor.getInstance().stop();

    }
}
