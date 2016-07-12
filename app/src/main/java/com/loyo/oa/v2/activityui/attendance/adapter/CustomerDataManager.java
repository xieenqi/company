package com.loyo.oa.v2.activityui.attendance.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by loyo_dev1 on 16/7/11.
 */
public class CustomerDataManager extends StaggeredGridLayoutManager {

    private double speedRatio;

    public CustomerDataManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomerDataManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int a = super.scrollHorizontallyBy((int)(speedRatio*dx), recycler, state);//屏蔽之后无滑动效果，证明滑动的效果就是由这个函数实现
        if(a == (int)(speedRatio*dx)){
            return dx;
        }
        return a;
    }

    public void setSpeedRatio(double speedRatio){
        this.speedRatio = speedRatio;
    }
}
