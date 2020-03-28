package com.davee.awidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * AttributeUtils
 * <p>
 * Created by davee 2017/5/12.
 * Copyright (c) 2017 davee. All rights reserved.
 */
public class AttributeUtils {
    
    /**
     * This method can be extended to get all android attributes color, string, dimension ...etc
     *
     * @param context          used to fetch android attribute
     * @param androidAttribute attribute codes like R.attr.colorAccent
     * @return in this case color of android attribute
     */
    public static int fetchAttributeColor(Context context, int androidAttribute) {
        TypedValue typedValue = new TypedValue();
        
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{androidAttribute});
        int color = a.getColor(0, 0);
        
        a.recycle();
        
        return color;
    }
}
