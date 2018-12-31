package com.example.pikamouse.learn_utils.test.window;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

/**
 * create by jiangfeng 2018/12/31
 */
public interface IFloatWindow {

    void release();

    void attachToWindow(@NonNull View view, @NonNull WindowManager.LayoutParams layoutParams);

}
