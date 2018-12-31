package com.example.pikamouse.learn_utils.test.window;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * create by liting 2018/12/30
 *
 */
public class FloatBallWindow extends FloatWindow implements View.OnTouchListener {

    private int mLastX;
    private int mLastY;

    public FloatBallWindow(Context context) {
        super(context);
    }


    @Override
    public void attachToWindow(View view, WindowManager.LayoutParams layoutParams) {
        super.attachToWindow(view, layoutParams);
        mContentView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - mLastX;
                int dy = y - mLastY;
                mLayoutParams.x += dx;
                mLayoutParams.y += dy;
                // Window层通过View来控制移动
                mWindowManager.updateViewLayout(mContentView, mLayoutParams);
                mLastX = x;
                mLastY = y;
                break;
            default:
                break;
        }
        return false;
    }
}
