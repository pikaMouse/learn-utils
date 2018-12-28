package com.example.pikamouse.learn_utils.test;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * create by liting 2018/12/29
 */
public class FloatContainer
        implements IFloatView
{
    private static final String TAG = "FloatContainer";
    private WindowManager mWm;
    private WindowManager.LayoutParams mLp;
    private View mContentView;

    public FloatContainer(Context context)
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
        this.mLp.type = 2005;
        this.mLp.format = -3;
        this.mLp.flags = 24;

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
            Log.d("FloatContainer", "悬浮窗添加失败:" + e.getLocalizedMessage());
        }
    }
}

