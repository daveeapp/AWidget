package com.davee.awidgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * CircleSlider 圆形进度选择器
 * <p>
 * Created by davee 2018/9/11.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class CircleSlider extends View {
    
    /**
     * 默认轨道宽度
     */
    private static final int DEFAULT_TRACK_WIDTH = 8;
    /**
     * 默认轨道颜色
     */
    private static final int DEFAULT_TRACK_COLOR = Color.DKGRAY;
    /**
     * 默认进度条颜色
     */
    private static final int DEFAULT_PROGRESS_COLOR = 0xffff4081;
    /**
     * 默认滑块颜色
     */
    private static final int DEFAULT_THUMB_COLOR = 0xffff4081;
    /**
     * 默认滑块大小
     */
    private static final int DEFAULT_THUMB_RADIUS = 6;
    /**
     * 默认刻度线宽
     */
    private static final int DEFAULT_SCALE_WIDTH = 2;
    /**
     * 默认刻度线长
     */
    private static final int DEFAULT_SCALE_LENGTH = 8;
    /**
     * 默认刻度线短
     */
    private static final int DEFAULT_SCALE_LENGTH_SHORT = 4;
    /**
     * 默认刻度线颜色
     */
    private static final int DEFAULT_SCALE_COLOR = Color.DKGRAY;
    /**
     * 默认刻度文字大小
     */
    private static final int DEFAULT_SCALE_TEXT_SIZE = 13;
    /**
     * 默认刻度文字间距
     */
    private static final int DEFAULT_SCALE_TEXT_SPACING = 16;
    
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 24;
    private static final int DEFAULT_VALUE_STRIDE = 1;
    private static final int DEFAULT_LABEL_STRIDE = 2;
    
    /**
     * 默认渐变：起始颜色
     */
    private static final int START_COLOR = 0xff2cd565;
    /**
     * 默认渐变：结束颜色
     */
    private static final int END_COLOR = 0xffff0618;
    
    /**
     * 默认绘制起始角度
     */
    private static final float START_ANGLE = -90;
    
    /**
     * 滑块标记
     */
    public static final int FLAG_THUMB = 0;
    /**
     * 结束滑块标记
     */
    public static final int FLAG_THUMB_END = 1;
    
    
    
    /**
     * 只有一个滑块可以拖动
     */
    public static final int SINGLE_STYLE = 0;
    /**
     * 两个点可以拖动，选择一个范围
     */
    public static final int PERIOD_STYLE = 1;
    /**
     * 两个点可以拖动，选择一个范围，且endValue必须大于startValue
     */
    public static final int PERIOD_CLOSE_STYLE = 2;
    
    /**
     * 选择器样式
     */
    @IntDef(value = {SINGLE_STYLE, PERIOD_STYLE, PERIOD_CLOSE_STYLE})
    @Retention(SOURCE)
    public @interface Style {
    }
    
    /**
     * 默认样式
     */
    @Style
    private int mStyle = SINGLE_STYLE;
    
    /**
     * 轨道宽
     */
    private int mTrackWidth;
    /**
     * 轨道颜色
     */
    private int mTrackColor = DEFAULT_TRACK_COLOR;
    /**
     * 进度条颜色
     */
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;
    private Paint mTrackPaint;
    private Paint mProgressPaint;
    
    /**
     * 当前拖动滑块的标记
     */
    private int mThumbFlag = FLAG_THUMB;
    /**
     * 滑块大小
     */
    private int mThumbRadius;
    /**
     * 滑块颜色
     */
    private int mThumbColor = DEFAULT_THUMB_COLOR;
    private Paint mThumbPaint;
    
    // private boolean mEndThumbEnable = false;
    /**
     * 末端滑块的颜色
     */
    private int mEndThumbColor = DEFAULT_THUMB_COLOR;
    private Paint mEndThumbPaint;
    
    /**
     * 刻度线宽
     */
    private int mScaleWidth = DEFAULT_SCALE_WIDTH;
    /**
     * 长刻度线长度
     */
    private int mScaleLength = DEFAULT_SCALE_LENGTH;
    /**
     * 短刻度线长度
     */
    private int mScaleLengthShort = DEFAULT_SCALE_LENGTH_SHORT;
    /**
     * 刻度颜色
     */
    private int mScaleColor = DEFAULT_SCALE_COLOR;
    /**
     * 刻度文字大小
     */
    private int mScaleTextSize;
    /**
     * 文字与刻度间距
     */
    private int mScaleTextSpacing;
    private Paint mScalePaint;
    private Paint mLabelPaint;
    
    /**
     * 最小值
     */
    private int mMinValue = DEFAULT_MIN_VALUE;
    /**
     * 最大值
     */
    private int mMaxValue = DEFAULT_MAX_VALUE;
    /**
     * 可选值的递增幅度
     */
    private int mValueStride = DEFAULT_VALUE_STRIDE;
    /**
     * 绘制刻度文字的幅度
     */
    private int mLabelStride = DEFAULT_LABEL_STRIDE;
    /**
     * 当前选择值
     */
    private int mSelectedValue = 0;
    /**
     * 末端选择值
     */
    private int mEndSelectedValue = 0;
    
    private CircleF mOutCircle = new CircleF(0, 0, 1);
    private RectF mOutRect = new RectF(0, 0, 0, 0);
    
    private float mThumbDegree = 0;
    private float mEndThumbDegree = 90;
    
    /**
     * 是否开启渐变颜色
     */
    private boolean mGradientEnable = true;
    
    /**
     * 是否正在拖动
     */
    private boolean mIsBeingDragged = false;
    
    /**
     * 监听器
     */
    public interface OnValueChangedListener {
        void onValueChanged(CircleSlider circleSlider, int whichThumb);
    }
    
    /**
     * 选择值监听
     */
    private OnValueChangedListener mOnValueChangedListener;
    
    public CircleSlider(Context context) {
        super(context);
        initialize();
    }
    
    public CircleSlider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    
    public CircleSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    
    private void initialize() {
        // 设置最小宽度和高度
        setMinimumWidth(Dimens.dp2px(getContext(), 100));
        setMinimumHeight(Dimens.dp2px(getContext(), 100));
        
        mTrackWidth = Dimens.dp2px(getContext(), DEFAULT_TRACK_WIDTH);
        mThumbRadius = Dimens.dp2px(getContext(), DEFAULT_THUMB_RADIUS);
        mScaleWidth = Dimens.dp2px(getContext(), DEFAULT_SCALE_WIDTH);
        mScaleLength = Dimens.dp2px(getContext(), DEFAULT_SCALE_LENGTH);
        mScaleLengthShort = Dimens.dp2px(getContext(), DEFAULT_SCALE_LENGTH_SHORT);
        mScaleTextSize = Dimens.sp2px(getContext(), DEFAULT_SCALE_TEXT_SIZE);
        mScaleTextSpacing = Dimens.dp2px(getContext(), DEFAULT_SCALE_TEXT_SPACING);
        
        mTrackPaint = new Paint();
        mTrackPaint.setAntiAlias(true);
        mTrackPaint.setColor(mTrackColor);
        mTrackPaint.setStyle(Paint.Style.STROKE);
        
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        
        mThumbPaint = new Paint();
        mThumbPaint.setAntiAlias(true);
        mThumbPaint.setColor(mThumbColor);
        
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setColor(mScaleColor);
        
        mLabelPaint = new Paint();
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setColor(mScaleColor);
        mLabelPaint.setTextSize(mScaleTextSize);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
    }
    
    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        mOnValueChangedListener = onValueChangedListener;
    }
    
    public void setStyle(@Style int style) {
        if (mStyle != style) {
            mStyle = style;
        }
    }
    
    public void setSelectedValue(int value) {
        if (isValueInRange(value)) {
            mSelectedValue = value;
            final int index = indexForValue(mSelectedValue);
            final float degree = degreeForIndex(index);
            setThumbDegree(degree);
        }
    }
    
    // public void setEndThumbEnable(boolean endThumbEnable) {
    //     mEndThumbEnable = endThumbEnable;
    // }
    
    public int getSelectedValue() {
        return mSelectedValue;
    }
    
    public int getEndSelectedValue() {
        return mEndSelectedValue;
    }
    
    public void setEndSelectedValue(int endSelectedValue) {
        if (isValueInRange(endSelectedValue)) {
            mEndSelectedValue = endSelectedValue;
            final int index = indexForValue(mEndSelectedValue);
            final float degree = degreeForIndex(index);
            setEndThumbDegree(degree);
        }
    }
    
    public void setGradientEnable(boolean gradientEnable) {
        if (mGradientEnable != gradientEnable) {
            mGradientEnable = gradientEnable;
            invalidate();
        }
    }
    
    // =====================================
    // MARK - Private Getter / Setter
    // =====================================
    
    private boolean isValueInRange(final int value) {
        return value >= mMinValue && value <= mMaxValue;
    }
    
    private boolean isSingleStyle() {
        return mStyle == SINGLE_STYLE;
    }
    
    private boolean isPeriodStyle() {
        return mStyle == PERIOD_STYLE || mStyle == PERIOD_CLOSE_STYLE;
    }
    
    private boolean isPeriodCloseStyle() {
        return mStyle == PERIOD_CLOSE_STYLE;
    }
    
    /// e.g 0~100,实际value个数应为101个，但是第一个和最后一个在同一个点，所以刻度是100个
    private int getScaleCount() {
        return (mMaxValue - mMinValue) / mValueStride;
    }
    
    private int getValueCount() {
        return getScaleCount() + 1;
    }
    
    private int valueForIndex(int index) {
        return mMinValue + index * mValueStride;
    }
    
    private int indexForValue(int value) {
        return (value - mMinValue) / mValueStride;
    }
    
    private float degreeForEach() {
        return 360.0f / getScaleCount();
    }
    
    /// 设定坐标系（0度在圆上顶端）
    private float degreeForIndex(int index) {
        return index * degreeForEach();
    }
    
    /// 设定坐标系（0度在圆上顶端）
    private void setThumbDegree(float degree) {
        if (isPeriodStyle()) {
            // Start degree can not be bigger than end degree
            final float target = readjustDegrees(degree);
            if (isPeriodCloseStyle()) {
                if (target <= mEndThumbDegree) {
                    mThumbDegree = target;
                    invalidate();
                }
            } else {
                mThumbDegree = target;
                invalidate();
            }
            
        } else {
            mThumbDegree = readjustDegrees(degree);
            invalidate();
        }
    }
    
    private void setEndThumbDegree(float endThumbDegree) {
        final float target = readjustDegrees(endThumbDegree);
        if (isPeriodCloseStyle()) {
            if (target >= mThumbDegree) {
                mEndThumbDegree = target;
                invalidate();
            }
        } else {
            mEndThumbDegree = target;
            invalidate();
        }
    }
    
    private void setSelectedValueInternal(int value) {
        if (mSelectedValue != value) {
            mSelectedValue = value;
            notifyValueChanged(FLAG_THUMB);
        }
    }
    
    private void setEndSelectedValueInternal(int value) {
        if (mEndSelectedValue != value) {
            mEndSelectedValue = value;
            notifyValueChanged(FLAG_THUMB_END);
        }
    }
    
    private void notifyValueChanged(int flag) {
        if (mOnValueChangedListener != null) {
            mOnValueChangedListener.onValueChanged(this, flag);
        }
    }
    
    private Paint getEndThumbPaint() {
        if (mEndThumbPaint == null) {
            mEndThumbPaint = new Paint();
            mEndThumbPaint.setAntiAlias(true);
            mEndThumbPaint.setColor(mEndThumbColor);
        }
        return mEndThumbPaint;
    }
    
    private SweepGradient getGradient(float startDegree, float sweepAngle) {
        final float cx = getWidth() / 2;
        final float cy = getHeight() / 2;
        final float start = startDegree / 360;
        final float end = sweepAngle / 360;
        SweepGradient sweepGradient = new SweepGradient(cx, cy,
                new int[]{START_COLOR, END_COLOR},
                new float[]{0, end});
        Matrix matrix = new Matrix();
        sweepGradient.getLocalMatrix(matrix);
        matrix.setRotate(startDegree, mOutCircle.centerX, mOutCircle.centerY);
        sweepGradient.setLocalMatrix(matrix);
        return sweepGradient;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int shouldWH = Math.min(width, height);
        setMeasuredDimension(shouldWH, shouldWH);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
        mOutCircle.centerX = getWidth() / 2;
        mOutCircle.centerY = getHeight() / 2;
        mOutCircle.radius = getWidth() / 2 - mThumbRadius;
        
        mOutRect.left = getPaddingLeft() + mThumbRadius;
        mOutRect.top = getPaddingTop() + mThumbRadius;
        mOutRect.right = getWidth() - getPaddingRight() - mThumbRadius;
        mOutRect.bottom = getHeight() - getPaddingBottom() - mThumbRadius;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw Track
        mTrackPaint.setStrokeWidth(mTrackWidth);
        canvas.drawCircle(mOutCircle.centerX, mOutCircle.centerY, mOutCircle.radius, mTrackPaint);
        
        if (isSingleStyle()) {
            drawThumb(canvas);
        } else {
            drawThumbs(canvas);
        }
        drawScales(canvas);
    }
    
    private void drawScales(Canvas canvas) {
        final int valueCount = getScaleCount();
        final float scaleRadiusStart = mOutCircle.radius - mTrackWidth / 2;
        final float scaleTextRadius = mOutCircle.radius - mTrackWidth / 2 - mScaleLength - mScaleTextSpacing;
        mScalePaint.setStrokeWidth(mScaleWidth);
        for (int i = 0; i < valueCount; i++) {
            final int value = valueForIndex(i);
            boolean needsDrawLabel = false;
            float scaleRadiusEnd = scaleRadiusStart - mScaleLengthShort;
            if (value % mLabelStride == 0) {
                scaleRadiusEnd = scaleRadiusStart - mScaleLength;
                needsDrawLabel = true;
            }
            /// 默认坐标系（0度在圆上最右侧）
            final float degree = degreeForIndex(i) + START_ANGLE;
            final float startX = mOutCircle.xWithAngle(degree, scaleRadiusStart);
            final float startY = mOutCircle.yWithAngle(degree, scaleRadiusStart);
            final float endX = mOutCircle.xWithAngle(degree, scaleRadiusEnd);
            final float endY = mOutCircle.yWithAngle(degree, scaleRadiusEnd);
            canvas.drawLine(startX, startY, endX, endY, mScalePaint);
            if (needsDrawLabel) {
                drawScaleLabel(value, degree, scaleTextRadius, canvas);
            }
        }
    }
    
    private void drawScaleLabel(int value, float degree, float scaleTextRadius, Canvas canvas) {
        final Paint paint = mLabelPaint;
        final float posX = mOutCircle.xWithAngle(degree, scaleTextRadius);
        float posY = mOutCircle.yWithAngle(degree, scaleTextRadius);
        posY -= (paint.ascent() + paint.descent()) / 2;
        canvas.drawText(Integer.toString(value), posX, posY, paint);
    }
    
    private void drawThumb(Canvas canvas) {
        // Draw Progress
        mProgressPaint.setStrokeWidth(mTrackWidth);
        final float sweepAngle = mThumbDegree;
        if (mGradientEnable) {
            mProgressPaint.setShader(getGradient(START_ANGLE, sweepAngle));
        } else {
            mProgressPaint.setShader(null);
        }
        canvas.drawArc(mOutRect, START_ANGLE, sweepAngle, false, mProgressPaint);
        
        // Draw Thumb
        final float thumbDegreeToDefault = mThumbDegree + START_ANGLE;
        final float posX = mOutCircle.xWithAngle(thumbDegreeToDefault);
        final float posY = mOutCircle.yWithAngle(thumbDegreeToDefault);
        canvas.drawCircle(posX, posY, mThumbRadius, mThumbPaint);
    }
    
    private void drawThumbs(final Canvas canvas) {
        // Draw Progress
        mProgressPaint.setStrokeWidth(mTrackWidth);
        final float startDegree = mThumbDegree + START_ANGLE;
        float sweepAngle = mEndThumbDegree - mThumbDegree;
        if (sweepAngle < 0) {
            sweepAngle += 360;
        }
        if (mGradientEnable) {
            mProgressPaint.setShader(getGradient(startDegree, sweepAngle));
        } else {
            mProgressPaint.setShader(null);
        }
        canvas.drawArc(mOutRect, startDegree, sweepAngle, false, mProgressPaint);
        
        // Draw Start Thumb
        {
            if (mGradientEnable) {
                mThumbPaint.setColor(START_COLOR);
            } else {
                mThumbPaint.setColor(mEndThumbColor);
            }
            final float posX = mOutCircle.xWithAngle(mThumbDegree - 90);
            final float posY = mOutCircle.yWithAngle(mThumbDegree - 90);
            canvas.drawCircle(posX, posY, mThumbRadius, mThumbPaint);
        }
        
        // Draw End Thumb
        {
            final Paint paint = getEndThumbPaint();
            if (mGradientEnable) {
                paint.setColor(END_COLOR);
            } else {
                paint.setColor(mEndThumbColor);
            }
            final float posX = mOutCircle.xWithAngle(mEndThumbDegree - 90);
            final float posY = mOutCircle.yWithAngle(mEndThumbDegree - 90);
            canvas.drawCircle(posX, posY, mThumbRadius, paint);
        }
    }
    
    /// MARK: Touch Events
    
    private void startDragging(final float x, final float y) {
        if (mIsBeingDragged) {
            return;
        }
        final float distance = (float) mOutCircle.distanceToCenter(x, y);
        final float maxRadius = getWidth() / 2;
        final float minRadius = mOutCircle.radius - mThumbRadius - mScaleLength - mScaleTextSpacing;
        if (distance >= minRadius && distance <= maxRadius) {
            mIsBeingDragged = true;
            if (isPeriodStyle()) {
                checkThumbFlag(new Point((int) x, (int) y));
            }
        }
    }
    
    private void checkThumbFlag(final Point point) {
        final Point mThumbPos = mOutCircle.pointWithAngle(mThumbDegree - 90);
        final Point mEndThumbPos = mOutCircle.pointWithAngle(mEndThumbDegree - 90);
        final float distanceStart = distanceOfTwoPoints(mThumbPos, point);
        final float distanceEnd = distanceOfTwoPoints(mEndThumbPos, point);
        // BugFixed: 终点优先级高
        if (distanceStart >= distanceEnd) {
            mThumbFlag = FLAG_THUMB_END;
        } else {
            mThumbFlag = FLAG_THUMB;
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleDragging(event);
                break;
            
            case MotionEvent.ACTION_MOVE:
                handleDragging(event);
                break;
            
            case MotionEvent.ACTION_UP:
                onFinishDragging();
                break;
            
            case MotionEvent.ACTION_CANCEL:
                onFinishDragging();
                break;
        }
        
        return mIsBeingDragged;
    }
    
    private void handleDragging(MotionEvent event) {
        startDragging(event.getX(), event.getY());
        if (mIsBeingDragged) {
            final float degree = degreesFromXY(event.getX(), event.getY());
            if (mThumbFlag == FLAG_THUMB) {
                setThumbDegree(degree + 90);
                checkSelectedValue();
            } else {
                setEndThumbDegree(degree + 90);
                checkEndSelectedValue();
            }
        }
    }
    
    private void onFinishDragging() {
        final float degreeForEach = degreeForEach();
        if (mThumbFlag == FLAG_THUMB) {
            final float modulus = mThumbDegree % degreeForEach;
            final float absModulus = Math.abs(modulus);
            float targetDegree = mThumbDegree;
            if (absModulus > degreeForEach / 2) {
                if (modulus < 0) {
                    targetDegree -= degreeForEach - absModulus;
                } else {
                    targetDegree += degreeForEach - absModulus;
                }
            } else {
                targetDegree -= modulus;
            }
            setThumbDegree(targetDegree);
            checkSelectedValue();
        } else {
            final float modulus = mEndThumbDegree % degreeForEach;
            final float absModulus = Math.abs(modulus);
            float targetDegree = mEndThumbDegree;
            if (absModulus > degreeForEach / 2) {
                if (modulus < 0) {
                    targetDegree -= degreeForEach - absModulus;
                } else {
                    targetDegree += degreeForEach - absModulus;
                }
            } else {
                targetDegree -= modulus;
            }
            setEndThumbDegree(targetDegree);
            checkEndSelectedValue();
        }
        
        mIsBeingDragged = false;
    }
    
    private void checkSelectedValue() {
        final float activatedIndex = mThumbDegree / degreeForEach();
        // 这里需要用Value Count
        final int selectedIndex = Math.round(activatedIndex) % getValueCount();
        int selectedValue = mMinValue + selectedIndex * mValueStride;
        setSelectedValueInternal(selectedValue);
    }
    
    private void checkEndSelectedValue() {
        final float activatedIndex = mEndThumbDegree / degreeForEach();
        // 这里需要用Value Count
        final int selectedIndex = Math.round(activatedIndex) % getValueCount();
        int selectedValue = mMinValue + selectedIndex * mValueStride;
        setEndSelectedValueInternal(selectedValue);
    }
    
    private float degreesFromXY(float x1, float y1) {
        // 计算向量(x1-cx, y1 - cy)与横轴的夹角(-PI ~ PI)
        return (float) Math.toDegrees(Math.atan2(y1 - mOutCircle.centerY, x1 - mOutCircle.centerX));
    }
    
    private float distanceOfTwoPoints(Point p1, Point p2) {
        return (float) Math.sqrt((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x));
    }
    
    /// 0 ~ 360
    private float readjustDegrees(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        return angle;
    }
}
