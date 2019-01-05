package com.example.pikamouse.learn_utils.tools.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存相关工具类
 * 所有结果以KB为单位
 */
public class MemoryUtil {

    /**
     * Dalvik堆内存，只要App用到的内存都算（包括共享内存）
     */
    public static class DalvikHeapMem {
        public long mFreeMem;     //java虚拟机（这个进程）从操作系统那里挖到但还没用上的内存
        public long mMaxMem;     //java虚拟机（这个进程）能够从操作系统那里挖到的最大的内存
        public long mTotalMem;  //java虚拟机（这个进程）现在已经从操作系统那里挖过来的内存大小
        public long mAllocatedMem;  //java虚拟机（这个进程)实际占用的内存大小
    }

    /**
     * 应用实际占用内存（共享按比例分配）
     */
    public static class PssInfo {
        public int mTotalPss;
        public int mDalvikPss;
        public int mNativePss;
        public int mOtherPss;
    }

    /**
     * 手机RAM内存信息
     * 物理内存信息
     */
    public static class RamMemoryInfo {
        public long mAvailRAM;           //可用RAM
        public long mTotalRAM;           //手机总RAM
        public long mLowRAMThreshold;   //内存占用满的阀值，超过即认为低内存运行状态，可能会Kill process
        public boolean mIsLowRAM;     //是否低内存状态运行
    }

    /**
     * 所有信息
     */
    public static class AllInfo {
        public DalvikHeapMem mDalvikHeapMem;
        public PssInfo mPssInfo;
        public RamMemoryInfo mRamMemoryInfo;
    }

