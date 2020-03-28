package com.davee.awidgets;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * DrawableUtils
 * <p>
 * Created by davee 2017/6/9.
 * Copyright (c) 2017 davee. All rights reserved.
 */
public class DrawableUtils {
    
    public static StateListDrawable wrapColorDrawables(int colorNormal, int colorPressed){
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(colorPressed));
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, new ColorDrawable(colorNormal));
        stateListDrawable.addState(new int[]{}, new ColorDrawable(colorNormal));
        return stateListDrawable;
    }
    
    public static StateListDrawable wrapStateListDrawable(Drawable drawableNormal, Drawable drawablePressed){
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, drawableNormal);
        stateListDrawable.addState(new int[]{}, drawableNormal);
        return stateListDrawable;
    }
    
    public static Drawable newRoundCornerColorDrawable(int color, int radius){
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }
    
    public static Drawable newRoundCornerColorDrawable(int color, float[] radii){
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadii(radii);
        return gradientDrawable;
    }
}
