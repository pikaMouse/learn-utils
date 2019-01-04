package com.example.pikamouse.learn_utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pikamouse.learn_utils.tools.view.MonitorListAdapter;
import com.example.pikamouse.learn_utils.tools.view.model.Config;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "MainActivity";
    public static SoftReference<AppCompatActivity> mActivityRef;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MonitorListAdapter mAdapter;
    private List<Config> mData;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityRef = new SoftReference<AppCompatActivity>(this);
        requestAlertWindowPermission();
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.monitor_config_list);
        mButton = findViewById(R.id.btn_config_confirm);
        mButton.setOnClickListener(this);
        mAdapter = new MonitorListAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        initData();
    }

    private void initData() {
        mData = new ArrayList<>();
        Config item1 = new Config.Builder()
                .title(getResources().getString(R.string.monitor_config_mem_title))
                .tag(getResources().getString(R.string.monitor_config_mem_default))
                .add(getResources().getString(R.string.monitor_config_mem_default))
                .add(getResources().getString(R.string.monitor_config_mem_heap))
                .add(getResources().getString(R.string.monitor_config_mem_pss))
                .add(getResources().getString(R.string.monitor_config_mem_system))
                .build();
        Config item2 = new Config.Builder()
                .title(getResources().getString(R.string.monitor_config_net_title))
                .tag(getResources().getString(R.string.monitor_config_net_default))
                .add(getResources().getString(R.string.monitor_config_net_default))
                .add(getResources().getString(R.string.monitor_config_net_rx))
                .add(getResources().getString(R.string.monitor_config_net_tx))
                .add(getResources().getString(R.string.monitor_config_net_rate))
                .build();
        Config item3 = new Config.Builder()
                .title(getResources().getString(R.string.monitor_config_chart_title))
                .tag(getResources().getString(R.string.monitor_config_chart_default))
                .add(getResources().getString(R.string.monitor_config_chart_default))
                .build();

        mData.add(item1);
        mData.add(item2);
        mData.add(item3);
        mAdapter.setData(mData);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityRef.clear();
        MonitorManager.getInstance().stopAll();
    }

    private static final int REQUEST_CODE = 1;

    private void requestAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                MonitorManager.getInstance().start();
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
                    MonitorManager.getInstance().start();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_config_confirm:
                if (!MonitorManager.Configure.isDefNet && !MonitorManager.Configure.isDefMem && !MonitorManager.Configure.isDefChart) {
                    Toast.makeText(MainActivity.this, "至少选中一个配置", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
