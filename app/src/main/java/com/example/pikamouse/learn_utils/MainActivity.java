package com.example.pikamouse.learn_utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.pikamouse.learn_utils.utils.MemoryUtil;
import com.example.pikamouse.learn_utils.utils.ProcessUtil;
import com.example.pikamouse.learn_utils.view.MemoryView;

public class MainActivity extends AppCompatActivity {

    private MemoryView mMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMemory = findViewById(R.id.sv_memory_view);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    String s = new String();
                }
            }
        });
    }


}
