package com.example.pikamouse.learn_utils.test.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.MyApplication;

/**
 * @author: jiangfeng
 * @date: 2018/12/28
 */
public class DisplayUtil {

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        return height;
    }

    /**
     * dp转px
     *
     * @param dpValue float
     * @return int
     */
    public static int dp2px(float dpValue) {
        float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param sp float
     * @return int
     */
    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, MyApplication.getAppContext().getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxValue float
     * @return int
     */
    public static int px2dp(float pxValue) {
        float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
