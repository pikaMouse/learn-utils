package com.example.pikamouse.learn_utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.pikamouse.learn_utils.test.view.FloatContainerView;
import com.example.pikamouse.learn_utils.test.MemoryMonitor;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private Button mStartPSS;
    private Button mStartHeap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartPSS = (Button)findViewById(R.id.btn_start_pss_float);
        mStartHeap = (Button)findViewById(R.id.btn_start_heap_float);
        mStartPSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemoryMonitor.getInstance().start(FloatContainerView.MEMORY_TYPE_PSS);
            }
        });
        mStartHeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemoryMonitor.getInstance().start(FloatContainerView.MEMORY_TYPE_HEAP);
            }
        });
        requestAlertWindowPermission();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MemoryMonitor.getInstance().stop();
    }

    private static final int REQUEST_CODE = 1;

    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Log.i(TAG, "onActivityResult granted");
                }
            }
        }
    }

}
