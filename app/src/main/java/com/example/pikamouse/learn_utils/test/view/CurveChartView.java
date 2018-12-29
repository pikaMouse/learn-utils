package com.example.pikamouse.learn_utils.test.view;

/**
 * create by liting 2018/12/29
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.pikamouse.learn_utils.R;

import java.util.ArrayList;
import java.util.List;

public class CurveChartView extends View {
    private CurveChartConfig mConfig;
    private float yMaxValue = -2.14748365E9F;
    private float yMinValue = 2.14748365E9F;
    private String[] mYLabels;
    private List<Float> mDatas = new ArrayList();
    private Path mLinePath;
    private Path mFillPath;
    private Paint mPaint;

    public CurveChartView(Context context)
    {
        this(context, null);
    }

    public CurveChartView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initWaveView(context, attrs);
    }

    private void initWaveView(Context context, AttributeSet attrs)
    {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mLinePath = new Path();
        this.mFillPath = new Path();
        CurveChartConfig config;
        if (attrs == null)
        {
            config = new CurveChartConfig();
        }
        else
        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CurveChartView);

            CurveChartConfig.Builder builder = new CurveChartConfig.Builder()
                    .setXTextPadding(a.getInteger(R.styleable.CurveChartView_ccv_XTextPadding, 0))
                    .setYTextPadding(a.getInteger(R.styleable.CurveChartView_ccv_YTextPadding, 0))
                    .setMaxValueMulti(a.getFloat(R.styleable.CurveChartView_ccv_MaxValueMulti, 1.2F))
                    .setMinValueMulti(a.getFloat(R.styleable.CurveChartView_ccv_MinValueMulti, 0.8F))
                    .setYPartCount(a.getInteger(R.styleable.CurveChartView_ccv_YPartCount, 5))
                    .setDataSize(a.getInteger(R.styleable.CurveChartView_ccv_DataSize, 30))
                    .setYFormat(a.getString(R.styleable.CurveChartView_ccv_YFormat))
                    .setXYColor(a.getColor(R.styleable.CurveChartView_ccv_XYColor, -7829368))
                    .setXYStrokeWidth(a.getFloat(R.styleable.CurveChartView_ccv_XYStrokeWidth, 2.0F))
                    .setLineColor(a.getColor(R.styleable.CurveChartView_ccv_LineColor, -7829368))
                    .setLineStrokeWidth(a.getFloat(R.styleable.CurveChartView_ccv_LineStrokeWidth, 2.0F))
                    .setFillColor(a.getColor(R.styleable.CurveChartView_ccv_FillColor, CurveChartConfig.DEFAULT_FILL_COLOR))
                    .setYLabelColor(a.getColor(R.styleable.CurveChartView_ccv_YLabelColor, -7829368))
                    .setYLabelSize(a.getFloat(R.styleable.CurveChartView_ccv_YLabelSize, 12.0F))
                    .setGraduatedLineColor(a.getColor(R.styleable.CurveChartView_ccv_GraduatedLineColor, CurveChartConfig.DEFAULT_GRADUATEDLINE_COLOR))
                    .setGraduatedStrokeWidth(a.getFloat(R.styleable.CurveChartView_ccv_GraduatedLineStrokeWidth, 1.0F));
            a.recycle();
            config = builder.create();
        }
        setUp(config);
    }

    public void setUp(CurveChartConfig config)
    {
        if (config == null) {
            return;
        }
        this.mConfig = config;
        this.mYLabels = new String[this.mConfig.mYPartCount];
        this.mDatas.clear();
    }

    public void addData(float data)
    {
        this.mDatas.add(Float.valueOf(data));
        if (this.mDatas.size() > this.mConfig.mDataSize) {
            this.mDatas.remove(0);
        }
        prepareData();
        postInvalidate();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawXY(canvas);
        int size = this.mDatas.size();
        if (size == 0) {
            return;
        }
        drawLine(canvas, size);
        drawScaleLabel(canvas);
    }

    private void drawXY(Canvas canvas)
    {
        this.mPaint.reset();
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setColor(this.mConfig.mXYColor);
        this.mPaint.setStrokeWidth(this.mConfig.mXYStrokeWidth);

        canvas.drawLine(getXPoint(), getPaddingTop(), getXPoint(), getYPoint(), this.mPaint);

        canvas.drawLine(getXPoint(), getYPoint(), getWidth() - getPaddingRight(), getYPoint(), this.mPaint);
    }

    private void drawScaleLabel(Canvas canvas)
    {
        this.mPaint.reset();
        this.mPaint.setStyle(Paint.Style.STROKE);
        createYText();
        int yIntervalLen = getYLen() / (this.mConfig.mYPartCount - 1);
        for (int i = 0; i < this.mConfig.mYPartCount; i++)
        {
            int scaleY = getYPoint() - yIntervalLen * i;
            this.mPaint.setColor(this.mConfig.mGraduatedLineColor);
            this.mPaint.setStrokeWidth(this.mConfig.mGraduatedLineStrokeWidth);

            canvas.drawLine(getXPoint(), scaleY, getWidth() - getPaddingRight(), scaleY, this.mPaint);
            if (!TextUtils.isEmpty(this.mConfig.mYFormat))
            {
                this.mPaint.setColor(this.mConfig.mYLabelColor);
                this.mPaint.setStrokeWidth(0.0F);
                this.mPaint.setTextSize(this.mConfig.mYLabelSize);

                canvas.drawText(this.mYLabels[i], getPaddingLeft(), scaleY, this.mPaint);
            }
        }
    }

    private void drawLine(Canvas canvas, int size)
    {
        this.mLinePath.reset();
        this.mFillPath.reset();
        this.mLinePath.moveTo(getXPoint(), getYPoint() - (((Float)this.mDatas.get(0)).floatValue() - this.yMinValue) * getYLenPerValue());
        this.mFillPath.moveTo(getXPoint(), getYPoint());
        this.mFillPath.lineTo(getXPoint(), getYPoint() - (((Float)this.mDatas.get(0)).floatValue() - this.yMinValue) * getYLenPerValue());
        for (int i = 1; i < size; i++)
        {
            float value = ((Float)this.mDatas.get(i)).floatValue();
            this.mLinePath.lineTo(getXPoint() + i * getXLenPerCount(), getYPoint() - (value - this.yMinValue) * getYLenPerValue());
            this.mFillPath.lineTo(getXPoint() + i * getXLenPerCount(), getYPoint() - (value - this.yMinValue) * getYLenPerValue());
        }
        this.mFillPath.lineTo(getXPoint() + (size - 1) * getXLenPerCount(), getYPoint());
        this.mFillPath.close();

        this.mPaint.reset();
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setColor(this.mConfig.mLineColor);
        this.mPaint.setStrokeWidth(this.mConfig.mLineStrokeWidth);
        canvas.drawPath(this.mLinePath, this.mPaint);

        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mConfig.mFillColor);
        canvas.drawPath(this.mFillPath, this.mPaint);
    }

    private void prepareData()
    {
        float maxValue = -2.14748365E9F;
        float minValue = 2.14748365E9F;
        int size = this.mDatas.size();
        for (int i = 0; i < size; i++)
        {
            float v = (Float) this.mDatas.get(i);
            if (v > maxValue) {
                maxValue = v;
            }
            if (v < minValue) {
                minValue = v;
            }
        }
        this.yMaxValue = (this.mConfig.mMaxValueMulti * maxValue);
        this.yMinValue = (this.mConfig.mMinValueMulti * minValue);
    }

    private void createYText()
    {
        if (TextUtils.isEmpty(this.mConfig.mYFormat)) {
            return;
        }
        for (int i = 0; i < this.mConfig.mYPartCount; i++) {
            this.mYLabels[i] = String.format(this.mConfig.mYFormat,
                    new Object[] { Float.valueOf((this.yMaxValue - this.yMinValue) * i / (this.mConfig.mYPartCount - 1) + this.yMinValue) });
        }
    }

    private float getYLenPerValue()
    {
        return getYLen() / (this.yMaxValue - this.yMinValue);
    }

    private float getXLenPerCount()
    {
        return getXLen() / (this.mConfig.mDataSize - 1);
    }

    private int getXPoint()
    {
        return getPaddingLeft() + this.mConfig.mXTextPadding;
    }

    private int getYPoint()
    {
        return getHeight() - getPaddingBottom() - this.mConfig.mYTextPadding;
    }

    private int getXLen()
    {
        return getWidth() - getPaddingLeft() - getPaddingRight() - this.mConfig.mXTextPadding;
    }

    private int getYLen()
    {
        return getHeight() - getPaddingBottom() - getPaddingTop() - this.mConfig.mYTextPadding;
    }
}

