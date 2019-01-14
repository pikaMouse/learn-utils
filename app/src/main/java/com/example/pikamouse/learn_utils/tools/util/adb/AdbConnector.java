package com.example.pikamouse.learn_utils.tools.util.adb;

import java.net.Socket;

/**
 * @author: jiangfeng
 * @date: 2019/1/14
 */
public class AdbConnector {
    private final static String TAG = "AdbConnector";
    private final Socket mSocket;

    public AdbConnector(Socket mSocket) {
        this.mSocket = mSocket;
    }
}
