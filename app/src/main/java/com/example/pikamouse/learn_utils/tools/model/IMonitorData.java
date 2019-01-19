package com.example.pikamouse.learn_utils.tools.model;

import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;

/**
 * @author: jiangfeng
 * @date: 2019/1/19
 */
public interface IMonitorData {
    void createMemoryData(MemoryUtil.AllInfo allInfo);
    void createFrame(String frame);
    void createNetData(long txByte, long rxByte, long rate);
    void createCPUData(float value);
}
