package com.example.pikamouse.learn_utils.tools.util.adb;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;


/**
 * @author: jiangfeng
 * @date: 2019/1/14
 */
public class AdbConnector {
    private final static String TAG = "AdbConnector";
    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private int mLocalId;
    private int mRemoteId;
    private Thread mConnectionThread;
    private boolean mIsConnected = false;
    private String mAcceptMessage = null;
    private volatile static AdbConnector sInstance;

    private AdbConnector() throws IOException, InterruptedException {
        Log.d(TAG, "Socket connecting...");
        mSocket = new Socket(AdbConstant.HOST, AdbConstant.PORT);
        Log.d(TAG, "Socket connected");
        mConnectionThread = createConnectionThread();
        mInputStream = mSocket.getInputStream();
        mOutputStream = mSocket.getOutputStream();
        connect();
    }

    public static AdbConnector getInstance() throws IOException, InterruptedException {
        synchronized (AdbConnector.class) {
            if (sInstance == null) {
                sInstance = new AdbConnector();
            }
        }
        return sInstance;
    }

    public String openShell(String cmd) throws IOException, InterruptedException {
        return open(cmd);
    }

    private void connect() throws IOException, InterruptedException {
        if (mIsConnected) {
            throw new IllegalStateException("Already connected !");
        } else {
            mOutputStream.write(AdbProtocol.generateConnect());
            mOutputStream.flush();
            Log.d(TAG, "Socket is writing a connective cmd...");
            mConnectionThread.start();
            synchronized (AdbConnector.this) {
                if (!mIsConnected) {
                    this.wait();
                }
                if (!mIsConnected) {
                    throw new IllegalStateException("Connection is failed !");
                }
            }
        }
    }

    private String open(String destination) throws InterruptedException, IOException {
        synchronized (AdbConnector.this) {
            if (!mIsConnected) {
                this.wait();
            }
            if (!mIsConnected) {
                throw new IllegalStateException("Connection is failed !");
            }
        }
        int localId = ++mLocalId;
        mAcceptMessage = null;
        mOutputStream.write(AdbProtocol.generateOpen(localId, destination));
        mOutputStream.flush();
        Log.d(TAG, "Socket is writing a opened cmd...");
        synchronized (AdbConnector.this) {
            if (mAcceptMessage == null) {
                this.wait();
            }
            if (mAcceptMessage == null) {
                throw new ConnectException("Stream open actively rejected by remote peer !");
            }
        }

        return mAcceptMessage;
    }

    private Thread createConnectionThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!mConnectionThread.isInterrupted()) {
                        try {
                            AdbProtocol.AdbMessage msg = AdbProtocol.AdbMessage.parseAdbMessage(AdbConnector.this.mInputStream);
                            if (!AdbProtocol.validateMessage(msg)) {
                                continue;
                            }
                            switch (msg.command) {
                                case AdbProtocol.CMD_CNXN:
                                    Log.d(TAG, "Socket accepted CMD_CNXN");
                                    synchronized (AdbConnector.this) {
                                        mIsConnected = true;
                                        AdbConnector.this.notifyAll();
                                    }
                                    continue;
                                case AdbProtocol.CMD_OKAY:
                                    Log.d(TAG, "Socket accepted A_OKAY");
                                    if (msg.arg1 != mLocalId) continue;
                                    mRemoteId = msg.arg0;
                                    continue;
                                case AdbProtocol.CMD_WRTE:
                                    Log.d(TAG, "Socket accepted A_WRTE");
                                    if (msg.arg1 != mLocalId) continue;
                                    mRemoteId = msg.arg0;
                                    mAcceptMessage = new String(msg.payload);
                                    synchronized (AdbConnector.this) {
                                        AdbConnector.this.notifyAll();
                                    }
                                    mOutputStream.write(AdbProtocol.generateReady(mLocalId, mRemoteId));
                                    mOutputStream.flush();
                                    continue;
                                case AdbProtocol.CMD_CLSE:
                                    Log.d(TAG, "Socket accepted A_CLSE");
                                    continue;
                                default:
                                    continue;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (AdbConnector.this) {
                        Log.d(TAG, "Accepted Thread is interrupted !");
                        mAcceptMessage = null;
                        mLocalId = 0;
                        mRemoteId = 0;
                        AdbConnector.this.notifyAll();
                        return;
                    }
                }
            }
        });
    }

}
