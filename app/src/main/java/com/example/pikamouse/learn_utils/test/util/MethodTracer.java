package com.example.pikamouse.learn_utils.test.util;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by hui.zhao on 2016/6/3.
 */
public class MethodTracer {
    public static final String TAG = "MethodTracer";

    private static long sNow;

    public static void start() {
        sNow = System.nanoTime();
    }

    public static long endToResultForNano() {
        return System.nanoTime() - sNow;
    }

    public static long endToResultForMillis() {
        return TimeUnit.NANOSECONDS.toMillis(endToResultForNano());
    }

    public static void endToOutput(String name) {
        Log.d(TAG, name + " spent " + endToResultForMillis() + " millis");
    }
}
