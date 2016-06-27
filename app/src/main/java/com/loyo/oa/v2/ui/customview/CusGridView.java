package com.loyo.oa.v2.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 【GridView】自定义 适配ScrollView
 * Created by yyy on 16/6/15.
 */
public class CusGridView extends GridView {
    public CusGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CusGridView(Context context) {
        super(context);
    }
    public CusGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}