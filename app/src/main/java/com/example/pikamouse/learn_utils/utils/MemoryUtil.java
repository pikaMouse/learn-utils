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
 */
public class MemoryUtil {

    private final static String TAG = "MemoryUtil";

    public static class DalvikHeapInfo {
        public long freeMem;
        public long maxMem;
        public long allocatedMem;
    }

    /**
     * 应用实际物理内存大小
     * 包括了你的应用进程所占用的内存大小和共享内存中占用的内存大小(比例分配方式计算).
     */
    public static class PSSInfo {
        public int totalPSS;
        public int dalvikPSS;
        public int nativePSS;
        public int otherPSS;
    }
}
