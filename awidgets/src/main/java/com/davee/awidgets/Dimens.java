package com.davee.awidgets;

import android.content.Context;

/**
 * Dimens
 * <p>
 * Created by davee 2018/9/8.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class Dimens {
    
    public static int dp2px(Context context, int dp) {
        return (int)(context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    
    public static int dp8(Context context) {
        return dp2px(context, 8);
    }
    
    public static int dp16(Context context) {
        return dp2px(context, 16);
    }
    
    public static int dp20(Context context) {
        return dp2px(context, 20);
    }
    
    public static int dp24(Context context) {
        return dp2px(context, 24);
    }
    
    
    public static int sp2px(Context context, int sp) {
        return (int)(context.getResources().getDisplayMetrics().scaledDensity * (float)sp + 0.5F);
    }
    
    public static int sp15(Context context) {
        return sp2px(context, 15);
    }
    
    public static int sp17(Context context) {
        return sp2px(context, 17);
    }
    
    public static int sp20(Context context) {
        return sp2px(context, 20);
    }
}
