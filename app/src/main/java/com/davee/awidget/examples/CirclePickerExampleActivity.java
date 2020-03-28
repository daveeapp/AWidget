package com.davee.awidget.examples;

import android.os.Bundle;

import com.davee.awidget.R;
import com.davee.awidgets.CirclePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * CirclePickerExampleActivity
 * <p>
 * Created by davee 2020/3/28.
 * Copyright (c) 2020 davee. All rights reserved.
 */
public class CirclePickerExampleActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_example_circle_picker);
    
        CirclePicker circlePicker = findViewById(R.id.circlePicker);
        circlePicker.setOptions(getMinuteOptions());
        circlePicker.setLabelIncrement(5);
    }
    
    private static String[] getMinuteOptions() {
        String[] ops = new String[60];
        for (int i = 0; i < 60; i++) {
            ops[i] = Integer.toString(i);
        }
        return ops;
    }
}
