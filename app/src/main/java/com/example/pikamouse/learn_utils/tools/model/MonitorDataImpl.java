package com.example.pikamouse.learn_utils.tools.model;

import com.example.pikamouse.learn_utils.tools.util.MemoryUtil;

/**
 * @author: jiangfeng
 * @date: 2019/1/21
 */
public abstract class MonitorDataImpl implements IMonitorData {

    @Override
    public void createCPUData(float value) {

    }

    @Override
    public void createMemoryData(MemoryUtil.AllInfo allInfo) {

    }

    @Override
    public void createNetData(long txByte, long rxByte, long rate) {

    }

    @Override
    public void createFrame(String frame) {

    }
}
