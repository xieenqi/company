package com.loyo.oa.v2.tool.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

/**
 * Created by Administrator on 2014/11/1 0001.
 */
public class GridView_inScrollView extends GridView {
    private Boolean isSetHigh = true;

    public GridView_inScrollView(Context context) {
        super(context);
    }

    public GridView_inScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridView_inScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Boolean getIsSetHigh() {
        return isSetHigh;
    }

    public void setIsSetHigh(Boolean isSetHigh) {
        this.isSetHigh = isSetHigh;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("GridView_inScrollView", "onMeasure widthMeasureSpec:" + widthMeasureSpec + ",heightMeasureSpec:" + heightMeasureSpec);
        if (isSetHigh) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            Log.d("GridView_inScrollView", "onMeasure expandSpec:" + expandSpec);
            super.onMeasure(widthMeasureSpec, expandSpec);

        } else {
            // isInit=false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
