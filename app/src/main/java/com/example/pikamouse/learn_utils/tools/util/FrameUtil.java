package com.example.pikamouse.learn_utils.tools.util;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;

/**
 * @author: jiangfeng
 * @date: 2019/1/19
 */
public class FrameUtil {
    private final static String TAG = "FrameUtil";
    private final static float SECOND_TO_NANOS = 1000000000f;
    private int mLastFrame;
    private long mLastFrameTimeNanos;
    private CallBack mCallBack;

    private FrameUtil() {

    }

    private static class SingleHolder{
        private final static FrameUtil INSTANCE = new FrameUtil();
    }

    public static FrameUtil getInstance() {
        return SingleHolder.INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Choreographer.FrameCallback mFrameCallBack = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (mLastFrameTimeNanos == 0) {
                mLastFrameTimeNanos = frameTimeNanos;
            }
            long temp = frameTimeNanos - mLastFrameTimeNanos;
            if (temp != 0) {
                mLastFrame = Math.round(SECOND_TO_NANOS / temp);
                if (mCallBack != null) {
                    mCallBack.onSuccess(mLastFrame);
                }
            }
            mLastFrameTimeNanos = frameTimeNanos;
            //注册下一帧回调
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    public interface CallBack {
        void onSuccess(int value);
    }

    public void getFrameInfo(CallBack callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Choreographer.getInstance().postFrameCallback(mFrameCallBack);
        }
        mCallBack = callBack;
    }

    public void stopFrameInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Choreographer.getInstance().removeFrameCallback(mFrameCallBack);
        }
        mCallBack = null;
    }
}
