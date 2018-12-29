package com.example.pikamouse.learn_utils.test.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * create by liting 2018/12/29
 */
public class FloatWindow implements IFloatWindow {

    private static final String TAG = "FloatWindow";
    private WindowManager mWm;
    private WindowManager.LayoutParams mLp;
    private View mContentView;

    public FloatWindow(Context context)
    {
        this.mWm = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE));
        this.mLp = new WindowManager.LayoutParams();
    }

    public void release()
    {
        if ((this.mContentView != null) && (this.mContentView.getParent() != null)) {
            this.mWm.removeView(this.mContentView);
        }
    }

    public void attachToWindow(View view, int gravity, int x, int y, int width, int height)
    {
        if (view.getParent() != null) {
            return;
        }
        this.mLp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        this.mLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        this.mLp.format = PixelFormat.TRANSLUCENT;

        this.mLp.gravity = gravity;
        this.mLp.width = (width == 0 ? -2 : width);
        this.mLp.height = (height == 0 ? -2 : height);
        this.mLp.x = x;
        this.mLp.y = y;
        try
        {
            this.mContentView = view;
            this.mWm.addView(this.mContentView, this.mLp);
        }
        catch (Exception e)
        {
            Log.d("FloatWindow", "悬浮窗添加失败:" + e.getLocalizedMessage());
        }
    }
}

