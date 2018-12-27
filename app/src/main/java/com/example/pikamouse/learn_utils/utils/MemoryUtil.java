package com.example.pikamouse.learn_utils.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author: jiangfeng
 * @date: 2018/12/26
 * 内存相关工具类
 * 所有结果以KB为单位
 */
public class MemoryUtil {

    private final static String TAG = "MemoryUtil";
    private final static String FILE_MEM = "/proc/meminfo";

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

    public static DalvikHeapInfo getDalvikHeapInfo() {
        Runtime runtime = Runtime.getRuntime();
        DalvikHeapInfo dalvikHeapInfo = new DalvikHeapInfo();
        dalvikHeapInfo.maxMem = runtime.maxMemory() / 1024;
        dalvikHeapInfo.freeMem = runtime.freeMemory() / 1024;
        dalvikHeapInfo.allocatedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024;
        return dalvikHeapInfo;
    }

    public static PSSInfo getAppPSSInfo(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{pid})[0];
        PSSInfo pssInfo = new PSSInfo();
        pssInfo.dalvikPSS = memoryInfo.dalvikPss;
        pssInfo.nativePSS = memoryInfo.nativePss;
        pssInfo.otherPSS = memoryInfo.otherPss;
        pssInfo.totalPSS = memoryInfo.getTotalPss();
        return pssInfo;
    }

    public static RAMInfo getRAMInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        RAMInfo ramInfo = new RAMInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ramInfo.totalMem = memoryInfo.totalMem / 1024;
        } else {
            FileReader fr;
            BufferedReader br;
            try {
                fr = new FileReader(FILE_MEM);
                br = new BufferedReader(fr);
                String text = br.readLine();
                String[] array = text.split("\\s+");
                // 单位为KB
                ramInfo.totalMem = Long.valueOf(array[1]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ramInfo.availMem = memoryInfo.availMem / 1024;
        ramInfo.isLowMem = memoryInfo.lowMemory;
        ramInfo.lowMemThreshold = memoryInfo.threshold / 1024;
        return ramInfo;
    }


}
