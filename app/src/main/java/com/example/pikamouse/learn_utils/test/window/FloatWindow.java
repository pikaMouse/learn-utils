package com.example.pikamouse.learn_utils.test.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.pikamouse.learn_utils.test.view.FloatContainerView;

/**
 * create by liting 2018/12/29
 */
public class FloatWindow implements IFloatWindow, View.OnTouchListener {

    private static final String TAG = "FloatWindow";
    private WindowManager mWm;
    private WindowManager.LayoutParams mLp;
    private View mContentView;
    private boolean isAddSucess;

    private FloatContainerView.Callback mCallback = new FloatContainerView.CallbackAdapter() {
        @Override
        public void onMove(WindowManager.LayoutParams layoutParams) {
            super.onMove(layoutParams);
        }
    };

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
        this.mLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        this.mLp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        this.mLp.format = PixelFormat.TRANSLUCENT;

        this.mLp.gravity = gravity;
        this.mLp.width = (width == 0 ? -2 : width);
        this.mLp.height = (height == 0 ? -2 : height);
        this.mLp.x = x;
        this.mLp.y = y;
        try
        {
            this.mContentView = view;
            this.mContentView.setOnTouchListener(this);
            this.mWm.addView(this.mContentView, this.mLp);
            isAddSucess = true;
        }
        catch (Exception e)
        {
            isAddSucess = false;
            Log.d("FloatWindow", "悬浮窗添加失败:" + e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isAddSucess) return false;
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mLp.x = rawX;
                mLp.y = rawY;
                mWm.updateViewLayout(mContentView, mLp);
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }
        return false;
    }
}

