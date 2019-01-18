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

import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.view.MonitorListAdapter;
import com.example.pikamouse.learn_utils.tools.view.model.MonitorListItemBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MonitorListAdapter mAdapter;
    private List<MonitorListItemBean> mData;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        MonitorListItemBean item1 = new MonitorListItemBean.Builder()
                .title(MonitorManager.MONITOR_MEM_TAG)
                .addItem(MonitorManager.MONITOR_MEM_TAG_HEAP)
                .addItem(MonitorManager.MONITOR_MEM_TAG_PSS)
                .addItem(MonitorManager.MONITOR_MEM_TAG_SYSTEM)
                .addItem(MonitorManager.MONITOR_MEM_TAG_HEAP_FREE)
                .addItem(MonitorManager.MONITOR_MEM_TAG_PSS_DALVIK)
                .addItem(MonitorManager.MONITOR_MEM_TAG_SYSTEM_AVAIL)
                .addItem(MonitorManager.MONITOR_MEM_TAG_HEAP_ALLOC)
                .addItem(MonitorManager.MONITOR_MEM_TAG_PSS_NATIVE)
                .addItem(MonitorManager.MONITOR_MEM_TAG_PSS_OTHER)
                .build();
        MonitorListItemBean item2 = new MonitorListItemBean.Builder()
                .title(MonitorManager.MONITOR_NET_TAG)
                .addItem(MonitorManager.MONITOR_NET_TAG_RX)
                .addItem(MonitorManager.MONITOR_NET_TAG_TX)
                .addItem(MonitorManager.MONITOR_NET_TAG_RATE)
                .build();
        MonitorListItemBean item3 = new MonitorListItemBean.Builder()
                .title(MonitorManager.MONITOR_CPU_TAG)
                .addItem(MonitorManager.MONITOR_CPU_TAG_PERCENTAGE)
                .build();
        MonitorListItemBean item4 = new MonitorListItemBean.Builder()
                .title(MonitorManager.MONITOR_CHART_TAG)
                .addItem(MonitorManager.MONITOR_CHART_TAG_HEAP)
                .addItem(MonitorManager.MONITOR_CHART_TAG_PSS)
                .build();
        mData.add(item1);
        mData.add(item2);
        mData.add(item3);
        mData.add(item4);
        mAdapter.setData(mData);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MonitorManager.getInstance().stopAll();
    }
    private static final int REQUEST_CODE = 1;

    private void requestAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
                Toast.makeText(MainActivity.this, "请打开悬浮窗权限", Toast.LENGTH_SHORT).show();
            } else {
                MonitorManager.getInstance().start();
                finish();
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
                } else {
                    Toast.makeText(MainActivity.this, "获取悬浮窗权限失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_config_confirm:
                if (MonitorManager.ItemBuilder.getAllItems().size() <= 0) {
                    Toast.makeText(MainActivity.this, "至少选中一个配置", Toast.LENGTH_SHORT).show();
                    MonitorManager.getInstance().stopAll();
                } else {
                    requestAlertWindowPermission();
                }
                break;
            default:
                break;
        }
    }
}
