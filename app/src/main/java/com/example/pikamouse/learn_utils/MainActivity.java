package com.example.pikamouse.learn_utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.pikamouse.learn_utils.test.MonitorManager;

import java.lang.ref.SoftReference;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    public static SoftReference<AppCompatActivity> mActivityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityRef = new SoftReference<AppCompatActivity>(this);
        requestAlertWindowPermission();
        MonitorManager.getInstance().start(null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityRef.clear();
        MonitorManager.getInstance().stop();
    }

    private static final int REQUEST_CODE = 1;

    private void requestAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
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
