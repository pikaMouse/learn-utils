package com.example.pikamouse.learn_utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.utils.DisplayUtil;
import com.example.pikamouse.learn_utils.view.container.Container;

/**
 * create by liting 2018/12/27
 */
public class MemoryView extends SurfaceView implements Runnable, SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;




    private Paint mBorderLinePaint;
    private Paint mTextPaint;
    private Paint mBrokenLinePaint;
    private int maxValue = 40;

    private int mViewWidth;
    private int mViewHeight;

    /**边框的左边距*/
    private float mBrokenLineLeft = DisplayUtil.dp2px(40);
    /**边框的上边距*/
    private float mBrokenLineTop = DisplayUtil.dp2px(40);
    /**边框的下边距*/
    private float mBrokenLineBottom = DisplayUtil.dp2px(40);
    /**边框的右边距*/
    private float mBrokenLinerRight = DisplayUtil.dp2px(20);
    /**需要绘制的宽度*/
    private float mNeedDrawWidth;
    /**需要绘制的高度*/
    private float mNeedDrawHeight;
    /**边框文本*/
    private int[] valueText = new int[]{40,30,20,10,0};
    /**数据值*/
    private int[] value = new int[]{11,10,15,12,34,12,22,23,33,13};
    /**是否绘制到了屏幕边际**/
    private boolean mIsOnRightBorder = false;

    private Container mContainer;
    private MemoryGraph mMemoryGraph;
    private Abscissa mAbscissa;
    private Ordinate mOrdinate;

    public MemoryView(Context context) {
        this(context, null);
    }

    public MemoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MemoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initPaint();
    }



    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        mContainer = new Container();

        mMemoryGraph = new MemoryGraph();
        mContainer.addChildren(mMemoryGraph);
        mAbscissa = new Abscissa();
        mContainer.addChildren(mAbscissa);
        mOrdinate = new Ordinate();
        mContainer.addChildren(mOrdinate);
    }

    private void initPaint() {
        if (mBorderLinePaint == null) {
            mBorderLinePaint = new Paint();
        }
        initPaintAttr(mBorderLinePaint);
        if (mTextPaint == null) {
            mTextPaint = new Paint();
            mTextPaint.setTextSize(DisplayUtil.sp2px(10));
        }
        initPaintAttr(mTextPaint);
        if (mBrokenLinePaint == null) {
            mBrokenLinePaint = new Paint();
        }
        initPaintAttr(mBrokenLinePaint);
    }

    private void initPaintAttr(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        mNeedDrawHeight = mViewHeight - mBrokenLineTop - mBrokenLineBottom;
        mNeedDrawWidth = mViewWidth - mBrokenLineLeft - mBrokenLinerRight;
        mIsDrawing = true;
        mMemoryGraph.setWidth(mViewWidth);
        mMemoryGraph.setMoveToLeft(true);
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            draw();
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mContainer.draw(mCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 横坐标
     */
    private class Abscissa extends Container {

        /**边框文本*/
        private int[] valueText = new int[]{0,10,20,30,40};
        /**横坐标的分度**/
        private int abscissaScale = 10;

        public Abscissa() {

        }

        @Override
        public void childDraw(Canvas canvas) {
            super.childDraw(canvas);
            canvas.drawLine(mBrokenLineLeft, mViewHeight - mBrokenLineBottom, mViewWidth - mBrokenLinerRight, mViewHeight - mBrokenLineBottom, mBorderLinePaint);
            /*绘制竖线*/
            float width = (mNeedDrawWidth / abscissaScale);
            for (int i = 0; i <= abscissaScale; i++) {
                float left = mBrokenLineLeft + width * i;
                canvas.drawLine(left, mBrokenLineTop, left, mViewHeight - mBrokenLineBottom, mBorderLinePaint);
            }
        }
    }
    /**
     * 纵坐标
     */
    private class Ordinate extends Container {

        public Ordinate(){

        }

        @Override
        public void childDraw(Canvas canvas) {
            super.childDraw(canvas);
            /*绘制横线*/
            int len = valueText.length;
            float averageHeight = mNeedDrawHeight / (len - 1);
            for (int i = 0; i < len; i++) {
                float height = averageHeight * i;
                mCanvas.drawLine(mBrokenLineLeft, mBrokenLineTop + height, mViewWidth - mBrokenLinerRight, mBrokenLineTop + height, mBrokenLinePaint);
                mCanvas.drawText(valueText[i] + "", mBrokenLineLeft - DisplayUtil.dp2px(15), mBrokenLineTop + height, mTextPaint);
            }
        }
    }
    /**
     * 内存曲线图
     */
    private class MemoryGraph extends Container {
        private int x;
        private int y;
        private Paint mGraphPaint;
        private Path mGraphPath;
        private boolean mIsMoveToLeft = false;
        private int mWidth;

        public MemoryGraph() {
            mGraphPaint = new Paint();
            mGraphPath = new Path();
            mGraphPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
            mGraphPaint.setStyle(Paint.Style.STROKE);
            mGraphPaint.setAntiAlias(true);
            mGraphPaint.setStrokeWidth(5);
        }

        @Override
        public void childDraw(Canvas canvas) {
            super.childDraw(canvas);
            canvas.drawPath(mGraphPath, mGraphPaint);
            /*控制移动*/
            if (mIsMoveToLeft && x >= mWidth) {
                this.setX(getX() - 1);
            }
            /*复位*/
            if (x >= Integer.MAX_VALUE || y >= Integer.MAX_VALUE) {
                reset();
            }
            x += 1;
            y = (int)(100 * Math.sin(x * 2 * Math.PI / 180) + 400);
            mGraphPath.lineTo(x, y);
        }

        public void setMoveToLeft(boolean moveToLeft) {
            mIsMoveToLeft = moveToLeft;
        }

        public void setWidth(int mWidth) {
            this.mWidth = mWidth;
        }

        public void reset() {
            mGraphPath.reset();
            x = 0;
            y = 0;
            setX(0);
            setY(0);
        }
    }

}
