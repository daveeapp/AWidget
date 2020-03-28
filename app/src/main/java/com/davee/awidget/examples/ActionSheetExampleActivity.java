package com.davee.awidget.examples;

import android.os.Bundle;
import android.view.View;

import com.davee.awidget.R;
import com.davee.awidgets.actionsheet.ActionHandler;
import com.davee.awidgets.actionsheet.ActionSheet;
import com.davee.awidgets.actionsheet.AlertAction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ActionSheetExampleActivity
 * <p>
 * Created by davee 2020/3/28.
 * Copyright (c) 2020 davee. All rights reserved.
 */
public class ActionSheetExampleActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_example_actionsheet);
        
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }
    
    private void show() {
        ActionSheet actionSheet = new ActionSheet(ActionSheetExampleActivity.this);
        
        AlertAction defaultAction = new AlertAction(AlertAction.ACTION_STYLE_DEFAULT, "Default Action", new ActionHandler() {
            @Override
            public void onActionClicked(@NonNull ActionSheet actionSheet, @NonNull AlertAction alertAction) {
        
            }
        });
    
        AlertAction destructiveAction = new AlertAction(AlertAction.ACTION_STYLE_DESTRUCTIVE, "Destructive Action", new ActionHandler() {
            @Override
            public void onActionClicked(@NonNull ActionSheet actionSheet, @NonNull AlertAction alertAction) {
            
            }
        });
    
        AlertAction cancelAction = new AlertAction(AlertAction.ACTION_STYLE_CANCEL, "Cancel Action", new ActionHandler() {
            @Override
            public void onActionClicked(@NonNull ActionSheet actionSheet, @NonNull AlertAction alertAction) {
            
            }
        });
        
        actionSheet.addAlertAction(defaultAction);
        actionSheet.addAlertAction(destructiveAction);
        actionSheet.addAlertAction(cancelAction);
        
        actionSheet.show(true);
    }
}
