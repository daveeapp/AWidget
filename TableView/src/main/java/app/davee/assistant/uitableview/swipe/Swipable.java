package app.davee.assistant.uitableview.swipe;

/**
 * Swipable
 * <p>
 * Created by davee 2018/4/21.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public interface Swipable {
    
    boolean isLeadingSwipeEnable();
    
    boolean isTrailingSwipeEnable();
    
    int getThresholdOffset();
    
    SwipeAction getFirstSwipeAction();
    
    boolean shouldPerformFullSwipe();
    
    
}
