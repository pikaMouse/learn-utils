package com.example.pikamouse.learn_utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static final String TAG = "SplashActivity";
    private static String[] sPermissions = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private Button mStartMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermission();
        mStartMainActivity = findViewById(R.id.btn_start_main);
        mStartMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkPermission() {
        boolean grant = true;
        for (String permission : sPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                grant = false;
            }
        }
        if (!grant) ActivityCompat.requestPermissions(this, sPermissions, REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {

        } else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
