package com.loyo.oa.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;

/**
 * Created by EthanGong on 2016/11/22.
 */

public class LYGridLayout extends GridLayout {
    public LYGridLayout(Context context) {
        super(context);
    }

    public LYGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LYGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int column = getColumnCount();
        int width = getMeasuredWidth();
        if (column != 0) {
            width = width/column;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            GridLayout.LayoutParams lp = (GridLayout.LayoutParams)child.getLayoutParams();
            lp.width = width;
            lp.height = width;
            child.requestLayout();
        }
    }
}
