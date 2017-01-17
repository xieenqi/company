package com.loyo.oa.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * com.loyo.oa.v2.customview.pullToRefresh
 * 描述 :下拉刷新Recycleview
 * 作者 : ykb
 * 时间 : 15/11/2.
 */
public class PullToRefreshRecycleView extends PullToRefreshBase<RecyclerView>{

    public PullToRefreshRecycleView(Context context) {
        super(context);
    }

    public PullToRefreshRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecycleView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecycleView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    //重写4个方法
    //1 滑动方向
    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    //重写4个方法
    //2  滑动的View
    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView view = new RecyclerView(context, attrs);
        return view;
    }

    //重写4个方法
    //3 是否滑动到底部
    @Override
    protected boolean isReadyForPullEnd() {
//        View view = getRefreshableView().getChildAt(getRefreshableView().getChildCount() - 1);
//        if (null != view) {
//            return getRefreshableView().getBottom() >= view.getBottom();
//        }
//        return false;
        return !getRefreshableView().canScrollVertically(1);
    }

    //重写4个方法
    //4 是否滑动到顶部
    @Override
    protected boolean isReadyForPullStart() {
        View view = getRefreshableView().getChildAt(0);

        if (view != null) {
            return view.getTop() >= getRefreshableView().getTop();
        }
        return false;
    }
}
