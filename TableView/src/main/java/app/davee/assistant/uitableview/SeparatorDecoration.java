package app.davee.assistant.uitableview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * SeparatorDecoration
 * <p>
 * UITableView默认的分割线绘制类
 * <p>
 * Created by davee 2018/3/6.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public class SeparatorDecoration extends RecyclerView.ItemDecoration {
    
    /// Separator
    private boolean mSeparatorEnable = true;
    private int mCommonSeparatorColor = Color.DKGRAY;
    private float mCommonSeparatorHeight = 1f;
    private Paint mSeparatorPaint;
    
    private Drawable mSeparatorDrawable = null;
    // private Rect mBounds = new Rect();
    
    SeparatorDecoration() {
        ensureDividerPaint();
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    private void ensureDividerPaint() {
        if (mSeparatorPaint == null) {
            mSeparatorPaint = new Paint();
            mSeparatorPaint.setAntiAlias(true);
            mSeparatorPaint.setColor(mCommonSeparatorColor);
            mSeparatorPaint.setStyle(Paint.Style.FILL);
            mSeparatorPaint.setStrokeWidth(mCommonSeparatorHeight);
        }
    }

    boolean isSeparatorEnable() {
        return mSeparatorEnable;
    }

    void setSeparatorEnable(boolean separatorEnable) {
        mSeparatorEnable = separatorEnable;
    }

    void setCommonSeparatorColor(int commonSeparatorColor) {
        if (mCommonSeparatorColor != commonSeparatorColor){
            mCommonSeparatorColor = commonSeparatorColor;
            mSeparatorPaint.setColor(mCommonSeparatorColor);
        }
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    void setCommonSeparatorHeight(float commonSeparatorHeight) {
        if (mCommonSeparatorHeight != commonSeparatorHeight){
            mCommonSeparatorHeight = commonSeparatorHeight;
            if (mCommonSeparatorHeight < 1){
                mCommonSeparatorHeight = 0;
            }
            mSeparatorPaint.setStrokeWidth(mCommonSeparatorHeight);
        }
    }
    
    // @Override
    // public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    //     super.getItemOffsets(outRect, view, parent, state);
    //     不改变offset，如果添加offset后，当绘制的分割线不是从cell左侧开始时，空白区域会漏出UITableView底色
    //     if (!mSeparatorEnable) {
    //         return;
    //     }
    //     默认不改变UITableView中每个Cell的绘制范围，分割线绘制在cell的上层
    //     final UITableView tableView = (UITableView) parent;
    //     final NSIndexPath indexPath = tableView.indexPathForCell(view);
    //     if (indexPath != null) {
    //         int topOffset = 0;
    //         if (tableView.getTableViewAdapter().isFirstCellInSection(indexPath)) {
    //             if (tableView.isSectionHeaderSeparatorEnable()) {
    //                 topOffset += mCommonSeparatorHeight;
    //             }
    //         }
    //         int bottomOffset = 0;
    //         if (tableView.getTableViewAdapter().isLastCellInSection(indexPath)) {
    //             if (tableView.isSectionFooterSeparatorEnable()) {
    //                 bottomOffset += mCommonSeparatorHeight;
    //             }
    //         } else {
    //             bottomOffset += mCommonSeparatorHeight;
    //         }
    //         outRect.set(0, topOffset, 0, bottomOffset);
    //     }
    // }
    
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (mSeparatorEnable){
            drawVertical(c, (UITableView) parent);
        }
    }
    
    /**
     * 绘制垂直方向布局时的分割线
     */
    private void drawVertical(Canvas canvas, UITableView tableView) {
        final int childrenCount = tableView.getChildCount();
        final UITableViewAdapter adapter = tableView.getTableViewAdapter();
        for (int i = 0; i < childrenCount; i++) {
            final View child = tableView.getChildAt(i);
            final NSIndexPath indexPath = tableView.indexPathForCell(child);
            if (indexPath == null){
                continue;
            }
            //Log.d("SeparatorDecoration", "drawVertical: cache indexPath = " + holder.getNowIndexPath().toString());
            if (adapter.isSectionHeaderIndex(indexPath)
                    || adapter.isSectionFooterIndex(indexPath)) {
                continue;
            }
            
            UITableViewCell cell = (UITableViewCell) child;
            if (adapter.isFirstCellInSection(indexPath) && tableView.isSectionHeaderSeparatorEnable()) {
                /* 绘制Section的头分割线 */
                //cell.drawSectionHeaderDivider(canvas, mSeparatorPaint);
                drawSectionHeaderSeparator(cell, indexPath, canvas);
            }
            if (adapter.isLastCellInSection(indexPath)) {
                // fixed: 修复设置sectionFooterSeparatorEnable = false时仍然绘制cell的separator的bug
                if (tableView.isSectionFooterSeparatorEnable()){
                    /* 绘制Section的尾部分割线 */
                    //cell.drawSectionFooterDivider(canvas, mSeparatorPaint);
                    drawSectionFooterSeparator(cell, indexPath, canvas);
                }
            } else {
                // cell.drawCellSeparator(canvas, mSeparatorPaint);
                drawCellSeparator(cell, indexPath, canvas);
            }
            
        }
    }
    
    private void drawCellSeparator(UITableViewCell cell, NSIndexPath indexPath, Canvas canvas) {
        if (!cell.shouldDrawSeparator()) {
            return;
        }
        
        final Drawable dividerDrawable = mSeparatorDrawable;
        if (dividerDrawable != null) {
            final boolean clip = dividerDrawable instanceof ColorDrawable;
            final int left = cell.getSeparatorStartX();
            final int right = cell.getSeparatorStopX();
            final int bottom = cell.getBottom();
            final int top = bottom - dividerDrawable.getIntrinsicHeight();
            if (clip) {
                canvas.save();
                canvas.clipRect(left, top, right, bottom);
            } else {
                dividerDrawable.setBounds(left, top, right, bottom);
            }
            dividerDrawable.draw(canvas);
            if (clip) {
                canvas.restore();
            }
        } else {
            // Draw Line Divider
            final int startX = cell.getSeparatorStartX();
            final int stopX = cell.getSeparatorStopX();
            final int startY = cell.getBottom();
            canvas.drawLine(startX, startY, stopX, startY, mSeparatorPaint);
        }
        
    }
    
    private void drawSectionHeaderSeparator(UITableViewCell cell, NSIndexPath indexPath, Canvas canvas) {
        // Draw Line Divider top of cell (first in section)
        final int startX = cell.getLeft();
        final int stopX = cell.getRight();
        final int startY = cell.getTop();
        canvas.drawLine(startX, startY, stopX, startY, mSeparatorPaint);
    }
    
    private void drawSectionFooterSeparator(UITableViewCell cell, NSIndexPath indexPath, Canvas canvas) {
        // Draw Line Divider top of cell (first in section)
        final int startX = cell.getLeft();
        final int stopX = cell.getRight();
        final int startY = cell.getBottom();
        canvas.drawLine(startX, startY, stopX, startY, mSeparatorPaint);
    }
    
}
