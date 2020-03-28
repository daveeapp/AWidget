package com.davee.awidgets.actionsheet;

import android.content.Context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * AlertAction
 * <p>
 * Created by davee 2017/6/9.
 * Copyright (c) 2017 davee. All rights reserved.
 */
public class AlertAction {
    
    public static final int ACTION_STYLE_DEFAULT = 0;
    public static final int ACTION_STYLE_CANCEL = 1;
    public static final int ACTION_STYLE_DESTRUCTIVE = 2;
    
    @IntDef(value = {ACTION_STYLE_DEFAULT, ACTION_STYLE_CANCEL, ACTION_STYLE_DESTRUCTIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionStyle {
    }
    
    @ActionStyle
    private int mActionStyle = ACTION_STYLE_DEFAULT;
    private boolean mEnabled = true;
    private int mTitleRes = 0;
    private CharSequence mTitle;
    private ActionHandler mActionHandler;
    
    private int mTitleColor = 0;
    private int mTitleSize = 0;
    
    private int mTag;
    
    public static AlertAction defaultAction(int titleRes, ActionHandler actionHandler) {
        return new AlertAction(ACTION_STYLE_DEFAULT, titleRes, actionHandler);
    }
    
    public static AlertAction cancelAction(int titleRes, ActionHandler actionHandler) {
        return new AlertAction(ACTION_STYLE_CANCEL, titleRes, actionHandler);
    }
    
    public static AlertAction destructiveAction(int titleRes, ActionHandler actionHandler) {
        return new AlertAction(ACTION_STYLE_DESTRUCTIVE, titleRes, actionHandler);
    }
    
    // AlertAction() { }
    
    // public AlertAction(int actionId, String title) {
    //     mTag = actionId;
    //     mTitle = title;
    // }
    
    // public AlertAction(String title, int actionId, ActionHandler actionHandler) {
    //     mTitle = title;
    //     mTag = actionId;
    //     mActionHandler = actionHandler;
    // }
    
    public AlertAction(@ActionStyle int actionStyle, int titleRes, ActionHandler actionHandler) {
        mActionStyle = actionStyle;
        mTitleRes = titleRes;
        mActionHandler = actionHandler;
    }
    
    public AlertAction(@ActionStyle int actionStyle, String title, ActionHandler actionHandler) {
        mActionStyle = actionStyle;
        mTitle = title;
        mActionHandler = actionHandler;
    }
    
    // public AlertAction(int actionStyle, String title, int actionId, ActionHandler actionHandler) {
    //     mActionStyle = actionStyle;
    //     mTitle = title;
    //     mTag = actionId;
    //     mActionHandler = actionHandler;
    // }
    
    @ActionStyle
    public int getActionStyle() {
        return mActionStyle;
    }
    
    public void setActionStyle(int actionStyle) {
        mActionStyle = actionStyle;
    }
    
    public CharSequence getTitle(Context context) {
        if (mTitle == null && mTitleRes != 0) {
            mTitle = context.getText(mTitleRes);
        }
        return mTitle;
    }
    
    public void setTitleRes(int titleRes) {
        mTitleRes = titleRes;
    }
    
    public void setTitle(CharSequence title) {
        mTitle = title;
    }
    
    public boolean isEnabled() {
        return mEnabled;
    }
    
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }
    
    public ActionHandler getActionHandler() {
        return mActionHandler;
    }
    
    public void setActionHandler(ActionHandler actionHandler) {
        mActionHandler = actionHandler;
    }
    
    ///
    
    public int getTitleColor() {
        return mTitleColor;
    }
    
    public void setTitleColor(int titleColor) {
        mTitleColor = titleColor;
    }
    
    public int getTitleSize() {
        return mTitleSize;
    }
    
    public void setTitleSize(int titleSize) {
        mTitleSize = titleSize;
    }
    
    public int getTag() {
        return mTag;
    }
    
    public void setTag(int tag) {
        mTag = tag;
    }
    
    
    // public static class Builder {
    //     private Context mContext;
    //     AlertAction mAlertAction;
    //
    //     public Builder(Context context) {
    //         mContext = context;
    //         mAlertAction = new AlertAction();
    //     }
    //
    //     public Builder setEnable(boolean enable){
    //         return this;
    //     }
    //
    //     public Builder setActionStyle(int actionStyle){
    //         mAlertAction.setActionStyle(actionStyle);
    //         return this;
    //     }
    //
    //     public Builder setTitle(int resId){
    //         mAlertAction.setTitle(mContext.getString(resId));
    //         return this;
    //     }
    //
    //     public Builder setTitle(String title){
    //         mAlertAction.setTitle(title);
    //         return this;
    //     }
    //
    //     public Builder setTitleColor(int color){
    //         mAlertAction.setTitleColor(color);
    //         return this;
    //     }
    //
    //     public Builder setTitleSize(int sizeInSp){
    //         mAlertAction.setTitleSize(sizeInSp);
    //         return this;
    //     }
    //
    //     public Builder setTag(int tag){
    //         mAlertAction.setTag(tag);
    //         return this;
    //     }
    //
    //     public Builder setActionHandler(ActionHandler handler){
    //         mAlertAction.setActionHandler(handler);
    //         return this;
    //     }
    //
    //     public AlertAction create(){
    //         return mAlertAction;
    //     }
    // }
    
}
