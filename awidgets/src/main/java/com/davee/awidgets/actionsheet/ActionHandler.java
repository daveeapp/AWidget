package com.davee.awidgets.actionsheet;


import androidx.annotation.NonNull;

/**
 * ActionHandler
 * <p>
 * Created by davee 2018/9/15.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public interface ActionHandler {
    void onActionClicked(@NonNull ActionSheet actionSheet, @NonNull AlertAction alertAction);
}
