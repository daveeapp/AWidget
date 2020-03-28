package com.davee.awidgets.actionsheet;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davee.awidgets.Dimens;
import com.davee.awidgets.DrawableUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

/**
 * ActionSheet 仿iOS的UIAlertActionSheet
 * <p>
 * Created by davee 2017/6/9.
 * Copyright (c) 2017 davee. All rights reserved.
 */
public class ActionSheet extends AppCompatDialog
        implements View.OnClickListener,
        DialogInterface.OnCancelListener,
        DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener {
    
    private static final int DEFAULT_SPACING = 10; // dp
    private static final int DEFAULT_PADDING = 16; // dp
    private static final int DEFAULT_RADIUS = 10; // dp
    private static final int DEFAULT_DIVIDER_COLOR = 0xffb8b8b8;
    
    private static final int DEFAULT_TITLE_COLOR = 0xff676767;
    private static final int DEFAULT_MESSAGE_COLOR = 0xffb8b8b8;
    private static final int DEFAULT_TITLE_TEXT_SIZE = 15; //sp
    private static final int DEFAULT_MESSAGE_TEXT_SIZE = 13; //sp
    
    private static final int DEFAULT_ACTION_TITLE_COLOR = 0xff3B99FC;
    private static final int DEFAULT_ACTION_TITLE_COLOR_DESTRUCTIVE = 0xffFF3B30;
    
    private static final int RADIUS_NONE = 0;
    private static final int RADIUS_TOP = 1;
    private static final int RADIUS_BOTTOM = 2;
    private static final int RADIUS_ALL = 3;
    
    private float[] mTopRadii = new float[8];
    private float[] mAllRadii = new float[8];
    private float[] mBottomRadii = new float[8];
    
    private int mDividerColor = DEFAULT_DIVIDER_COLOR;
    
    private int mTitleColor = DEFAULT_TITLE_COLOR;
    private int mMessageColor = DEFAULT_MESSAGE_COLOR;
    private int mTitleSize = DEFAULT_TITLE_TEXT_SIZE;
    private int mMessageSize = DEFAULT_MESSAGE_TEXT_SIZE;
    
    private LinearLayout mContentView;
    private LinearLayout mTopLayout;
    
    /**
     * The action with style ACTION_STYLE_CANCEL will be add to this layout
     */
    private LinearLayout mBottomLayout;
    private TextView mTitleView;
    private TextView mMessageView;
    
    private CharSequence mTitle;
    private CharSequence mMessage;
    private ArrayList<AlertAction> mAlertActions = new ArrayList<>();
    
    private DialogInterface.OnShowListener mOnShowListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private DialogInterface.OnCancelListener mOnCancelListener;
    
    private boolean mAnimated = false;
    
    public ActionSheet(@NonNull Context context) {
        super(context);
        init();
    }
    
    public ActionSheet(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }
    
    protected ActionSheet(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }
    
    private void init() {
        // Handle the listener by self
        super.setOnCancelListener(this);
        super.setOnShowListener(this);
        super.setOnDismissListener(this);
        
        int radius = Dimens.dp2px(getContext(), DEFAULT_RADIUS);
        for (int i = 0; i < 8; i++) {
            mAllRadii[i] = radius;
            if (i < 4) {
                mTopRadii[i] = radius;
            } else {
                mBottomRadii[i] = radius;
            }
        }
        
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }
    
    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getText(titleId));
    }
    
    public void setTitleColor(int titleColor) {
        mTitleColor = titleColor;
    }
    
    public void setTitleSize(int titleSize) {
        mTitleSize = titleSize;
    }
    
    public void setMessage(CharSequence message) {
        mMessage = message;
    }
    
    public void setMessage(int messageId){
        setMessage(getContext().getText(messageId));
    }
    
    public void setMessageColor(int messageColor) {
        mMessageColor = messageColor;
    }
    
    public void setMessageSize(int messageSize) {
        mMessageSize = messageSize;
    }
    
    public int getDividerColor() {
        return mDividerColor;
    }
    
    public void setDividerColor(int dividerColor) {
        mDividerColor = dividerColor;
    }
    
    public void show(boolean animated){
        mAnimated = animated;
        super.show();
    }
    
    @Override
    public void onShow(DialogInterface dialog) {
        if (mAnimated){
            final int translateY = mContentView.getHeight() + Dimens.dp2px(getContext(), 8);
            mContentView.setTranslationY(translateY);
            animateContentLayout(translateY, 0, null);
        }
        if (mOnShowListener != null){
            mOnShowListener.onShow(dialog);
        }
    }
    
    @Override
    public void onCancel(DialogInterface dialog) {
        // dispatch cancel action handler
        for (AlertAction action : mAlertActions) {
            if (action.getActionStyle() == AlertAction.ACTION_STYLE_CANCEL) {
                if (action.getActionHandler() != null) {
                    action.getActionHandler().onActionClicked(this, action);
                }
            }
        }
    
        if (mOnCancelListener != null){
            mOnCancelListener.onCancel(dialog);
        }
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mAnimated){
            // FIXME: 2017/6/10 No Effects
            final int height = mContentView.getHeight() + Dimens.dp2px(getContext(), 8);
            animateContentLayout(0, height, null);
        }
    
        if (mOnDismissListener != null){
            mOnDismissListener.onDismiss(dialog);
        }
    }
    
    private void animateContentLayout(float startOffset, float endOffset, Animator.AnimatorListener animatorListener){
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(startOffset, endOffset);
        valueAnimator.setDuration(250);
        // valueAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final View target = mContentView;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                target.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        if (animatorListener != null){
            valueAnimator.addListener(animatorListener);
        }
        valueAnimator.start();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mContentView = onCreateView();
        setContentView(mContentView);
        // These attributes must be set after the contentView set
        final Window window = getWindow();
        if (window != null) {
            // The default background is white, set to transparent
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
        
        onLoadSubview();
    }
    
    private LinearLayout onCreateView() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        final int padding = Dimens.dp2px(getContext(), DEFAULT_PADDING);
        linearLayout.setPadding(padding, padding, padding, padding);
        
        mTopLayout = newActionLayout();
        linearLayout.addView(mTopLayout, -1, -2);
        
        mBottomLayout = newActionLayout();
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(-1, -2);
        bottomParams.topMargin = Dimens.dp2px(getContext(), DEFAULT_SPACING);
        linearLayout.addView(mBottomLayout, bottomParams);
        
        return linearLayout;
    }
    
    private LinearLayout newActionLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        // layout.setBackgroundColor(Color.WHITE);
        layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        ColorDrawableExt divider = new ColorDrawableExt(mDividerColor);
        divider.setIntrinsicHeight(1);
        layout.setDividerDrawable(divider);
        return layout;
    }
    
    private LinearLayout getBottomLayout() {
        if (mBottomLayout == null) {
            mBottomLayout = newActionLayout();
            LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(-1, -2);
            bottomParams.topMargin = Dimens.dp2px(getContext(), DEFAULT_SPACING);
            mContentView.addView(mBottomLayout, bottomParams);
        }
        return mBottomLayout;
    }
    
    private void onLoadSubview() {
        ensureMessageView();
        addActions();
        
        // Configure top layout children background
        configureBackground(mTopLayout);
        if (mBottomLayout.getVisibility() == View.VISIBLE){
            configureBackground(mBottomLayout);
        }
    }
    
    private void ensureMessageView() {
        boolean hasTitle = !TextUtils.isEmpty(mTitle);
        boolean hasMessage = !TextUtils.isEmpty(mMessage);
        
        if (!hasTitle && !hasMessage) {
            return;
        }
        
        final int padding = Dimens.dp2px(getContext(), DEFAULT_RADIUS);
        
        LinearLayout topView = new LinearLayout(getContext());
        topView.setOrientation(LinearLayout.VERTICAL);
        topView.setPadding(padding, padding, padding, padding);
        mTopLayout.addView(topView, -1, -2);
        
        if (hasTitle) {
            mTitleView = new TextView(getContext());
            mTitleView.setText(mTitle);
            mTitleView.setGravity(Gravity.CENTER_HORIZONTAL);
            mTitleView.setTextColor(mTitleColor);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleSize);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            topView.addView(mTitleView, 0, params);
        }
        
        if (hasMessage) {
            mMessageView = new TextView(getContext());
            mMessageView.setText(mMessage);
            mMessageView.setGravity(Gravity.CENTER_HORIZONTAL);
            mMessageView.setTextColor(mMessageColor);
            mMessageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mMessageSize);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            if (hasTitle){
                params.topMargin = padding;
            }
            topView.addView(mMessageView, params);
        }
    }
    
    private void addActions() {
        // boolean hasCancelAction = false;
        for (AlertAction alertAction : mAlertActions) {
            ActionView actionView = new ActionView(getContext());
            actionView.setAlertAction(alertAction);
            actionView.setOnClickListener(this);
            if (actionView.isCancelStyle()) {
                // hasCancelAction = true;
                getBottomLayout().addView(actionView, -1, -2);
            } else {
                mTopLayout.addView(actionView, -1, -2);
            }
        }
        // if (!hasCancelAction) {
        //     mBottomLayout.setVisibility(View.GONE);
        // } else {
        //     mBottomLayout.setVisibility(View.VISIBLE);
        // }
    }
    
    public void addAlertAction(@NonNull AlertAction alertAction) {
        mAlertActions.add(alertAction);
    }
    
    // public void addAlertActions(@NonNull ArrayList<AlertAction> alertActions) {
    //     mAlertActions.addAll(alertActions);
    // }
    //
    // public void setAlertActions(@NonNull ArrayList<AlertAction> alertActions) {
    //     mAlertActions.clear();
    //     mAlertActions.addAll(alertActions);
    // }
    
    public void removeAlertAction(AlertAction alertAction) {
        if (alertAction == null) {
            return;
        }
        final int index = mAlertActions.indexOf(alertAction);
        if (index >= 0) {
            mAlertActions.remove(index);
        }
    }
    
    public void clearAllActions() {
        mAlertActions.clear();
    }
    
    private int getColor(int res) {
        return ContextCompat.getColor(getContext(), res);
    }
    
    @Override
    public void onClick(View v) {
        if (v instanceof ActionView) {
            ActionView actionView = (ActionView) v;
            AlertAction action = actionView.getAlertAction();
            if (action.getActionHandler() != null) {
                action.getActionHandler().onActionClicked(this, action);
            }
            dismiss();
        }
    }
    
    /// Create child background
    private void configureBackground(LinearLayout layout) {
        final int topCount = layout.getChildCount();
        for (int i = 0; i < topCount; i++) {
            View child = layout.getChildAt(i);
            int radiusMode;
            if (i == 0) {
                if (topCount == 1) {
                    // only one child
                    radiusMode = RADIUS_ALL;
                } else {
                    radiusMode = RADIUS_TOP;
                }
            } else {
                if (i == (topCount - 1)) {
                    radiusMode = RADIUS_BOTTOM;
                } else {
                    radiusMode = RADIUS_NONE;
                }
            }
            
            boolean isActionView = false;
            if (child instanceof ActionView) {
                isActionView = true;
            }
            
            child.setBackground(createBackground(radiusMode, isActionView));
        }
    }
    
    private Drawable createBackground(int radiusMode, boolean needPressedState) {
        final float[] radii;
        if (radiusMode == RADIUS_TOP) {
            radii = mTopRadii;
        } else if (radiusMode == RADIUS_BOTTOM) {
            radii = mBottomRadii;
        } else if (radiusMode == RADIUS_ALL) {
            radii = mAllRadii;
        } else {
            radii = null;
        }
        if (radii != null) {
            if (!needPressedState) {
                return DrawableUtils.newRoundCornerColorDrawable(Color.WHITE, radii);
            } else {
                Drawable drawableNormal = DrawableUtils.newRoundCornerColorDrawable(Color.WHITE, radii);
                Drawable drawablePressed = DrawableUtils.newRoundCornerColorDrawable(Color.LTGRAY, radii);
                return DrawableUtils.wrapStateListDrawable(drawableNormal, drawablePressed);
            }
        } else {
            if (!needPressedState) {
                return new ColorDrawable(Color.WHITE);
            } else {
                return DrawableUtils.wrapColorDrawables(Color.WHITE, Color.LTGRAY);
            }
        }
    }
    
    @Override
    public void setOnShowListener(@Nullable DialogInterface.OnShowListener listener) {
        // super.setOnShowListener(listener);
        mOnShowListener = listener;
    }
    
    @Override
    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        // super.setOnCancelListener(listener);
        mOnCancelListener = listener;
    }
    
    @Override
    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        // super.setOnDismissListener(listener);
        mOnDismissListener = listener;
    }
    
    static class ActionView extends AppCompatButton {
        
        private static final int DEFAULT_TEXT_SIZE = 17; // sp
        
        private AlertAction mAlertAction;
        
        public ActionView(Context context) {
            super(context);
            init();
        }
        
        private void init() {
            // this.setBackground(DrawableUtils.wrapColorDrawables(BACKGROUND_COLOR_NORMAL, BACKGROUND_COLOR_PRESSED));
            this.setTextSize(DEFAULT_TEXT_SIZE);
            this.setAllCaps(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Disable the border effect when pressed
                this.setStateListAnimator(null);
            }
        }
        
        public AlertAction getAlertAction() {
            return mAlertAction;
        }
        
        public void setAlertAction(@NonNull AlertAction alertAction) {
            if (mAlertAction != alertAction) {
                mAlertAction = alertAction;
                applyAction();
            }
        }
        
        private void applyAction() {
            if (mAlertAction == null) {
                return;
            }
            this.setEnabled(mAlertAction.isEnabled());
            this.setText(mAlertAction.getTitle(getContext()));
            
            if (mAlertAction.getTitleColor() == 0){
                switch (mAlertAction.getActionStyle()) {
                    case AlertAction.ACTION_STYLE_CANCEL:
                        setTextColor(DEFAULT_ACTION_TITLE_COLOR);
                        break;
                    case AlertAction.ACTION_STYLE_DEFAULT:
                        setTextColor(DEFAULT_ACTION_TITLE_COLOR);
                        break;
                    case AlertAction.ACTION_STYLE_DESTRUCTIVE:
                        setTextColor(DEFAULT_ACTION_TITLE_COLOR_DESTRUCTIVE);
                        break;
                }
            } else {
                setTextColor(mAlertAction.getTitleColor());
            }
            
            if (mAlertAction.getTitleSize() != 0){
                setTextSize(mAlertAction.getTitleSize());
            }
            
        }
        
        public boolean isCancelStyle() {
            return mAlertAction != null && mAlertAction.getActionStyle() == AlertAction.ACTION_STYLE_CANCEL;
        }
        
    }
}
