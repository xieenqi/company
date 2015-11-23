package com.loyo.oa.v2.tool.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * com.loyo.oa.v2.tool.customview
 * 描述 :不响应手势的Viewpager
 * 作者 : ykb
 * 时间 : 15/9/9.
 */
public class NoScrollViewpager extends ViewPager {

    public NoScrollViewpager(Context context)
    {
        super(context);
    }

    public NoScrollViewpager(Context context,AttributeSet attributeSet)
    {
        super(context,attributeSet);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
