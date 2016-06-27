package com.loyo.oa.v2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * com.loyo.oa.v2.customview
 * 描述 :寬高适应子view数量的GridView
 * 作者 : ykb
 * 时间 : 15/9/14.
 */
public class MyGridView extends GridView {

    public boolean hasScrollBar = true;

    /**
     * @param context
     */
    public MyGridView(Context context) {
        this(context, null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = heightMeasureSpec = 0;
        if (hasScrollBar) {
            expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);// 注意这里,这里的意思是直接测量出GridView的高度
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}