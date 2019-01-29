package com.example.pikamouse.learn_utils.tools.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.util.adb.AdbConnector;
import com.example.pikamouse.learn_utils.tools.util.adb.AdbConstant;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * @author: jiangfeng
 * @date: 2019/1/7
 */
public class CpuUtil {

    private final static String TAG = "CpuUtil";
    private RandomAccessFile mProcStatFile;
    private RandomAccessFile mAppStatFile;
    private Long mLastCpuTime;
    private Long mLastAppCpuTime;
    private boolean mAboveAndroidO;
    private Context mContext;
    private String mPackageName;
    private boolean mIsShowCpuTip;

    public interface CallBack {
        void onSuccess(float value);
        void onFail(String msg);
    }

    private CpuUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true;
        }
    }

    private static class SingleHolder {
        private final static CpuUtil CPU_UTIL = new CpuUtil();
    }

    public static CpuUtil getInstance() {
        return SingleHolder.CPU_UTIL;
    }


    public void getCPUData(Context context, CallBack callBack) {
        mContext = context;
        mPackageName = mContext.getPackageName();
        if (mAboveAndroidO) {
            getCPUDataAboveAndroidO(callBack);
        } else {
            callBack.onSuccess(getCPUDataBellowAndroidO());
        }
    }

    private void getCPUDataAboveAndroidO(final CallBack callBack) {
        try {
            String msg = AdbConnector.getInstance().openShell("shell:dumpsys cpuinfo | grep '" + mPackageName + "'");
            if (msg != null) {
                Log.d(TAG, msg);
                callBack.onSuccess(parseCPUData(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!mIsShowCpuTip) {
                callBack.onFail(e.getMessage());
                if (e.toString().contains(AdbConstant.HOST)) {
                    callBack.onFail(ResourceUtils.getResources().getString(R.string.cpu_monitor_tip));
                    mIsShowCpuTip = true;
                }
            }
        }
    }

    private float getCPUDataBellowAndroidO() {
        long cpuTime;
        long appTime;
        float value = 0.0f;
        try {
            if (mProcStatFile == null || mAppStatFile == null) {
                mProcStatFile = new RandomAccessFile("/proc/stat", "r");
                mAppStatFile = new RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r");
            } else {
                mProcStatFile.seek(0L);
                mAppStatFile.seek(0L);
            }
            String procStatString = mProcStatFile.readLine();
            String appStatString = mAppStatFile.readLine();
            Log.d(TAG, "procStatString:  " + procStatString);
            Log.d(TAG, "appStatString:  " + appStatString);
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (mLastCpuTime == null || mLastAppCpuTime == null) {
                mLastCpuTime = cpuTime;
                mLastAppCpuTime = appTime;
                return value;
            }
            value = ((float) (appTime - mLastAppCpuTime) / (float) (cpuTime - mLastCpuTime)) * 100f;
            mLastCpuTime = cpuTime;
            mLastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public float parseCPUData(String data) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.contains("Permission Denial")) {
                    break;
                } else {
                    String[] lineItems = line.split("\\s+");
                    if (lineItems.length > 1) {
                        bufferedReader.close();
                        return Float.parseFloat(lineItems[0].replace("%", ""));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void release() {
        mIsShowCpuTip = false;
        AdbConnector.getInstance().release();
    }
}
