package com.loyo.oa.v2.common.commonview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by yyy on 16/1/21.
 *
 * GridView高度自适应
 */
public class NoScroolGridView extends GridView {

    public NoScroolGridView(Context context) {
        super(context);
    }

    public NoScroolGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScroolGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //不出现滚动条
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}