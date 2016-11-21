package com.loyo.oa.pulltorefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.loyo.oa.v2.R;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;

/**
 * com.loyo.oa.pulltorefresh.internal
 * 描述 :gif动画加载视图
 * 作者 : ykb
 * 时间 : 15/11/3.
 */
public class AutoRoteLoadingLayout extends LoadingLayout {


    public AutoRoteLoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
    }

    protected void onPullImpl(float scaleOfLayout) {
    }

    @Override
    protected void refreshingImpl() {
        mHeaderImage.startAuto();
    }

    @Override
    protected void resetImpl() {
        resetImageRotation();
    }

    private void resetImageRotation() {
        mHeaderImage.stopAuto();
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.icon_loading;
    }
}
