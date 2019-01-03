package com.example.pikamouse.learn_utils.test;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public interface IMonitor {
    void init(Context context);
    void start(@Nullable @MonitorManager.MonitorType String type);
    void stop();
}
