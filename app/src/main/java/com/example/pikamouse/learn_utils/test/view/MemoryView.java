package com.example.pikamouse.learn_utils.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.pikamouse.learn_utils.MyApplication;
import com.example.pikamouse.learn_utils.R;
import com.example.pikamouse.learn_utils.tools.util.DisplayUtil;
import com.example.pikamouse.learn_utils.test.utils.MemoryUtil;
import com.example.pikamouse.learn_utils.test.view.container.Container;

/**
 * create by liting 2018/12/27
 */
public class MemoryView extends SurfaceView implements Runnable, SurfaceHolder.Callback{

    private static final String TAG = "MemoryView";
    private static final int ORDINATE_SCAL = 4;

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;




    private Paint mBorderLinePaint;
    private Paint mTextPaint;
    private Paint mBrokenLinePaint;
    private int mMaxValue = 0;

    private int mViewWidth;
    private int mViewHeight;

    /**边框的左边距*/
    private float mBrokenLineLeft = 0;
    /**边框的上边距*/
    private float mBrokenLineTop = DisplayUtil.dp2px(40);
    /**边框的下边距*/
    private float mBrokenLineBottom = DisplayUtil.dp2px(40);
    /**边框的右边距*/
    private float mBrokenLinerRight = 0;
    /**需要绘制的宽度*/
    private float mNeedDrawWidth;
    /**需要绘制的高度*/
    private float mNeedDrawHeight;
    /**纵坐标文本*/
    private int[] mOrdinateValueText;
    /**是否绘制到了屏幕边际**/
    private boolean mIsOnRightBorder = false;
    /**纵坐标的刻度**/
    private float averageHeight;

    private float mValue;

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

    public void setData(MemoryUtil.DalvikHeapInfo dalvikHeapInfo) {
        mValue = (dalvikHeapInfo.allocatedMem / 1024);
        mMaxValue = (int) mValue;
        Log.d(TAG, "MaxValue: " + mValue);
        if (mMaxValue <= ORDINATE_SCAL) {
            mMaxValue = ORDINATE_SCAL;
        }
        mOrdinateValueText = new int[mMaxValue];
        for (int i = mMaxValue - 1, j = 0; i > 0; i--, j++) {
            mOrdinateValueText[j] = i;
        }
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
        mMemoryGraph.setHeight(mViewHeight);
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
            MemoryUtil.getDalvikInfo(MyApplication.getAppContext(), new MemoryUtil.OnGetDalvikInfoCallback() {

                @Override
                public void onGetDalvikInfo(MemoryUtil.DalvikHeapInfo dalvikHeapInfo) {
                    setData(dalvikHeapInfo);
                }
            });
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

        /**横坐标的分度**/
        private final static int ABSCISSA_SCALE = 10;

        public Abscissa() {

        }

        @Override
        public void childDraw(Canvas canvas) {
            super.childDraw(canvas);
            canvas.drawLine(mBrokenLineLeft, mViewHeight - mBrokenLineBottom, mViewWidth - mBrokenLinerRight, mViewHeight - mBrokenLineBottom, mBorderLinePaint);
            /*绘制竖线*/
            float width = (mNeedDrawWidth / ABSCISSA_SCALE);
            for (int i = 0; i <= ABSCISSA_SCALE; i++) {
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
             averageHeight = mNeedDrawHeight / (mMaxValue - 1);
            for (int i = 0; i < mMaxValue; i++) {
                float height = averageHeight * i;
                mCanvas.drawLine(mBrokenLineLeft, mBrokenLineTop + height, mViewWidth - mBrokenLinerRight, mBrokenLineTop + height, mBrokenLinePaint);
                mCanvas.drawText(mOrdinateValueText[i] + "", DisplayUtil.dp2px(15), mBrokenLineTop + height - DisplayUtil.dp2px(8), mTextPaint);
            }
        }
    }
    /**
     * 内存曲线图
     */
    private class MemoryGraph extends Container {
        private float x = 0;
        private float y = 0;
        private Paint mGraphPaint;
        private Path mGraphPath;
        private boolean mIsMoveToLeft = false;
        private int mWidth;
        private int mStartY;
        private boolean isFirst = true;

        public MemoryGraph() {
            mGraphPaint = new Paint();
            mGraphPath = new Path();
            mGraphPaint.setStyle(Paint.Style.STROKE);
            mGraphPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
            mGraphPaint.setAntiAlias(true);
            mGraphPaint.setStrokeWidth(5);
        }

        @Override
        public void childDraw(Canvas canvas) {
            super.childDraw(canvas);
            /*控制移动*/
            if (mIsMoveToLeft && x >= mWidth) {
                this.setX(getX() - 1);
            }
            /*复位*/
            if (x >= Float.MAX_VALUE || y >= Float.MAX_VALUE) {
                reset();
            }
            /*起点*/
            if (isFirst) {
                isFirst = false;
                mStartY = (int) (mViewHeight - mBrokenLineBottom - mValue * averageHeight);
            }
            mGraphPath.moveTo(0, mStartY);
            mGraphPath.lineTo(x, y);
            mGraphPath.lineTo(x, mViewHeight - mBrokenLineBottom);
            mGraphPath.lineTo(0, mViewHeight - mBrokenLineBottom);
            mGraphPath.lineTo(0, mStartY);
            canvas.drawPath(mGraphPath, mGraphPaint);
            x += 1;
            y = mViewHeight - mBrokenLineBottom - mValue * averageHeight;
        }

        public void setMoveToLeft(boolean moveToLeft) {
            mIsMoveToLeft = moveToLeft;
        }

        public void setWidth(int width) {
            this.mWidth = width;
            x = mBrokenLineLeft;
        }

        public void setHeight(int height){
            y = (height - mBrokenLineBottom);
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
