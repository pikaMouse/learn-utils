package com.example.pikamouse.learn_utils.tools.view;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.example.pikamouse.learn_utils.MyApplication;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.monitor.MonitorManager;
import com.example.pikamouse.learn_utils.tools.dialog.InstrumentDialog;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;

/**
 * create by jiangfeng 2018/12/30
 */
public class FloatInstrumentView extends AppCompatTextView implements View.OnClickListener, InstrumentDialog.InstrumentDialogCallBack {

    private Context mContext;
    private String mTag;

    private boolean isShowClose;

    private final static String INSTRUMENT_MONITOR_DIALOG = "instrument_monitor_dialog";

    public FloatInstrumentView(Context context) {
        this(context, null);
    }

    public FloatInstrumentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatInstrumentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        setId(R.id.monitor_instrument_dialog);
        setWidth(DisplayUtil.dp2px(50));
        setHeight(DisplayUtil.dp2px(50));
        setGravity(Gravity.CENTER);
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.monitor_bg_float_instrument_open));
        setTextSize(DisplayUtil.sp2px(16));
        setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.monitor_instrument_dialog:
                if (!isShowClose) {
                    if (MyApplication.mActivityRef.get() instanceof AppCompatActivity) {
                        AppCompatActivity appCompatActivity = (AppCompatActivity)MyApplication.mActivityRef.get();
                        if (appCompatActivity != null) {
                            InstrumentDialog instrumentDialog = new InstrumentDialog();
                            if (appCompatActivity.getFragmentManager().findFragmentByTag(INSTRUMENT_MONITOR_DIALOG) == null) {
                                FragmentTransaction transaction = appCompatActivity.getFragmentManager().beginTransaction();
                                transaction.add(instrumentDialog, INSTRUMENT_MONITOR_DIALOG);
                                //fixed bug: Can not perform this action after onSaveInstanceState
                                transaction.commitAllowingStateLoss();
                                transaction.show(instrumentDialog);
                                instrumentDialog.setCallback(this);
                            }
                        }
                    }
                } else {
                    setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.monitor_bg_float_instrument_open));
                    isShowClose = false;
                    switch (mTag) {
                        case MonitorManager.MONITOR_INSTRUMENT_TAG:
                            MonitorManager.getInstance().stopAll();
                            break;
                        case MonitorManager.MONITOR_MEM_TAG:
                        case MonitorManager.MONITOR_MEM_TAG_HEAP:
                        case MonitorManager.MONITOR_MEM_TAG_PSS:
                        case MonitorManager.MONITOR_MEM_TAG_SYSTEM:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_MEMORY_INFO_CLASS).stop();
                            break ;
                        case MonitorManager.MONITOR_CHART_TAG_HEAP:
                        case MonitorManager.MONITOR_CHART_TAG_PSS:
                        case MonitorManager.MONITOR_CHART_TAG:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_CHART_CLASS).stop();
                            break;
                        case MonitorManager.MONITOR_NET_TAG:
                        case MonitorManager.MONITOR_NET_TAG_RX:
                        case MonitorManager.MONITOR_NET_TAG_TX:
                        case MonitorManager.MONITOR_NET_TAG_RATE:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_NET_INFO_CLASS).stop();
                            break;
                        case MonitorManager.MONITOR_CPU_TAG:
                        case MonitorManager.MONITOR_CPU_TAG_PERCENTAGE:
                            MonitorManager.getInstance().get(MonitorManager.MONITOR_CPU_INFO_CLASS).stop();
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
    public void onStartFloat(String tag) {
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.monitor_bg_float_instrument_close));
        isShowClose = true;
        mTag = tag == null ? "" : tag;
    }

    @Override
    public void onDisMiss() {

    }
}
