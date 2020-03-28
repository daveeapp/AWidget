package com.davee.awidgets;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * HorizonScaleBar
 * <p> 游标刻度尺控件
 * <p> 详细属性控制，{@link R.styleable.HorizonScaleBar}
 * Created by davee 2019/4/7.
 * Copyright (c) 2019 davee. All rights reserved.
 */
public class HorizonScaleBar extends View {
    
    private static final int UNITS_ONE_SECOND = 1000;
    private static final int TRIGGER_VELOCITY = 100;
    
    
    private int mValueMinimum = 0;
    private int mValueMaximum = 100;
    /**
     * value递增幅度
     */
    private int mValueStride = 1;
    /**
     * 第二刻度递增幅度
     */
    private int mSecondStride = 10; // 第二刻度
    
    /**
     * 当前选中的值(当valueStride > 1时，需要修改值计算方式)
     */
    private int mSelectedValue = 0;
    
    /// 游尺底部的横线
    private boolean mBaseLineEnable = true;
    private int mBaseLineColor = Color.LTGRAY;
    private int mBaseLineHeight = 1;
    
    /* 刻度线 */
    
    /**
     * 刻度间距(这里不计算 刻度线 本身的宽度)
     */
    private int mScaleSpacing = 18; // 8dp
    
    /**
     * 刻度线宽度
     */
    private int mScaleWidth = 2; // 1dp
    
    /**
     * 刻度线高度
     */
    private int mScaleHeight = 15; // 12dp
    
    /**
     * 刻度线颜色
     */
    private int mScaleColor = Color.LTGRAY;
    
    /**
     * 第二刻度线高度
     */
    private int mSecondScaleHeight = 30; // 18dp 大刻度
    
    /**
     * 第二刻度线颜色(默认 = mScaleColor)
     */
    private int mSecondScaleColor = Color.LTGRAY;
    
    /**
     * 指针颜色
     */
    private int mIndicatorColor = Color.RED;
    
    /**
     * 指针高度
     */
    private int mIndicatorHeight = 50;
    
    /**
     * 指针宽度
     */
    private int mIndicatorWidth = 2;
    
    /**
     * 刻度标签字体大小
     */
    private int mLabelTextSize = 24; // 12sp
    
    /**
     * 刻度标签字体颜色
     */
    private int mLabelTextColor = Color.LTGRAY;
    
    private Paint mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private int mWidth = 0;
    private int mHeight = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;
    
    private boolean isBeingDragged = false;
    private float mTouchSlop = 0;
    private VelocityTracker mVelocityTracker;
    
    private int mScaleOffsetMaximum = 0;
    private int mScaleOffsetMinimum = 0;
    private int mScaleOffsetX = 0;
    
    private SettlingRunnable mSettlingRunnable;
    
    private boolean mNeedsReset = false;
    
    private Handler mHandler;
    private OnScaleChangedListener mScaleChangedListener;
    
    public HorizonScaleBar(Context context) {
        super(context);
        this.initWithAttrs(context, null, 0);
    }
    
