package com.example.pikamouse.learn_utils.test.view;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.example.pikamouse.learn_utils.MainActivity;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.test.AllInfoMonitor;
import com.example.pikamouse.learn_utils.test.DebugMonitor;
import com.example.pikamouse.learn_utils.test.MonitorManager;
import com.example.pikamouse.learn_utils.test.NetInfoMonitor;
import com.example.pikamouse.learn_utils.test.dialog.DebugDialog;
import com.example.pikamouse.learn_utils.test.MemoryMonitor;
import com.example.pikamouse.learn_utils.test.util.DisplayUtil;

/**
 * create by jiangfeng 2018/12/30
 */
public class FloatBallView extends AppCompatTextView implements View.OnClickListener, DebugDialog.DebugDialogCallBack{

    private Context mContext;
    private String mType;

    private boolean isShowClose;

    private final static String DEBUG_TOOLS_DIALOG = "debug_tools_dialog";

    public FloatBallView(Context context) {
        this(context, null);
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        setId(R.id.float_tools);
        setWidth(DisplayUtil.dp2px(50));
        setHeight(DisplayUtil.dp2px(50));
        setGravity(Gravity.CENTER);
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_float_tools_open));
        setTextSize(DisplayUtil.sp2px(16));
        setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        label:
        switch (v.getId()) {
            case R.id.float_tools:
                if (!isShowClose) {
                    AppCompatActivity appCompatActivity = MainActivity.mActivityRef.get();
                    if (appCompatActivity != null) {
                        DebugDialog debugDialog = new DebugDialog();
                        if (appCompatActivity.getFragmentManager().findFragmentByTag(DEBUG_TOOLS_DIALOG) == null) {
                            FragmentTransaction transaction = appCompatActivity.getFragmentManager().beginTransaction();
                            transaction.add(debugDialog, DEBUG_TOOLS_DIALOG);
                            //fixed bug: Can not perform this action after onSaveInstanceState
                            transaction.commitAllowingStateLoss();
                            transaction.show(debugDialog);
                            debugDialog.setCallback(this);
                        }
                    }
                } else {
                    setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_float_tools_open));
                    isShowClose = false;
                    switch (mType) {
                        case MonitorManager.MONITOR_DEBUG_BALL_TYPE:
                            MonitorManager.getInstance().stop();
                            break;
                        case MonitorManager.MONITOR_MEMORY_ALL_TYPE:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_ALL_CLASS).stop();
                            break ;
                        case MonitorManager.MONITOR_MEMORY_HEAP_TYPE:
                        case MonitorManager.MONITOR_MEMORY_PSS_TYPE:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_CLASS).stop();
                            break;
                        case MonitorManager.MONITOR_NET_ALL_TYPE:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_NET_INFO_CLASS).stop();
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartFloat(String type) {
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_float_tools_close));
        isShowClose = true;
        mType = type == null ? "" : type;
    }

    @Override
    public void onDisMiss() {

    }
}
