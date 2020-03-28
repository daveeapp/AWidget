package com.davee.awidgets.actionsheet;

import android.graphics.drawable.ColorDrawable;

/**
 * ColorDrawableExt
 * <p>
 * Created by davee 2017/6/9.
 * Copyright (c) 2017 davee. All rights reserved.
 */
public class ColorDrawableExt extends ColorDrawable {
    
    private int mIntrinsicWidth = -1;
    private int mIntrinsicHeight = -1;
    
    public ColorDrawableExt(int color) {
        super(color);
    }
    
    public ColorDrawableExt(int color, int intrinsicWidth, int intrinsicHeight) {
        super(color);
        mIntrinsicWidth = intrinsicWidth;
        mIntrinsicHeight = intrinsicHeight;
    }
    
    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }
    
    public void setIntrinsicWidth(int intrinsicWidth) {
        mIntrinsicWidth = intrinsicWidth;
    }
    
    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }
    
    public void setIntrinsicHeight(int intrinsicHeight) {
        mIntrinsicHeight = intrinsicHeight;
    }
}