    public HorizonScaleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initWithAttrs(context, attrs, 0);
    }
    
    public HorizonScaleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initWithAttrs(context, attrs, defStyleAttr);
    }
    
    private void initWithAttrs(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.initialize();
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizonScaleBar, defStyleAttr, 0);
    
            /* Value */
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_min_value)) {
                final int min = a.getInt(R.styleable.HorizonScaleBar_hsb_min_value, 0);
                if (min >= 0) {
                    setValueMinimum(min);
                }
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_max_value)) {
                final int max = a.getInt(R.styleable.HorizonScaleBar_hsb_max_value, 0);
                if (max >= 0) {
                    setValueMaximum(max);
                }
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_value_stride)) {
                final int stride = a.getInt(R.styleable.HorizonScaleBar_hsb_value_stride, mValueStride);
                if (stride > 0) {
                    setValueStride(stride);
                }
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_second_stride)) {
                final int stride = a.getInt(R.styleable.HorizonScaleBar_hsb_second_stride, mSecondStride);
                if (stride > 0) {
                    setSecondStride(stride);
                }
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_selected_value)) {
                final int value = a.getInt(R.styleable.HorizonScaleBar_hsb_selected_value, mSelectedValue);
                if (isValidValue(value)) {
                    mSelectedValue = value;
                }
            }
    
            /* BaseLine */
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_baseLineEnable)) {
                mBaseLineEnable = a.getBoolean(R.styleable.HorizonScaleBar_hsb_baseLineEnable, mBaseLineEnable);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_baseLineColor)) {
                mBaseLineColor = a.getColor(R.styleable.HorizonScaleBar_hsb_baseLineColor, mBaseLineColor);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_baseLineHeight)) {
                mBaseLineHeight = a.getDimensionPixelSize(R.styleable.HorizonScaleBar_hsb_baseLineHeight, mBaseLineHeight);
            }
    
            /* Scale */
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_scale_color)) {
                mScaleColor = a.getColor(R.styleable.HorizonScaleBar_hsb_scale_color, mScaleColor);
                mSecondScaleColor = mScaleColor;
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_scale_height)) {
                mScaleHeight = a.getDimensionPixelOffset(R.styleable.HorizonScaleBar_hsb_scale_height, mScaleHeight);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_scale_width)) {
                mScaleWidth = a.getDimensionPixelOffset(R.styleable.HorizonScaleBar_hsb_scale_width, mScaleWidth);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_scale_spacing)) {
                mScaleSpacing = a.getDimensionPixelOffset(R.styleable.HorizonScaleBar_hsb_scale_spacing, mScaleSpacing);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_second_scale_color)) {
                mSecondScaleColor = a.getColor(R.styleable.HorizonScaleBar_hsb_second_scale_color, mSecondScaleColor);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_second_scale_height)) {
                mSecondScaleHeight = a.getDimensionPixelOffset(R.styleable.HorizonScaleBar_hsb_second_scale_height, mSecondScaleHeight);
            }
    
            /* second label */
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_second_label_size)) {
                mLabelTextSize = a.getDimensionPixelSize(R.styleable.HorizonScaleBar_hsb_second_label_size, mLabelTextSize);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_second_label_color)) {
                mLabelTextColor = a.getColor(R.styleable.HorizonScaleBar_hsb_second_label_color, mLabelTextColor);
            }
    
            /* Indicator */
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_indicator_color)) {
                mIndicatorColor = a.getColor(R.styleable.HorizonScaleBar_hsb_indicator_color, mIndicatorColor);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_indicator_height)) {
                mIndicatorHeight = a.getDimensionPixelOffset(R.styleable.HorizonScaleBar_hsb_indicator_height, mIndicatorHeight);
            }
    
            if (a.hasValue(R.styleable.HorizonScaleBar_hsb_indicator_width)) {
                mIndicatorWidth = a.getDimensionPixelOffset(R.styleable.HorizonScaleBar_hsb_indicator_width, mIndicatorWidth);
            }
    
            setNeedsReset(true);
    
            a.recycle();
        }
    }
    
    private void initialize() {
        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        mTouchSlop = vc.getScaledTouchSlop();
        
        mHandler = new Handler();
        requestLayout();
        invalidate();
    }
    
    private void resetIfNeeds() {
        if (mNeedsReset) {
            mNeedsReset = false;
            
            mScalePaint.setColor(mScaleColor);
            mScalePaint.setStrokeWidth(mScaleWidth);
            
            mIndicatorPaint.setColor(mIndicatorColor);
            mIndicatorPaint.setStrokeWidth(mIndicatorWidth);
            
            mLabelPaint.setColor(mLabelTextColor);
            mLabelPaint.setTextSize(mLabelTextSize);
            mLabelPaint.setTextAlign(Paint.Align.CENTER);
            
            mScaleOffsetX = offsetXForValue(mSelectedValue);
            mScaleOffsetMinimum = -(getValueCount() - 1) * mScaleSpacing;
        }
    }
    
    /// MARK: Getter
    
    private int getValueCount() {
        int range = mValueMaximum - mValueMinimum;
        if (range > 0) {
            return range / mValueStride + 1;
        }
        return 0;
    }
    
    private boolean isValidValue(int value) {
        if (value >= mValueMinimum && value <= mValueMaximum) {
            return value % mValueStride == 0;
        }
        return false;
    }
    
    private int offsetXForValue(int value) {
        return -(value / mValueStride) * mScaleSpacing;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        resetIfNeeds();
        
        this.drawBaseLine(canvas);
        this.drawScales(canvas);
        this.drawIndicator(canvas);
    }
    
    private void drawBaseLine(Canvas canvas) {
        if (mBaseLineEnable) {
            mScalePaint.setStrokeWidth(mBaseLineHeight);
            mScalePaint.setColor(mBaseLineColor);
            final int startX = Math.max(0, mScaleOffsetX + mCenterX);
            final int startY = mHeight - getPaddingBottom();
            final int endX = mCenterX + (mValueMaximum - mSelectedValue) * mScaleSpacing;
            canvas.drawLine(startX, startY, endX, startY, mScalePaint);
        }
    }
    
    private void drawScales(Canvas canvas) {
        final int scaleCount = getValueCount();
        final int startY = mHeight - getPaddingBottom();
        
        mScalePaint.setStrokeWidth(mScaleWidth);
        
        for (int i = 0; i < scaleCount; i++) {
            final int scaleX = mScaleOffsetX + mCenterX + mScaleSpacing * i;
            
            if (i % mSecondStride == 0) {
                // 绘制第二刻度
                int scaleEndY = startY - mSecondScaleHeight;
                mScalePaint.setColor(mSecondScaleColor);
                canvas.drawLine(scaleX, startY, scaleX, scaleEndY, mScalePaint);
                
                final String label = Integer.toString(i);
                float yCenter = scaleEndY;
                yCenter -= (mLabelPaint.descent() - mLabelPaint.ascent()) / 2;
                canvas.drawText(label, (float) scaleX, yCenter, mLabelPaint);
            } else {
                // draw scale
                int scaleEndY = startY - mScaleHeight;
                mScalePaint.setColor(mScaleColor);
                canvas.drawLine(scaleX, startY, scaleX, scaleEndY, mScalePaint);
            }
        }
    }
    
    private void drawIndicator(Canvas canvas) {
        final int startY = mHeight - getPaddingBottom();
        canvas.drawLine(mCenterX, startY, mCenterX, startY - mIndicatorHeight, mIndicatorPaint);
    }
    
    /// MARK: Value
    
    private void computeSelectedScale() {
        final float offsetAbs = Math.abs(mScaleOffsetX);
        float index = offsetAbs / mScaleSpacing;
        final int oldSelectedValue = mSelectedValue;
        mSelectedValue = (int) index;
        if (mSelectedValue != oldSelectedValue) {
            mHandler.post(() -> {
                if (mScaleChangedListener != null) {
                    mScaleChangedListener.onScaleValueChanged(mSelectedValue);
                }
            });
        }
    }
    
    private void animateToValue(int value) {
        if (mSelectedValue == value) {
            return;
        }
        mSelectedValue = value;
        final int endOffset = -mSelectedValue / mValueStride * mScaleSpacing;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mScaleOffsetX, endOffset);
        valueAnimator.setDuration(250);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScaleOffsetX = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
    
    /// MARK: Touch Event
    
    private float mLastTouchX = 0;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelSettlingIfNeeds();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mLastTouchX = event.getX();
                break;
            
            case MotionEvent.ACTION_MOVE:
                final float touchX = event.getX();
                final float offset = touchX - mLastTouchX;
                mLastTouchX = event.getX();
                mVelocityTracker.addMovement(event);
                
                if (!isBeingDragged) {
                    startDragging(offset);
                }
                if (isBeingDragged) {
                    this.onDragged(offset, true);
                }
                break;
            
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                boolean settling = false;
                if (mVelocityTracker != null) {
                    mVelocityTracker.computeCurrentVelocity(UNITS_ONE_SECOND);
                    final float vx = mVelocityTracker.getXVelocity();
                    settling = true;
                    onSettling(vx);
                }
                if (!settling) {
                    this.onDragStop(true);
                }
                break;
        }
        
        return true;
    }
    
    private void startDragging(float dx) {
        if (isBeingDragged) {
            return;
        }
        if (Math.abs(dx) > mTouchSlop) {
            isBeingDragged = true;
        }
    }
    
    private void onDragged(float offset, boolean isSync) {
        mScaleOffsetX += offset;
        if (mScaleOffsetX > mScaleOffsetMaximum) {
            mScaleOffsetX = mScaleOffsetMaximum;
            cancelSettlingIfNeeds();
        }
        if (mScaleOffsetX < mScaleOffsetMinimum) {
            mScaleOffsetX = mScaleOffsetMinimum;
            cancelSettlingIfNeeds();
        }
        
        if (isSync) {
            this.invalidate();
        } else {
            this.postInvalidate();
        }
        
        computeSelectedScale();
    }
    
    private void onDragStop(boolean isSync) {
        final int diff = Math.abs(mScaleOffsetX % mScaleSpacing);
        if (diff > mScaleSpacing / 2) {
            mScaleOffsetX -= (mScaleSpacing - diff);
        } else {
            mScaleOffsetX += diff;
        }
        if (isSync) {
            invalidate();
        } else {
            postInvalidate();
        }
        computeSelectedScale();
    }
    
    private void onSettling(final float vx) {
        cancelSettlingIfNeeds();
        mSettlingRunnable = new SettlingRunnable(vx);
        new Thread(mSettlingRunnable).start();
    }
    
    private void cancelSettlingIfNeeds() {
        if (mSettlingRunnable != null && mSettlingRunnable.isSettling()) {
            mSettlingRunnable.cancel();
        }
    }
    
    private class SettlingRunnable implements Runnable {
        
        private final int factor = 1000000;
        
        private final float step = 50;
        
        private boolean mSettling;
        
        private float velocity;
        
        SettlingRunnable(float velocity) {
            this.velocity = velocity;
            mSettling = true;
        }
        
        boolean isSettling() {
            return mSettling;
        }
        
        void cancel() {
            if (mSettling) {
                mSettling = false;
            }
        }
        
        @Override
        public void run() {
            if (velocity > 0 && mSettling) {
                velocity -= step;
                onDragged(velocity * velocity / factor, false);
            } else if (velocity < 0 && mSettling) {
                velocity += step;
                onDragged(-velocity * velocity / factor, false);
            }
            if (mSettling && Math.abs(velocity) > step / 2) {
                post(this);
            } else {
                mSettling = false;
                onDragStop(false);
            }
        }
    }
    
    
    public interface OnScaleChangedListener {
        void onScaleValueChanged(int scaleValue);
    }
    
    
    /// MARK: Getter && Setter
    
    
    public boolean isNeedsReset() {
        return mNeedsReset;
    }
    
    public void setNeedsReset(boolean needsReset) {
        mNeedsReset = needsReset;
    }
    
    public int getValueMinimum() {
        return mValueMinimum;
    }
    
    public void setValueMinimum(int valueMinimum) {
        mValueMinimum = valueMinimum;
        setNeedsReset(true);
    }
    
    public int getValueMaximum() {
        return mValueMaximum;
    }
    
    public void setValueMaximum(int valueMaximum) {
        mValueMaximum = valueMaximum;
        setNeedsReset(true);
    }
    
    public int getValueStride() {
        return mValueStride;
    }
    
    public void setValueStride(int valueStride) {
        mValueStride = valueStride;
        setNeedsReset(true);
    }
    
    public int getSecondStride() {
        return mSecondStride;
    }
    
    public void setSecondStride(int secondStride) {
        mSecondStride = secondStride;
    }
    
    public int getSelectedValue() {
        return mSelectedValue;
    }
    
    public void setSelectedValue(int selectedValue) {
        if (isValidValue(selectedValue)) {
            // mSelectedValue = selectedValue;
            animateToValue(selectedValue);
        }
    }
    
    public boolean isBaseLineEnable() {
        return mBaseLineEnable;
    }
    
    public void setBaseLineEnable(boolean baseLineEnable) {
        mBaseLineEnable = baseLineEnable;
    }
    
    public int getBaseLineColor() {
        return mBaseLineColor;
    }
    
    public void setBaseLineColor(int baseLineColor) {
        mBaseLineColor = baseLineColor;
    }
    
    public int getBaseLineHeight() {
        return mBaseLineHeight;
    }
    
    public void setBaseLineHeight(int baseLineHeight) {
        mBaseLineHeight = baseLineHeight;
    }
    
    public int getScaleSpacing() {
        return mScaleSpacing;
    }
    
    public void setScaleSpacing(int scaleSpacing) {
        mScaleSpacing = scaleSpacing;
        setNeedsReset(true);
    }
    
    public int getScaleWidth() {
        return mScaleWidth;
    }
    
    public void setScaleWidth(int scaleWidth) {
        mScaleWidth = scaleWidth;
    }
    
    public int getScaleHeight() {
        return mScaleHeight;
    }
    
    public void setScaleHeight(int scaleHeight) {
        mScaleHeight = scaleHeight;
    }
    
    public int getScaleColor() {
        return mScaleColor;
    }
    
    public void setScaleColor(int scaleColor) {
        mScaleColor = scaleColor;
    }
    
    public int getSecondScaleHeight() {
        return mSecondScaleHeight;
    }
    
    public void setSecondScaleHeight(int secondScaleHeight) {
        mSecondScaleHeight = secondScaleHeight;
    }
    
    public int getSecondScaleColor() {
        return mSecondScaleColor;
    }
    
    public void setSecondScaleColor(int secondScaleColor) {
        mSecondScaleColor = secondScaleColor;
    }
    
    public int getIndicatorColor() {
        return mIndicatorColor;
    }
    
    public void setIndicatorColor(int indicatorColor) {
        mIndicatorColor = indicatorColor;
    }
    
    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }
    
    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = indicatorHeight;
    }
    
    public int getIndicatorWidth() {
        return mIndicatorWidth;
    }
    
    public void setIndicatorWidth(int indicatorWidth) {
        mIndicatorWidth = indicatorWidth;
    }
    
    public int getLabelTextSize() {
        return mLabelTextSize;
    }
    
    public void setLabelTextSize(int labelTextSize) {
        mLabelTextSize = labelTextSize;
    }
    
    public int getLabelTextColor() {
        return mLabelTextColor;
    }
    
    public void setLabelTextColor(int labelTextColor) {
        mLabelTextColor = labelTextColor;
    }
    
    public void setOnScaleChangedListener(OnScaleChangedListener scaleChangedListener) {
        mScaleChangedListener = scaleChangedListener;
    }
}