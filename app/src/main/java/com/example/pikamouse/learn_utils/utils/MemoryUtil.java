package com.example.pikamouse.learn_utils.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.example.pikamouse.learn_utils.MyApplication;

/**
 * @author: jiangfeng
 * @date: 2018/12/26
 * 内存相关工具类
 * 所有结果以KB为单位
 */
public class MemoryUtil {

    private final static String TAG = "MemoryUtil";


    /**
     * Dalvik堆内存
     * 只要App用到的内存都算（包括共享内存）
     */
    public static class DalvikHeapInfo {
        public long freeMem;
        public long maxMem;
        public long allocatedMem;
    }

    /**
     * 应用实际物理内存大小
     * 包括了应用进程所占用的内存大小和共享内存中占用的内存大小(比例分配方式计算).
     */
    public static class PSSInfo {
        public int totalPSS;
        public int dalvikPSS;
        public int nativePSS;
        public int otherPSS;
    }

    /**
     * 手机RAM内存信息
     * 物理内存信息
     */
    public static class RAMInfo {
        public long totalMem;                 //手机总RAM
        public long availMem;                 //手机可用RAM
        public long lowMemThreshold;         //内存占用满的阀值，超过即认为低内存运行状态，可能会Kill process
        public boolean isLowMem;             //是否低内存运行状态
    }
}