    /**
     * 异步获取总体内存使用情况
     */
    public static void getMemoryInfoAsync(final Context context, final OnGetMemoryInfoCallback onGetMemoryInfoCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //package name
                final String pkgName = context.getPackageName();
                //pid
                final int pid = ProcessUtil.getCurrentPid();
                //ram
                final RamMemoryInfo ramMemoryInfo = getSystemRamSync(context);
                //pss
                final PssInfo pssInfo = MemoryUtil.getAppPssInfo(context, pid);
                //dalvik heap
                final DalvikHeapMem dalvikHeapMem = MemoryUtil.getAppDalvikHeapMem();
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onGetMemoryInfoCallback.onGetMemoryInfo(pkgName, pid, ramMemoryInfo, pssInfo, dalvikHeapMem);
                    }
                });
            }
        }).start();
    }

    /**
     * 同步获取总体内存使用情况
     */
    public static @NonNull AllInfo getMemoryInfoSync(final Context context) {
        //package name
        final String pkgName = context.getPackageName();
        //pid
        final int pid = ProcessUtil.getCurrentPid();
        //ram
        final RamMemoryInfo ramMemoryInfo = getSystemRamSync(context);
        //pss
        final PssInfo pssInfo = getAppPssInfo(context, pid);
        //dalvik heap
        final DalvikHeapMem dalvikHeapMem = getAppDalvikHeapMem();
        //all info
        final AllInfo allInfo = new AllInfo();
        allInfo.mDalvikHeapMem = dalvikHeapMem;
        allInfo.mPssInfo = pssInfo;
        allInfo.mRamMemoryInfo = ramMemoryInfo;
        return allInfo;
    }

    /**
     * 异步获取手机RAM的存储情况
     */
    public static void getSystemRamAsync(final Context context, final OnGetRamMemoryInfoCallback onGetRamMemoryInfoCallback) {
        getRamTotalMemAsync(context, new OnGetRamTotalMemCallback() {
            @Override
            public void onGetRamTotalMem(long totalMem) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                final ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                RamMemoryInfo ramMemoryInfo = null;
                if (am != null) {
                    am.getMemoryInfo(mi);
                    ramMemoryInfo = new RamMemoryInfo();
                    ramMemoryInfo.mAvailRAM = mi.availMem / 1024;
                    ramMemoryInfo.mIsLowRAM = mi.lowMemory;
                    ramMemoryInfo.mLowRAMThreshold = mi.threshold / 1024;
                    ramMemoryInfo.mTotalRAM = totalMem;
                }
                onGetRamMemoryInfoCallback.onGetRamMemoryInfo(ramMemoryInfo);
            }
        });
    }

    /**
     * 同步获取手机RAM的存储情况
     */
    private static @Nullable RamMemoryInfo getSystemRamSync(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memoryInfo);
            RamMemoryInfo ramMemoryInfo = new RamMemoryInfo();
            ramMemoryInfo.mAvailRAM = memoryInfo.availMem / 1024;
            ramMemoryInfo.mIsLowRAM = memoryInfo.lowMemory;
            ramMemoryInfo.mLowRAMThreshold = memoryInfo.threshold / 1024;
            ramMemoryInfo.mTotalRAM = getRamTotalMemSync(context);
            return ramMemoryInfo;
        }
        return null;
    }

    /**
     * 获取应用实际占用内存
     */
    public static @Nullable PssInfo getAppPssInfo(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo memoryInfo;
        PssInfo pssInfo = null;
        if (am != null) {
            memoryInfo = am.getProcessMemoryInfo(new int[]{pid})[0];
            pssInfo = new PssInfo();
            pssInfo.mTotalPss = memoryInfo.getTotalPss();
            pssInfo.mDalvikPss = memoryInfo.dalvikPss;
            pssInfo.mNativePss = memoryInfo.nativePss;
            pssInfo.mOtherPss = memoryInfo.otherPss;
        }
        return pssInfo;
    }

    /**
     * 获取应用dalvik内存信息
     */
    public static DalvikHeapMem getAppDalvikHeapMem() {
        Runtime runtime = Runtime.getRuntime();
        DalvikHeapMem dalvikHeapMem = new DalvikHeapMem();
        dalvikHeapMem.mFreeMem = runtime.freeMemory() / 1024;                                  //对应dumpsys meminfo的Heap Free
        dalvikHeapMem.mMaxMem = runtime.maxMemory() / 1024;
        dalvikHeapMem.mTotalMem = runtime.totalMemory() / 1024;                                 //对应dumpsys meminfo的Heap Size
        dalvikHeapMem.mAllocatedMem = (dalvikHeapMem.mTotalMem - dalvikHeapMem.mFreeMem); //对应dumpsys meminfo的Heap Alloc
        return dalvikHeapMem;
    }

    public interface OnGetMemoryInfoCallback {
        void onGetMemoryInfo(String pkgName, int pid, @Nullable RamMemoryInfo ramMemoryInfo, @Nullable PssInfo pssInfo, DalvikHeapMem dalvikHeapMem);
    }

    public interface OnGetRamMemoryInfoCallback {
        void onGetRamMemoryInfo(@Nullable RamMemoryInfo ramMemoryInfo);
    }

    private interface OnGetRamTotalMemCallback {
        //手机总RAM容量/KB
        void onGetRamTotalMem(long totalMem);
    }

    /**
     * 异步获取系统的总ram大小
     */
    private static void getRamTotalMemAsync(final Context context, final OnGetRamTotalMemCallback onGetRamTotalMemCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long totalRam = getRamTotalMemSync(context);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onGetRamTotalMemCallback.onGetRamTotalMem(totalRam);
                    }
                });
            }
        }).start();
    }

    /**
     * 同步获取系统的总ram大小
     */
    private static long getRamTotalMemSync(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(mi);
            }
            return mi.totalMem / 1024;
        } else if (sTotalMem.get() > 0L) {//如果已经从文件获取过值，则不需要再次获取
            return sTotalMem.get();
        } else {
            final long tm = getRamTotalMemByFile();
            sTotalMem.set(tm);
            return tm;
        }
    }

    private static AtomicLong sTotalMem = new AtomicLong(0L);

    /**
     * 获取手机的RAM容量，其实和activityManager.getMemoryInfo(mi).totalMem效果一样，也就是说，在API16以上使用系统API获取，低版本采用这个文件读取方式
     */
    private static long getRamTotalMemByFile() {
        final String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine
                    .indexOf("MemTotal:"));
            br.close();
            return (long) Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}