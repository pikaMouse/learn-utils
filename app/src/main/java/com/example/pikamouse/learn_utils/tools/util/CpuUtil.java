package com.example.pikamouse.learn_utils.tools.util;

import android.os.Build;
import android.util.Log;

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


    public float getCPUData() {
        if (mAboveAndroidO) {
            //todo
            return 0;
        } else {
            return getCPUDataBellowAndroidO();
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
            if (mLastCpuTime == null && mLastAppCpuTime == null) {
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

}
