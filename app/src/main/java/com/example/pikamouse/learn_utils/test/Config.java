package com.example.pikamouse.learn_utils.test;

/**
 * create by liting 2018/12/29
 */

import android.graphics.Color;

public class Config
{
    public static final int DEFAULT_X_TEXT_PADDING = 0;
    public static final int DEFAULT_Y_TEXT_PADDING = 0;
    public static final float DEFAULT_Y_MAX_MULTIPLE = 1.2F;
    public static final float DEFAULT_Y_MIN_MULTIPLE = 0.8F;
    public static final int DEFAULT_Y_PART_COUNT = 5;
    public static final int DEFAULT_DATA_SIZE = 30;
    public static final String DEFAULT_Y_FORMAT = "%.1f";
    public static final int DEFAULT_XY_COLOR = -7829368;
    public static final float DEFAULT_XY_STROKE_WIDTH = 2.0F;
    public static final int DEFAULT_LINE_COLOR = -7829368;
    public static final float DEFAULT_LINE_STROKE_WIDTH = 2.0F;
    public static final int DEFAULT_FILL_COLOR = Color.parseColor("#aa0000FF");
    public static final int DEFAULT_Y_LABEL_COLOR = -7829368;
    public static final int DEFAULT_Y_LABEL_SIZE = 12;
    public static final int DEFAULT_GRADUATEDLINE_COLOR = Color.parseColor("#bbbbbb");
    public static final float DEFAULT_GRADUATEDLINE_STROKE_WIDTH = 1.0F;
    public int mGraduatedLineColor = DEFAULT_GRADUATEDLINE_COLOR;
    public float mGraduatedLineStrokeWidth = 1.0F;
    public int mYLabelColor = -7829368;
    public float mYLabelSize = 12.0F;
    public int mXTextPadding = 0;
    public int mYTextPadding = 0;
    public float mMaxValueMulti = 1.2F;
    public float mMinValueMulti = 0.8F;
    public int mYPartCount = 5;
    public int mDataSize = 30;
    public String mYFormat = "%.1f";
    public int mXYColor = -7829368;
    public float mXYStrokeWidth = 2.0F;
    public int mLineColor = -7829368;
    public float mLineStrokeWidth = 2.0F;
    public int mFillColor = DEFAULT_FILL_COLOR;

    public static class Builder
    {
        private Config mConfig;

        public Builder()
        {
            this.mConfig = new Config();
        }

        public Builder setXTextPadding(int XTextPadding)
        {
            this.mConfig.mXTextPadding = XTextPadding;
            return this;
        }

        public Builder setYTextPadding(int YTextPadding)
        {
            this.mConfig.mYTextPadding = YTextPadding;
            return this;
        }

        public Builder setMaxValueMulti(float maxValueMulti)
        {
            this.mConfig.mMaxValueMulti = maxValueMulti;
            return this;
        }

        public Builder setMinValueMulti(float minValueMulti)
        {
            this.mConfig.mMinValueMulti = minValueMulti;
            return this;
        }

        public Builder setYPartCount(int YPartCount)
        {
            this.mConfig.mYPartCount = YPartCount;
            return this;
        }

        public Builder setDataSize(int dataSize)
        {
            this.mConfig.mDataSize = dataSize;
            return this;
        }

        public Builder setYFormat(String format)
        {
            this.mConfig.mYFormat = format;
            return this;
        }

        public Builder setXYColor(int XYColor)
        {
            this.mConfig.mXYColor = XYColor;
            return this;
        }

        public Builder setXYStrokeWidth(float XYStrokeWidth)
        {
            this.mConfig.mXYStrokeWidth = XYStrokeWidth;
            return this;
        }

        public Builder setLineColor(int lineColor)
        {
            this.mConfig.mLineColor = lineColor;
            return this;
        }

        public Builder setLineStrokeWidth(float lineStrokeWidth)
        {
            this.mConfig.mLineStrokeWidth = lineStrokeWidth;
            return this;
        }

        public Builder setFillColor(int fillColor)
        {
            this.mConfig.mFillColor = fillColor;
            return this;
        }

        public Builder setYLabelColor(int yLabelColor)
        {
            this.mConfig.mYLabelColor = yLabelColor;
            return this;
        }

        public Builder setYLabelSize(float yLabelSize)
        {
            this.mConfig.mYLabelSize = yLabelSize;
            return this;
        }

        public Builder setGraduatedLineColor(int graduatedLineColor)
        {
            this.mConfig.mGraduatedLineColor = graduatedLineColor;
            return this;
        }

        public Builder setGraduatedStrokeWidth(float graduatedLineStrokeWidth)
        {
            this.mConfig.mGraduatedLineStrokeWidth = graduatedLineStrokeWidth;
            return this;
        }

        public Config create()
        {
            return this.mConfig;
        }
    }
}

