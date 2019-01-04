package com.example.pikamouse.learn_utils.tools.monitor;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.pikamouse.learn_utils.MonitorManager;

/**
 * @author: jiangfeng
 * @date: 2019/1/3
 */
public interface IMonitor {
    void init(Context context);
    void start(@Nullable @MonitorManager.MonitorType String type);
    void stop();
}
