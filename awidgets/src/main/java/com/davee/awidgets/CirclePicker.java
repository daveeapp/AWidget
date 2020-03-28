package com.davee.awidgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * CirclePicker 圆形选择器
 * <p>
 * Created by davee 2018/9/4.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class CirclePicker extends View {
    
    /**
     * 选择器值改变监听器
     */
    public interface OnValueChangedListener {
        void onValueChanged(CirclePicker circlePicker);
    }
    
    /**
     * 绘制起始角度（0度为right）
     */
    private static final float START_ANGLE = -90;
    
    // {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"}
    /**
     * 选择器选项内容
     */
    private String[] options;
    /**
     * 文字增幅：标记哪些位置需要绘制文字
     */
    private int mLabelIncrement = 1;
    /**
     * 当前选中位置
     */
    private int mSelectedIndex;
    /**
     * 间隔角度
     */
    private float mSweepAngle;
    
    /**
     * 文字大小
     */
    private float mTextSize = Dimens.sp2px(getContext(), 13);
    /**
     * 文字颜色：正常状态
     */
    private int mTextColorNormal = Color.DKGRAY;
    /**
     * 文字颜色：高亮状态
     */
    private int mTextColorHighlight = Color.WHITE;
    /**
     * 刻度圆点大小
     */
    private float mScaleDotRadius;
    private Typeface mTypeface;
    
    private Paint mTextPaint = new Paint();
    private Paint mThumbPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();
    
    /// Out Circle 选择器外圈
    private float mXCenter = 0;
    private float mYCenter = 0;
    private float mCircleRadius;
    private CircleF mTextCircle = new CircleF(0, 0, 1);
    
    /// Selector Thumb 选择器滑动块
    private CircleF mThumbCircle = new CircleF(0, 0, 1);
    private float mThumbRadius;
    private float mThumbDotRadius;
    private float mThumbStrokeWidth;
    private float mThumbDegrees;
    
    public boolean drawThumbLineEnable = true;
    
    /**
     * 标记是否正在拖到
     */
    private boolean isBeingDragged = false;
    
    private OnValueChangedListener mOnValueChangedListener;
    
    /// MARK: Description Labels 描述文字，绘制在圆内
    
    private Paint mTopPaint;
    private Paint mBottomPaint;
    
    /**
     * 绘制在圆内顶部的文字
     */
    public CharSequence topLabel;
    public int topMarginInPx;
    public int topTextSizeInPx = sp2px(12);
    public int topTextColor = Color.DKGRAY;
    
    /**
     * 绘制在圆内底部的文字
     */
    public CharSequence bottomLabel;
    public int bottomTextColor = Color.DKGRAY;
    public int bottomMarginInPx;
    public int bottomTextSizeInPx = sp2px(12);
    
    /**
     * 是否在中心位置绘制当前选择的值。默认为false
     */
    public boolean drawValueEnable = false;
    public CharSequence centerLabel;
    public int centerTextColor = Color.DKGRAY;
    public int centerTextSizeInPx = sp2px(15);
    private Paint mCenterTextPaint;
    
    /**
     * 选择器的背景颜色
     */
    private int mCircleBackgroundColor = 0xffefeff4;
    
    private static String[] getMinuteOptions() {
        String[] ops = new String[60];
        for (int i = 0; i < 60; i++) {
            ops[i] = Integer.toString(i);
        }
        return ops;
    }
    
    public CirclePicker(Context context) {
        super(context);
        initialize();
    }
    
    public CirclePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    
    public CirclePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    
    private void initialize() {
        if (isInEditMode()) {
            // 预览模式
            setOptions(getMinuteOptions());
            setLabelIncrement(5);
        }
        
        // 0xefeff4
        final int defaultPadding = dp2px(2);
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
        mScaleDotRadius = dp2px(3);
        mThumbRadius = dp2px(20);
        mThumbDotRadius = dp2px(2);
        mThumbStrokeWidth = dp2px(2);
        topMarginInPx = bottomMarginInPx = dp2px(8);
        
        /// Init Paints
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mCircleBackgroundColor);
        
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColorNormal);
        mTypeface = Typeface.create("sans-serif", Typeface.NORMAL);
        mTextPaint.setTypeface(mTypeface);
        
        mThumbPaint.setColor(AttributeUtils.fetchAttributeColor(getContext(), android.R.attr.colorAccent));
    }
    
    public void setCircleBackgroundColor(int circleBackgroundColor) {
        mCircleBackgroundColor = circleBackgroundColor;
        mBackgroundPaint.setColor(mCircleBackgroundColor);
    }
    
    public void setOptions(@NonNull String[] options) {
        this.options = options;
        if (isOptionsEmpty()) {
            mSweepAngle = 0;
        } else {
            mSweepAngle = 360.0f / options.length;
            invalidate();
        }
    }
    
    /**
     * Convenience to set options in range [from, to] by stride
     *
     * @param from value onStart from
     * @param to   value end to
     * @param by   value stride
     */
    public void setIntOptions(int from, int to, int by) {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = from; i <= to; i += by) {
            temp.add(Integer.toString(i));
        }
        String[] ops = new String[temp.size()];
        setOptions(temp.toArray(ops));
    }
    
    public void setLabelIncrement(int labelIncrement) {
        mLabelIncrement = labelIncrement;
    }
    
    public int getSelectedIndex() {
        return mSelectedIndex;
    }
    
    public void setSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
        setThumbDegrees(mSelectedIndex * mSweepAngle);
    }
    
    @Nullable
    public String getSelectedValue() {
        if (!isOptionsEmpty()) {
            return options[mSelectedIndex];
        }
        return null;
    }
    
    public void setSelectedValue(@NonNull String value) {
        if (!isOptionsEmpty()) {
            for (int i = 0; i < options.length; i++) {
                if (value.equals(options[i])) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        mOnValueChangedListener = onValueChangedListener;
    }
    
    private Paint getTopPaint() {
        if (mTopPaint == null) {
            mTopPaint = defaultTextPaint();
        }
        return mTopPaint;
    }
    
    private Paint getBottomPaint() {
        if (mBottomPaint == null) {
            mBottomPaint = defaultTextPaint();
        }
        return mBottomPaint;
    }
    
    private Paint getCenterTextPaint() {
        if (mCenterTextPaint == null) {
            mCenterTextPaint = defaultTextPaint();
        }
        return mCenterTextPaint;
    }
    
    private Paint defaultTextPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(mTypeface);
        return paint;
    }
    
    private boolean isOptionsEmpty() {
        return options == null || options.length == 0;
    }
    
    private void setThumbDegrees(float thumbDegrees) {
        mThumbDegrees = readjustDegrees(thumbDegrees);
        invalidate();
    }
    
    private void setSelected(int newIndex) {
        if (mSelectedIndex != newIndex) {
            mSelectedIndex = newIndex;
            notifyValueChanged();
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int min = Math.min(width, height);
        width = height = min;
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
        final int min = Math.min(getWidth(), getHeight());
        
        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
        mCircleRadius = Math.min(mXCenter, mYCenter);
        mTextCircle.centerX = mXCenter;
        mTextCircle.centerY = mYCenter;
        mTextCircle.radius = min / 2 - getPaddingLeft() - mThumbRadius;
    }
    
    
    // =====================================
    // MARK - Draw 绘制
    // =====================================
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 绘制背景
        drawCircleBackground(canvas);
        
        if (isOptionsEmpty()) {
            return;
        }
        
        // 绘制选择器
        drawSelector(canvas);
        // 绘制刻度文字
        drawTexts(canvas);
        // 绘制描述文字
        drawDescriptionLabels(canvas);
    }
    
    private void drawCircleBackground(final Canvas canvas) {
        canvas.drawCircle(mXCenter, mYCenter, mCircleRadius, mBackgroundPaint);
    }
    
    private void drawTexts(Canvas canvas) {
        // final float activatedIndex = mThumbDegrees / mSweepAngle;
        // final int activatedFloor = (int)activatedIndex;
        // final int activatedCeil = ((int)Math.ceil(activatedIndex)) % options.length;
        
        for (int i = 0; i < options.length; i++) {
            float angleInDegree = i * mSweepAngle + START_ANGLE;
            final float posX = mTextCircle.xWithAngle(angleInDegree);
            final float posY = mTextCircle.yWithAngle(angleInDegree);
            // 顶部到baseline距离 + 底部到baseline的距离
            final float textPosY = posY - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
            final boolean activated = mThumbCircle.containsPoint(posX, posY);
            if (activated) {
                mTextPaint.setColor(mTextColorHighlight);
            } else {
                mTextPaint.setColor(mTextColorNormal);
            }
            if (i % mLabelIncrement == 0) {
                canvas.drawText(options[i], posX, textPosY, mTextPaint);
            } else if (mSelectedIndex == i) {
                canvas.drawCircle(posX, posY, mScaleDotRadius, mTextPaint);
            }
        }
    }
    
    private void drawSelector(final Canvas canvas) {
        // Draw Selector Circle
        final Point thumbCenter = mTextCircle.pointWithAngle(mThumbDegrees + START_ANGLE);
        canvas.drawCircle(thumbCenter.x, thumbCenter.y, mThumbRadius, mThumbPaint);
        mThumbCircle.centerX = thumbCenter.x;
        mThumbCircle.centerY = thumbCenter.y;
        mThumbCircle.radius = mThumbRadius;
        
        if (!drawValueEnable && drawThumbLineEnable) {
            // Draw Selector Dot
            canvas.drawCircle(mXCenter, mYCenter, mThumbDotRadius, mThumbPaint);
            
            // Draw Selector Line
            mThumbPaint.setStrokeWidth(mThumbStrokeWidth);
            canvas.drawLine(mXCenter, mYCenter, thumbCenter.x, thumbCenter.y, mThumbPaint);
        }
    }
    
    private void drawDescriptionLabels(final Canvas canvas) {
        if (!TextUtils.isEmpty(topLabel)) {
            final Paint paint = getTopPaint();
            paint.setColor(topTextColor);
            paint.setTextSize(topTextSizeInPx);
            final float xCenter = mXCenter;
            float yCenter = getPaddingTop() + mThumbRadius * 2 + topMarginInPx;
            yCenter += (paint.descent() - paint.ascent()) / 2;
            canvas.drawText(topLabel, 0, topLabel.length(), xCenter, yCenter, paint);
        }
        
        if (!TextUtils.isEmpty(bottomLabel)) {
            final Paint paint = getBottomPaint();
            paint.setColor(bottomTextColor);
            paint.setTextSize(bottomTextSizeInPx);
            final float xCenter = mXCenter;
            float yCenter = getHeight() - getPaddingBottom() - mThumbRadius * 2 - bottomMarginInPx;
            //yCenter -= (paint.descent() - paint.ascent()) / 2;
            canvas.drawText(bottomLabel, 0, bottomLabel.length(), xCenter, yCenter, paint);
        }
        
        if (drawValueEnable && getSelectedValue() != null) {
            final Paint paint = getCenterTextPaint();
            paint.setColor(centerTextColor);
            paint.setTextSize(centerTextSizeInPx);
            final float xCenter = mXCenter;
            float yCenter = mYCenter - (paint.ascent() + paint.descent()) / 2;
            canvas.drawText(getSelectedValue(), 0, getSelectedValue().length(), xCenter, yCenter, paint);
        } else if (!TextUtils.isEmpty(centerLabel)) {
            final Paint paint = getCenterTextPaint();
            paint.setColor(centerTextColor);
            paint.setTextSize(centerTextSizeInPx);
            final float xCenter = mXCenter;
            float yCenter = mYCenter - (paint.ascent() + paint.descent()) / 2;
            canvas.drawText(centerLabel, 0, centerLabel.length(), xCenter, yCenter, paint);
        }
    }
    
    /// MARK: - Touch Event 触摸事件
    
    private void startDragging(float x, float y) {
        if (isBeingDragged) {
            return;
        }
        final float distance = (float) mTextCircle.distanceToCenter(x, y);
        if (distance <= (mTextCircle.radius + mThumbRadius)
                && distance >= (mTextCircle.radius - mThumbRadius)) {
            isBeingDragged = true;
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return onTouchDown(event);
            
            case MotionEvent.ACTION_MOVE:
                return onTouchMove(event);
            
            case MotionEvent.ACTION_UP:
                onTouchUp();
                break;
            
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel();
                break;
        }
        
        if (isBeingDragged) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        
        return isBeingDragged;
    }
    
    private boolean onTouchDown(MotionEvent event) {
        startDragging(event.getX(), event.getY());
        if (isBeingDragged) {
            updateThumbLocation(event.getX(), event.getY());
        }
        return isBeingDragged;
    }
    
    private boolean onTouchMove(MotionEvent event) {
        startDragging(event.getX(), event.getY());
        if (isBeingDragged) {
            updateThumbLocation(event.getX(), event.getY());
        }
        return isBeingDragged;
    }
    
    private void onTouchUp() {
        onDragFinished();
    }
    
    private void onTouchCancel() {
        onDragFinished();
    }
    
    private void updateThumbLocation(final float x, final float y) {
        setThumbDegrees(degreesFromXY(x, y) + 90);
        checkSelectedIndex();
    }
    
    private void onDragFinished() {
        final float modulus = mThumbDegrees % mSweepAngle;
        final float absModulus = Math.abs(modulus);
        float endDegrees = mThumbDegrees;
        if (absModulus > (mSweepAngle / 2)) {
            if (modulus > 0) {
                endDegrees += mSweepAngle - modulus;
            } else {
                endDegrees -= mSweepAngle - absModulus;
            }
        } else {
            endDegrees -= modulus;
        }
        setThumbDegrees(endDegrees);
        checkSelectedIndex();
        isBeingDragged = false;
    }
    
    private void checkSelectedIndex() {
        final float activatedIndex = mThumbDegrees / mSweepAngle;
        setSelected(Math.round(activatedIndex) % options.length);
    }
    
    private void notifyValueChanged() {
        if (mOnValueChangedListener != null) {
            mOnValueChangedListener.onValueChanged(this);
            // SoundEffectHelper.getInstance().playGearSound(getContext());
        }
    }
    
    // =====================================
    // MARK - Convenience
    // =====================================
    
    private int sp2px(int sp) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * (float) sp + 0.5F);
    }
    
    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * (float) dp + 0.5F);
    }
    
    private float degreesFromXY(float x1, float y1) {
        // 计算向量(x1-cx, y1 - cy)与横轴的夹角(-PI ~ PI)
        return (float) Math.toDegrees(Math.atan2(y1 - mTextCircle.centerY, x1 - mTextCircle.centerX));
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
