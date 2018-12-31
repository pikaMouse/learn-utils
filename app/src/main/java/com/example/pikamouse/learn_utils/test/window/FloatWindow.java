package com.example.pikamouse.learn_utils.test.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * create by jiangfeng 2018/12/30
 * Window层
 */
public abstract class FloatWindow implements IFloatWindow {

    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;
    protected View mContentView;
    protected Context mContext;
    private boolean isAddWindowSuccess;


    public FloatWindow(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mContext = context;
    }

    @Override
    public void release() {
        if (mContentView != null && mContentView.getParent() != null) {
            mWindowManager.removeView(mContentView);
        }
    }

    @Override
    public void attachToWindow(View view, WindowManager.LayoutParams layoutParams) {
        if (view.getParent() != null) {
            Log.d("FloatWindow", "view添加失败: Parent of view is not null !" );
            return;
        }
        mLayoutParams.flags = layoutParams.flags;
        mLayoutParams.type = layoutParams.type;
        mLayoutParams.format = layoutParams.format;
        mLayoutParams.gravity = layoutParams.gravity;
        mLayoutParams.x = layoutParams.x;
        mLayoutParams.y = layoutParams.y;
        mLayoutParams.width = layoutParams.width;
        mLayoutParams.height = layoutParams.height;
        try {
            mContentView = view;
            mWindowManager.addView(mContentView, mLayoutParams);
            isAddWindowSuccess = true;
        } catch (Exception e) {
            isAddWindowSuccess = false;
            Log.d("FloatWindow", "悬浮窗添加失败: " + e.getLocalizedMessage());
        }

    }

    public boolean isAddWindowSuccess() {
        return isAddWindowSuccess;
    }

    public static class WMLayoutParamsBuilder {

        private final static int DEFAULT_FORMAT = PixelFormat.TRANSLUCENT;
        private final static int DEFAULT_FLAG = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        private final static int DEFAULT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        private final static int DEFAULT_GRAVITY = Gravity.TOP | Gravity.LEFT;
        private final static int DEFAULT_WIDTH = WindowManager.LayoutParams.WRAP_CONTENT;
        private final static int DEFAULT_HEIGHT = WindowManager.LayoutParams.WRAP_CONTENT;
        private final static int DEFAULT_X = 0;
        private final static int DEFAULT_Y = 0;
        private WindowManager.LayoutParams mLayoutParams;

        public WMLayoutParamsBuilder() {
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.format = DEFAULT_FORMAT;
            mLayoutParams.flags = DEFAULT_FLAG;
            mLayoutParams.type = DEFAULT_TYPE;
            mLayoutParams.gravity = DEFAULT_GRAVITY;
            mLayoutParams.width = DEFAULT_WIDTH;
            mLayoutParams.height = DEFAULT_HEIGHT;
            mLayoutParams.x = DEFAULT_X;
            mLayoutParams.y = DEFAULT_Y;
        }

        public WMLayoutParamsBuilder setFormat(int format) {
            this.mLayoutParams.format = format;
            return this;
        }

        public WMLayoutParamsBuilder setFlag(int flag) {
            this.mLayoutParams.flags = flag;
            return this;
        }

        public WMLayoutParamsBuilder setType(int type) {
            this.mLayoutParams.type = type;
            return this;
        }

        public WMLayoutParamsBuilder setGravity(int gravity) {
            this.mLayoutParams.gravity = gravity;
            return this;
        }

        public WMLayoutParamsBuilder setWidth(int width) {
            this.mLayoutParams.width = (width == 0 ? WindowManager.LayoutParams.WRAP_CONTENT : width );
            return this;
        }

        public WMLayoutParamsBuilder setHeight(int height) {
            this.mLayoutParams.height = (height == 0 ? WindowManager.LayoutParams.WRAP_CONTENT : height );
            return this;
        }

        public WMLayoutParamsBuilder setX(int x) {
            this.mLayoutParams.x = x;
            return this;
        }

        public WMLayoutParamsBuilder setY(int y) {
            this.mLayoutParams.y = y;
            return this;
        }

        public WindowManager.LayoutParams build () {
            return mLayoutParams;
        }
    }
}
