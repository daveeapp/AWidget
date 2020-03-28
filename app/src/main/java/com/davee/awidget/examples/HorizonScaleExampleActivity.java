package com.davee.awidget.examples;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.davee.awidget.R;
import com.davee.awidgets.HorizonScaleBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * HorizonScaleExampleActivity
 * <p>
 * Created by davee 2020/3/28.
 * Copyright (c) 2020 davee. All rights reserved.
 */
public class HorizonScaleExampleActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_scalebar);
    
        HorizonScaleBar scaleBar = findViewById(R.id.scaleBar);
        TextView textView = findViewById(R.id.textView);
        
        scaleBar.setOnScaleChangedListener(new HorizonScaleBar.OnScaleChangedListener() {
            @Override
            public void onScaleValueChanged(int scaleValue) {
                textView.setText("selected: " + scaleValue);
                
                // Toast.makeText(getBaseContext(), "Selected: " + scaleValue, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
